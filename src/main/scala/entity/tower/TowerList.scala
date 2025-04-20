package entity.tower

import entity.creature.alliance.{Alliance, Soldier01}
import entity.creature.enemy.Enemy
import entity.weapon.MagicBullet
import entity.{Direction, State}
import game.GamePanel
import system.upgrade.UpgradeTowerType.{ARROW, BARRACK, EXPLO, MAGE}
import system.upgrade.UpgradeType.{HEALTH, RANGE}
import utils.{Animation, SoundConstant, Tools}

import java.awt.geom.AffineTransform
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

/** A tower that fires explosive projectiles at enemies, prioritizing those with the lowest health.
 *
 * The ExploTower supports idle, shooting, and preparation animations, with a range modified by upgrades.
 */
class ExploTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  val weaponType: String,
  var pos: (Double, Double),
  val towerImagePath: String,
  val range: Double = 120,
  val maxAttackCounter: Int = 100,
  val maxPrepareCounter: Int = 70,
  val maxAttackCoolDown: Double = 0,
  val towerType: String = ExploTower.towerType
) extends Tower(gp, level):
  protected val jsonPath: String = s"towers/ExploTower$level.json"
  protected val imagePath: String = s"towers/ExploTower$level.png"
  private var prepareAnimation: Animation = _
  private val transform: AffineTransform = AffineTransform()

  protected val readySoundEffect: Array[String] = Array(SoundConstant.EXPLO_READY1, SoundConstant.EXPLO_READY2, SoundConstant.EXPLO_READY3)
  protected def getRangeMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(EXPLO, RANGE)

  /** Parses animation data from a vector of frame sequences.
   *
   * Assigns idle, shoot, and prepare animations based on the provided frame sequences.
   *
   * @param value A Vector of Vector[BufferedImage] containing frame sequences for each animation.
   */
  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(0), frameDuration = 10)
    shootAnimation = Animation(frames = value(1), frameDuration = 10, attackStartFrame = 4, attackEndFrame = 6)
    prepareAnimation = Animation(frames = value(2), frameDuration = 10)

  /** Sets up the animation map for the tower.
   *
   * Creates a map associating directions and states (idle, attack, prepare) with their respective animations.
   */
  override def setUpImages(): Unit =
    this.images = AnimationFactory.createTowerAnimationMap(
      directions = Direction.allEntityDirections,
      idleAnim = idleAnimation,
      attackAnim = shootAnimation,
      prepareAnim = prepareAnimation
    )

  /** Draws the tower, its attack range, bullets, and current animation.
   *
   * Renders the attack range circle, active bullets, and either the current animation or the idle animation as a fallback.
   *
   * @param g2d The Graphics2D context for rendering.
   */
  override def draw(g2d: Graphics2D): Unit =
    drawRangeCircle(g2d)
    bulletList.toList.foreach(_.draw(g2d))
    drawAnimationOrDefault(g2d)

  /** Draws the current animation or defaults to the idle animation.
   *
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawAnimationOrDefault(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform, centerCoords, offsetX, offsetY)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform, centerCoords, offsetX, offsetY)

  /** Chooses the enemy with the lowest health from the list.
   *
   * @param enemyList A ListBuffer of Enemy objects within range.
   * @return An Option containing the selected Enemy, or None if the list is empty.
   */
  override protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy] =
    enemyList.sortBy(_.getHealth).headOption

object ExploTower:
  val towerType = "Explo"
  private val prices: Map[Int, Int] = Map(
    1 -> 125,
    2 -> 220,
    3 -> 320
  )

  def updatePrice(currentLevel: Int): Option[Int] =
    prices.get(currentLevel + 1)

  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ExploTower =
    new ExploTower(gp, level, s"ExploTower0$level", s"Explo0$level", pos, "ExploTower")

/** A tower that shoots arrows at enemies, targeting the one furthest along the path.
 *
 * The ArrowTower supports idle and shooting animations, with a range modified by upgrades.
 */
class ArrowTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  val weaponType: String,
  var pos: (Double, Double),
  val towerImagePath: String,
  val range: Double = 200,
  val maxAttackCounter: Int = 45,
  val maxPrepareCounter: Int = 70,
  val maxAttackCoolDown: Double = 0,
  val towerType: String = ArrowTower.towerType
) extends Tower(gp, level):
  protected val imagePath: String = s"towers/ArrowShooter0$level.png"
  protected val jsonPath: String = s"towers/ArrowShooter0$level.json"
  private val transform: AffineTransform = AffineTransform()

  override val offsetX: Double = -30
  override val offsetY: Double = -30
  override val drawOffsetX: Double = 2
  override val drawOffsetY: Double = -16

  protected val readySoundEffect: Array[String] = Array(SoundConstant.ARROW_READY1, SoundConstant.ARROW_READY3)
  protected def getRangeMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(ARROW, RANGE)

  /** Parses animation data from a vector of frame sequences.
   *
   * Assigns idle and shoot animations based on the provided frame sequences.
   *
   * @param value A Vector of Vector[BufferedImage] containing frame sequences for each animation.
   */
  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(1), frameDuration = 10)
    shootAnimation = Animation(frames = value(4), frameDuration = 15, attackStartFrame = 2, attackEndFrame = 8)

  /** Sets up the animation map for the tower.
   *
   * Creates a map associating directions and states (idle, attack) with their respective animations, using idle for prepare state.
   */
  override def setUpImages(): Unit =
    this.images = AnimationFactory.createSimpleTowerAnimationMap(
      directions = Direction.allEntityDirections,
      idleAnim = idleAnimation,
      attackAnim = shootAnimation
    )

  /** Calculates the position from which bullets are fired.
   *
   * @return A tuple of (x, y) coordinates adjusted by draw offsets.
   */
  override def bulletPosition: (Double, Double) =
    (centerCoords._1 + drawOffsetX, centerCoords._2 + drawOffsetY)

  /** Chooses the enemy furthest along the x-axis from the list.
   *
   * @param enemyList A ListBuffer of Enemy objects within range.
   * @return An Option containing the selected Enemy, or None if the list is empty.
   */
  override protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy] =
    enemyList.sortBy(_.getPosition._1).lastOption

object ArrowTower:
  val towerType = "Arrow"
  private val prices: Map[Int, Int] = Map(
    1 -> 70,
    2 -> 110,
    3 -> 160,
    4 -> 230,
  )

  def updatePrice(currentLevel: Int): Option[Int] =
    prices.get(currentLevel + 1)

  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ArrowTower =
    new ArrowTower(gp, level, s"ArrowShooter0$level", s"Arrow0$level", pos, "ArrowTower")

/** A tower that casts magic bullets, targeting enemies with the lowest defense.
 *
 * The MagicTower supports idle and shooting animations, with a range modified by upgrades.
 */
class MagicTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  val weaponType: String,
  var pos: (Double, Double),
  val towerImagePath: String,
  val range: Double = 110,
  val maxAttackCounter: Int = 70,
  val maxPrepareCounter: Int = 70,
  val maxAttackCoolDown: Double = 0,
  val towerType: String = MagicTower.towerType
) extends Tower(gp, level):
  protected val jsonPath: String = s"towers/MagicWizard.json"
  protected val imagePath: String = s"towers/MagicWizard.png"
  private val transform: AffineTransform = AffineTransform()

  override val offsetX: Double = -25
  override val offsetY: Double = -30
  override val drawOffsetX: Double = 2
  override val drawOffsetY: Double = -23
  protected val readySoundEffect: Array[String] = Array(SoundConstant.MAGIC_READY1, SoundConstant.MAGIC_READY2, SoundConstant.MAGIC_READY3)

  protected def getRangeMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(MAGE, RANGE)

  /** Parses animation data from a vector of frame sequences.
   *
   * Assigns idle and shoot animations based on the provided frame sequences.
   *
   * @param value A Vector of Vector[BufferedImage] containing frame sequences for each animation.
   */
  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(0), frameDuration = 10)
    shootAnimation = Animation(frames = value(2), frameDuration = 10, attackStartFrame = 5, attackEndFrame = 8)

  /** Sets up the animation map for the tower.
   *
   * Creates a map associating directions and states (idle, attack) with their respective animations, using idle for prepare state.
   */
  override def setUpImages(): Unit =
    this.images = AnimationFactory.createSimpleTowerAnimationMap(
      directions = Direction.allEntityDirections,
      idleAnim = idleAnimation,
      attackAnim = shootAnimation
    )

  /** Calculates the position from which bullets are fired.
   *
   * @return A tuple of (x, y) coordinates adjusted by draw offsets.
   */
  override def bulletPosition: (Double, Double) =
    (centerCoords._1 + drawOffsetX, centerCoords._2 + drawOffsetY)

  /** Chooses the enemy with the lowest attack defense from the list.
   *
   * @param enemyList A ListBuffer of Enemy objects within range.
   * @return An Option containing the selected Enemy, or None if the list is empty.
   */
  override protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy] =
    enemyList.sortBy(_.getAdDefense).headOption

