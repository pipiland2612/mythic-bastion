package entity.creature.alliance

import entity.{Direction, State}
import entity.creature.Creature
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

/** Abstract base class for alliance creatures in the game, handling movement and combat.
 * Extends Creature for movement, combat, and health management.
 * @param gp The GamePanel instance managing the game.
 */
abstract class Alliance(gp: GamePanel) extends Creature(gp):
  protected val maxHealth: Double
  protected var health: Double
  scaleFactor = 1.25
  direction = Direction.LEFT
  needsAnimationUpdate = true

  /** Sets up the animation map for the alliance, including mirrored animations for specific directions. */
  override def setUpImages(): Unit =
    val mirroredDirections = Seq(Direction.LEFT, Direction.UP_LEFT, Direction.DOWN_LEFT)
    val nonMirroredDirections = Direction.allCreatureDirections.diff(mirroredDirections)
    images = AllianceAnimationFactory.createAnimationMap(
      mirroredDirections = mirroredDirections,
      nonMirroredDirections = nonMirroredDirections,
      idleAnim = idleAnimation,
      walkAnim = walkingAnimation,
      fightAnim = fightingAnimation,
      deadAnim = deadAnimation
    )

  /** Parses animation frames for the alliance from a provided vector.
   * @param value A vector of vectors containing BufferedImage frames for different animations.
   */
  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(0), frameDuration = 10)
    walkingAnimation = Animation(frames = value(1), frameDuration = 10)
    fightingAnimation = Animation(frames = value(2), frameDuration = 10)
    deadAnimation = Animation(frames = value(3), frameDuration = 10)

  /** Finds enemy creatures within the alliance's attack range.
   * @return An optional list of enemy creatures in range.
   */
  override protected def findEnemy[T <: Creature](): Option[ListBuffer[T]] =
    gp.getSystemHandler.getStageManager.getGrid match
      case Some(grid) => Some(grid.scanForEnemiesInRange(this).asInstanceOf[ListBuffer[T]])
      case _ => None

  private var currentGoal: (Double, Double) = (-1, -1)

  /** Updates the alliance's state, including movement, attacks, and health status. */
  override def update(): Unit =
    super.update()
    setAction()
    handleAttackAnimation()
    updateGridPosition()
    checkHealthStatus()

  /** Determines the alliance's action, including movement or attacking. */
  override def setAction(): Unit =
    super.setAction()
    handleMovement()

  /** Manages the alliance's movement toward its current goal, if set. */
  private def handleMovement(): Unit =
    if this.state != State.ATTACK && this.currentGoal != (-1, -1) then
      val (xDist, yDist) = (currentGoal._1 - this.pos._1, currentGoal._2 - this.pos._2)
      val absX = Math.abs(xDist)
      val absY = Math.abs(yDist)
      if absX <= this.speed && absY <= this.speed then
        this.pos = this.currentGoal
      else
        direction = determineDirection(xDist, yDist)
        continueMove()

  /** Checks the alliance's health and removes it from the grid upon death. */
  private def checkHealthStatus(): Unit =
    if health <= 0 then
      removeGrid()

  /** Sets a new goal position for the alliance to move toward.
   * @param goal The target coordinates.
   */
  def followPath(goal: (Double, Double)): Unit =
    this.currentGoal = goal

/** Companion object for Alliance, handling alliance setup and instantiation. */
object Alliance:
  private var gp: GamePanel = _

  /** Sets up the GamePanel for alliance instantiation.
   * @param gp The GamePanel instance.
   */
  def setUp(gp: GamePanel): Unit = this.gp = gp

  /** Creates an alliance instance based on its name and position.
   * @param key The name of the alliance.
   * @param position The starting position of the alliance.
   * @return An optional Alliance instance.
   */
  def allianceOfName(key: String, position: (Double, Double)): Option[Alliance] =
    AllianceData.registry.get(key).map(data =>
      val soldier = Soldier(
        name = key,
        maxHealth = data.stats(0),
        apDmg = data.stats(1),
        apDefense = data.stats(2),
        adDmg = data.stats(3),
        adDefense = data.stats(4),
        range = data.stats(5),
        speed = data.stats(6),
        maxAttackCoolDown = data.stats(7),
        maxDeadCounter = data.stats(8),
        jsonPath = data.jsonPath,
        imagePath = data.imagePath,
        rect = data.rect,
        gp = gp,
        healthOffSet = data.healthOffSet
      )
      soldier.setPosition(position)
      soldier
    )

  /** Clones an existing alliance with the same attributes.
   * @param alliance The alliance to clone.
   * @return A new Alliance instance with identical attributes.
   */
  def clone(alliance: Alliance): Alliance =
    Soldier(
      name = alliance.getName,
      maxHealth = alliance.getMaxHealth,
      apDmg = alliance.getApDmg,
      apDefense = alliance.getApDefense,
      adDmg = alliance.getAdDmg,
      adDefense = alliance.getAdDefense,
      range = alliance.getRange,
      speed = alliance.getSpeed,
      maxAttackCoolDown = alliance.getMaxAttackCoolDown,
      maxDeadCounter = alliance.getMaxDeadCounter,
      jsonPath = alliance.getJsonPath,
      imagePath = alliance.getImagePath,
      rect = alliance.getRect,
      gp = gp,
      healthOffSet = alliance.getHealthOffSet
    )

/** Factory object for creating alliance animation maps. */
private object AllianceAnimationFactory:
  def createAnimationMap(
    mirroredDirections: Seq[Direction],
    nonMirroredDirections: Seq[Direction],
    idleAnim: Animation,
    walkAnim: Animation,
    fightAnim: Animation,
    deadAnim: Animation
  ): Map[(Direction, State), Animation] =
    Tools.fillMap(mirroredDirections, State.IDLE, Tools.flipAnimation(idleAnim)) ++
    Tools.fillMap(nonMirroredDirections, State.IDLE, idleAnim) ++
    Tools.fillMap(mirroredDirections, State.RUN, Tools.flipAnimation(walkAnim)) ++
    Tools.fillMap(nonMirroredDirections, State.RUN, walkAnim) ++
    Tools.fillMap(mirroredDirections, State.ATTACK, Tools.flipAnimation(fightAnim)) ++
    Tools.fillMap(nonMirroredDirections, State.ATTACK, fightAnim) ++
    Tools.fillMap(mirroredDirections, State.DEAD, Tools.flipAnimation(deadAnim)) ++
    Tools.fillMap(nonMirroredDirections, State.DEAD, deadAnim)

/** Data object for storing alliance configurations. */
object AllianceData:
  case class AllianceConfig(
   stats: Vector[Double],
   jsonPath: String,
   imagePath: String,
   rect: Rectangle2D,
   healthOffSet: (Int, Int)
  )

  /** Registry of alliance configurations, mapping alliance names to their configurations. */
  val registry: Map[String, AllianceConfig] = Map(
    Helper01.name -> AllianceConfig(Helper01.data, Helper01.jsonPath, Helper01.imagePath, Helper01.rect, Helper01.healthOffSet),
    Helper02.name -> AllianceConfig(Helper02.data, Helper02.jsonPath, Helper02.imagePath, Helper02.rect, Helper02.healthOffSet),
    Soldier01.name -> AllianceConfig(Soldier01.data, Soldier01.jsonPath, Soldier01.imagePath, Soldier01.rect, Soldier01.healthOffSet)
  )