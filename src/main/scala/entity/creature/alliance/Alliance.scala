package entity.creature.alliance

import entity.{Direction, State}
import entity.creature.Creature
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

abstract class Alliance(gp: GamePanel) extends Creature(gp):
  protected val maxHealth: Double
  protected var health: Double
  scaleFactor = 1.25

  direction = Direction.LEFT
  needsAnimationUpdate = true

  override def setUpImages(): Unit =
    val mirroredDirections = Seq(Direction.LEFT, Direction.UP_LEFT, Direction.DOWN_LEFT)
    val nonMirroredDirections = Direction.allCreatureDirections.diff(mirroredDirections)

    images = AnimationFactory.createAnimationMap(
      mirroredDirections = mirroredDirections,
      nonMirroredDirections = nonMirroredDirections,
      idleAnim = idleAnimation,
      walkAnim = walkingAnimation,
      fightAnim = fightingAnimation,
      deadAnim = deadAnimation
    )

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), frameDuration = 10)
    walkingAnimation = Animation(value(1), frameDuration = 10)
    fightingAnimation = Animation(value(2), frameDuration = 10)
    deadAnimation = Animation(value(3), frameDuration = 10)

  override protected def findEnemy[T <: Creature](): ListBuffer[T] =
    gp.getSystemHandler.getGrid
      .scanForEnemiesInRange(this)
      .asInstanceOf[ListBuffer[T]]

  override def update(): Unit =
    super.update()
    updateGridPosition()
    setAction()
    handleAttackAnimation()
    checkHealthStatus()

  private def updateGridPosition(): Unit =
    gp.getSystemHandler.getGrid.updateCreaturePosition(
      this,
      (lastPosition._1.toInt, lastPosition._2.toInt)
    )

  private def checkHealthStatus(): Unit =
    if health <= 0 then
      gp.getSystemHandler.getGrid.remove(this)

object Alliance:
  private var gp: GamePanel = _

  def setUp(gp: GamePanel): Unit = this.gp = gp

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
        gp = gp
      )
      soldier.setPosition(position)
      soldier
    )

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
      gp = gp
    )

object AnimationFactory:
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

object AllianceData:
  case class AllianceConfig(
   stats: Vector[Double],
   jsonPath: String,
   imagePath: String,
   rect: Rectangle2D
  )

  val registry: Map[String, AllianceConfig] = Map(
    Helper01.name -> AllianceConfig(Helper01.data, Helper01.jsonPath, Helper01.imagePath, Helper01.rect),
    Helper02.name -> AllianceConfig(Helper02.data, Helper02.jsonPath, Helper02.imagePath, Helper02.rect),
    Soldier01.name -> AllianceConfig(Soldier01.data, Soldier01.jsonPath, Soldier01.imagePath, Soldier01.rect)
  )