object MagicTower:
  val towerType = "Magic"
  private val prices: Map[Int, Int] = Map(
    1 -> 100,
    2 -> 160,
    3 -> 240,
    4 -> 300,
  )

  def updatePrice(currentLevel: Int): Option[Int] =
    prices.get(currentLevel + 1)

  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): MagicTower =
    new MagicTower(gp, level, "MagicWizard", MagicBullet.name, pos, "MagicTower")

/** A tower that trains and manages soldiers positioned in a triangular formation.
 *
 * The BarrackTower does not fire projectiles but instead spawns and updates soldiers, which can be repositioned
 * by moving the triangular formation.
 */
class BarrackTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  var pos: (Double, Double),
  val towerImagePath: String,
  val towerType: String = BarrackTower.towerType,
  val range: Double = 110
) extends Tower(gp, level):
  val weaponType: String = ""
  val maxAttackCounter: Int = 0
  val maxPrepareCounter: Int = 0
  val maxAttackCoolDown: Double = 0

  protected val jsonPath: String = s"towers/BarrackTower$level.json"
  protected val imagePath: String = s"towers/BarrackTrainer$level.png"
  private val allianceType: String = Soldier01.name
  private val transform: AffineTransform = AffineTransform()
  private val triangleRadius: Double = 10.0
  private var triangleCenter: (Double, Double) = pos
  private val barrackTrainers: Vector[BarrackTrainer] = initializeTrainers()
  protected val readySoundEffect: Array[String] = Array()

  private val allianceWidth = 74
  private val allianceHeight = 42

  protected def getRangeMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(BARRACK, RANGE)
  private def getHealthMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(BARRACK, HEALTH) + level * 0.3

  /** Removes all soldiers associated with the tower from the grid.
   *
   * Ensures that soldiers are removed from the stage's grid when the tower is destroyed or removed.
   */
  def removeAllAlliance(): Unit =
    barrackTrainers.flatMap(_.getCurrentSoldier).foreach(alliance =>
      gp.getSystemHandler.getStageManager.getGrid match
        case Some(grid) => grid.remove(alliance)
        case _ =>
    )

  /** Updates the tower by updating its trainers and soldiers.
   *
   * Calls update on each trainer and ensures all active soldiers are updated.
   */
  override def update(): Unit =
    barrackTrainers.foreach(_.update())
    updateSoldiers()

  /** Draws the tower image and its soldiers.
   *
   * Renders the tower at its position and draws all active soldiers managed by the trainers.
   *
   * @param g2d The Graphics2D context for rendering.
   */
  override def draw(g2d: Graphics2D): Unit =
    Tools.drawFrame(g2d, towerImage, transform, centerCoords, offsetX, offsetY)
    drawSoldiers(g2d)

  /** Initializes the trainers for soldier spawning.
   *
   * Creates three trainers positioned in a triangular formation around the tower's center.
   *
   * @return A Vector of BarrackTrainer objects.
   */
  private def initializeTrainers(): Vector[BarrackTrainer] =
    val rad: Vector[Double] = Vector(0, 120, 240).map(Math.toRadians(_))
    rad.map(rad =>
      val x = triangleCenter._1 + triangleRadius * Math.cos(rad) - allianceWidth / 2
      val y = triangleCenter._2 + triangleRadius * Math.sin(rad) - allianceHeight / 2
      BarrackTrainer(x, y)
    )

  /** Updates all active soldiers.
   *
   * Calls update on each soldier managed by the trainers.
   */
  private def updateSoldiers(): Unit =
    barrackTrainers.flatMap(_.getCurrentSoldier).foreach(_.update())

  /** Draws all active soldiers.
   *
   * Renders each soldier managed by the trainers.
   *
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawSoldiers(g2d: Graphics2D): Unit =
    barrackTrainers.flatMap(_.getCurrentSoldier).foreach(_.draw(g2d))

  /** Chooses the first enemy from the list.
   *
   * @param enemyList A ListBuffer of Enemy objects within range.
   * @return An Option containing the selected Enemy, or None if the list is empty.
   */
  override protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy] =
    enemyList.headOption

  /** Moves the triangular formation of soldiers to a new center position.
   *
   * Updates the trainer positions and instructs soldiers to follow the new positions.
   *
   * @param newCenter The new (x, y) center for the triangular formation.
   */
  def moveTriangleTo(newCenter: (Double, Double)): Unit =
    triangleCenter = newCenter
    updateAlliancePositions()

  /** Updates the positions of trainers and their soldiers.
   *
   * Repositions trainers in a triangular formation around the new center and updates soldier paths accordingly.
   */
  private def updateAlliancePositions(): Unit =
    val rad: Vector[Double] = Vector(0, 120, 240).map(Math.toRadians(_))
    barrackTrainers.zip(rad).foreach((trainer, rad) =>
      val x = triangleCenter._1 + triangleRadius * Math.cos(rad) - allianceWidth / 2
      val y = triangleCenter._2 + triangleRadius * Math.sin(rad) - allianceHeight / 2
      trainer.pos = (x, y)
      trainer.getCurrentSoldier.foreach(_.followPath((x, y)))
    )

  /** Manages the training and spawning of a single soldier.
   *
   * Tracks training progress and spawns a new soldier when training is complete or replaces a dead soldier.
   *
   * @param pos The initial (x, y) position of the trainer.
   */
  private class BarrackTrainer(var pos: (Double, Double)):
    private val soldierTrainingTime = 10 * 60
    private var trainingCounter = soldierTrainingTime
    private var currentSoldier: Option[Alliance] = Alliance.allianceOfNameAndHealth(allianceType, pos, getHealthMultiplier)

    def getCurrentSoldier: Option[Alliance] = currentSoldier

    /** Updates the trainer's state.
     *
     * Checks the status of the current soldier and starts training a new one if necessary.
     */
    def update(): Unit =
      currentSoldier match
        case Some(soldier) => checkSoldierStatus(soldier)
        case None => startTraining()

    /** Checks if the current soldier is dead.
     *
     * Clears the current soldier if it has died.
     *
     * @param soldier The current Alliance soldier.
     */
    private def checkSoldierStatus(soldier: Alliance): Unit =
      if soldier.hasDie then
        currentSoldier = None

    /** Starts training a new soldier.
     *
     * Decrements the training counter and spawns a new soldier when training is complete.
     */
    private def startTraining(): Unit =
      if trainingCounter <= 0 then
        currentSoldier = Alliance.allianceOfNameAndHealth(allianceType, pos, getHealthMultiplier)
        trainingCounter = soldierTrainingTime
      else
        trainingCounter -= 1

  end BarrackTrainer

  override protected def setUpImages(): Unit = {}
  override protected def parseInformation(value: Vector[Vector[BufferedImage]]): Unit = {}
  override protected def parse(): Unit = {}

