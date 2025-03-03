package entity.tower

import entity.creature.alliance.{Alliance, Soldier01}
import entity.creature.enemy.Enemy
import entity.weapon.MagicBullet
import entity.{Direction, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.geom.AffineTransform
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

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

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(0), frameDuration = 10)
    shootAnimation = Animation(frames = value(1), frameDuration = 10, attackStartFrame = 4, attackEndFrame = 6)
    prepareAnimation = Animation(frames = value(2), frameDuration = 10)

  override def setUpImages(): Unit =
    this.images = AnimationFactory.createTowerAnimationMap(
      directions = Direction.allEntityDirections,
      idleAnim = idleAnimation,
      attackAnim = shootAnimation,
      prepareAnim = prepareAnimation
    )

  override def draw(g2d: Graphics2D): Unit =
    drawRangeCircle(g2d)
    bulletList.toList.foreach(_.draw(g2d))
    drawAnimationOrDefault(g2d)

  private def drawAnimationOrDefault(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform, centerCoords, offsetX, offsetY)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform, centerCoords, offsetX, offsetY)

  override protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy] =
    enemyList.sortBy(_.getHealth).lastOption

object ExploTower:
  val towerType = "Explo"
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ExploTower =
    new ExploTower(gp, level, s"ExploTower0$level", s"Explo0$level", pos, "ExploTower")

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

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(1), frameDuration = 10)
    shootAnimation = Animation(frames = value(4), frameDuration = 15, attackStartFrame = 2, attackEndFrame = 8)

  override def setUpImages(): Unit =
    this.images = AnimationFactory.createSimpleTowerAnimationMap(
      directions = Direction.allEntityDirections,
      idleAnim = idleAnimation,
      attackAnim = shootAnimation
    )

  override def bulletPosition: (Double, Double) =
    (centerCoords._1 + drawOffsetX, centerCoords._2 + drawOffsetY)

  override protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy] =
    enemyList.sortBy(_.getPosition._1).lastOption

object ArrowTower:
  val towerType = "Arrow"
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ArrowTower =
    new ArrowTower(gp, level, s"ArrowShooter0$level", s"Arrow0$level", pos, "ArrowTower")

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

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(0), frameDuration = 10)
    shootAnimation = Animation(frames = value(2), frameDuration = 10, attackStartFrame = 5, attackEndFrame = 8)

  override def setUpImages(): Unit =
    this.images = AnimationFactory.createSimpleTowerAnimationMap(
      directions = Direction.allEntityDirections,
      idleAnim = idleAnimation,
      attackAnim = shootAnimation
    )

  override def bulletPosition: (Double, Double) =
    (centerCoords._1 + drawOffsetX, centerCoords._2 + drawOffsetY)

  override protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy] =
    enemyList.sortBy(_.getAdDefense).headOption

object MagicTower:
  val towerType = "Magic"
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): MagicTower =
    new MagicTower(gp, level, "MagicWizard", MagicBullet.name, pos, "MagicTower")

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
  protected val imagePath: String = s"towers/BarrackTower$level.png"
  private val allianceType: String = Soldier01.name
  private val transform: AffineTransform = AffineTransform()
  private val triangleRadius: Double = 10.0
  private var triangleCenter: (Double, Double) = pos
  private val barrackTrainers: Vector[BarrackTrainer] = initializeTrainers()

  private val allianceWidth = 74
  private val allianceHeight = 42

  override def update(): Unit =
    barrackTrainers.foreach(_.update())
    updateSoldiers()

  override def draw(g2d: Graphics2D): Unit =
    Tools.drawFrame(g2d, towerImage, transform, centerCoords, offsetX, offsetY)
    drawSoldiers(g2d)

  private def initializeTrainers(): Vector[BarrackTrainer] =
    val rad: Vector[Double] = Vector(0,120,240).map(Math.toRadians(_))
    rad.map (rad =>
      val x = triangleCenter._1 + triangleRadius * Math.cos(rad) - allianceWidth/2
      val y = triangleCenter._2 + triangleRadius * Math.sin(rad) - allianceHeight/2
      BarrackTrainer(x, y)
    )

  private def updateSoldiers(): Unit =
    barrackTrainers.flatMap(_.getCurrentSoldier).foreach(_.update())

  private def drawSoldiers(g2d: Graphics2D): Unit =
    barrackTrainers.flatMap(_.getCurrentSoldier).foreach(_.draw(g2d))

  override protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy] =
    enemyList.headOption

  def moveTriangleTo(newCenter: (Double, Double)): Unit =
    triangleCenter = newCenter
    updateAlliancePositions()

  private def updateAlliancePositions(): Unit =
    val rad: Vector[Double] = Vector(0,120,240).map(Math.toRadians(_))
    barrackTrainers.zip(rad).foreach((trainer, rad) =>
      val x = triangleCenter._1 + triangleRadius * Math.cos(rad) - allianceWidth/2
      val y = triangleCenter._2 + triangleRadius * Math.sin(rad) - allianceHeight/2
      trainer.pos = (x,y)
      trainer.getCurrentSoldier.foreach(_.followPath((x, y)))
    )

  class BarrackTrainer(var pos: (Double, Double)):
    private val soldierTrainingTime = 10 * 60
    private var trainingCounter = soldierTrainingTime
    private var currentSoldier: Option[Alliance] = Alliance.allianceOfName(allianceType, pos)

    def getCurrentSoldier: Option[Alliance] = currentSoldier

    def update(): Unit =
      currentSoldier match
        case Some(soldier) => checkSoldierStatus(soldier)
        case None => startTraining()

    private def checkSoldierStatus(soldier: Alliance): Unit =
      if soldier.hasDie then
        currentSoldier = None

    private def startTraining(): Unit =
      if trainingCounter <= 0 then
        currentSoldier = Alliance.allianceOfName(allianceType, pos)
        trainingCounter = soldierTrainingTime
      else
        trainingCounter -= 1

  end BarrackTrainer

  override protected def setUpImages(): Unit = {}
  override protected def parseInformation(value: Vector[Vector[BufferedImage]]): Unit = {}
  override protected def parse(): Unit = {}

object BarrackTower:
  val towerType = "Barrack"
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): BarrackTower =
    new BarrackTower(gp, level, s"BarrackTower0$level", pos, "BarrackTower")

object AnimationFactory:
  def createTowerAnimationMap(
    directions: Seq[Direction],
    idleAnim: Animation,
    attackAnim: Animation,
    prepareAnim: Animation
  ): Map[(Direction, State), Animation] =
    Tools.fillMap(directions, State.IDLE, idleAnim) ++
    Tools.fillMap(directions, State.ATTACK, attackAnim) ++
    Tools.fillMap(directions, State.PREPARE, prepareAnim)

  def createSimpleTowerAnimationMap(
    directions: Seq[Direction],
    idleAnim: Animation,
    attackAnim: Animation
  ): Map[(Direction, State), Animation] =
    Tools.fillMap(directions, State.IDLE, idleAnim) ++
    Tools.fillMap(directions, State.PREPARE, idleAnim) ++
    Tools.fillMap(directions, State.ATTACK, attackAnim)