object BarrackTower:
  val towerType = "Barrack"
  private val prices: Map[Int, Int] = Map(
    1 -> 70,
    2 -> 110,
    3 -> 160,
    4 -> 230,
  )

  def updatePrice(currentLevel: Int): Option[Int] =
    prices.get(currentLevel + 1)

  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): BarrackTower =
    new BarrackTower(gp, level, s"BarrackTower0$level", pos, "BarrackTower")

/** Factory for creating animation maps for towers.
 *
 * Provides methods to generate animation maps associating directions and states with animations for different tower types.
 */
object AnimationFactory:
  /** Creates an animation map for towers with idle, attack, and prepare animations.
   *
   * @param directions The sequence of Directions to map.
   * @param idleAnim The Animation for the idle state.
   * @param attackAnim The Animation for the attack state.
   * @param prepareAnim The Animation for the prepare state.
   * @return A Map of (Direction, State) to Animation.
   */
  def createTowerAnimationMap(
    directions: Seq[Direction],
    idleAnim: Animation,
    attackAnim: Animation,
    prepareAnim: Animation
  ): Map[(Direction, State), Animation] =
    Tools.fillMap(directions, State.IDLE, idleAnim) ++
    Tools.fillMap(directions, State.ATTACK, attackAnim) ++
    Tools.fillMap(directions, State.PREPARE, prepareAnim)

  /** Creates an animation map for towers with idle and attack animations.
   *
   * Uses the idle animation for the prepare state.
   *
   * @param directions The sequence of Directions to map.
   * @param idleAnim The Animation for the idle and prepare states.
   * @param attackAnim The Animation for the attack state.
   * @return A Map of (Direction, State) to Animation.
   */
  def createSimpleTowerAnimationMap(
    directions: Seq[Direction],
    idleAnim: Animation,
    attackAnim: Animation
  ): Map[(Direction, State), Animation] =
    Tools.fillMap(directions, State.IDLE, idleAnim) ++
    Tools.fillMap(directions, State.PREPARE, idleAnim) ++
    Tools.fillMap(directions, State.ATTACK, attackAnim)