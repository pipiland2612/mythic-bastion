package entity.tower

import entity.creature.alliance.{Alliance, Soldier01}
import entity.weapon.{Arrow, Explo, MagicBullet}
import entity.{Direction, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.geom.AffineTransform
import java.awt.{Color, Graphics2D}
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
  val maxAttackCoolDown: Double = 0
) extends Tower(gp, level):

  protected val jsonPath: String = s"towers/ExploTower$level.json"
  protected val imagePath: String = s"towers/ExploTower$level.png"
  private var prepareAnimation: Animation = _
  private val transform: AffineTransform = AffineTransform()

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    shootAnimation = Animation(value(1), 10, 4, 6)
    prepareAnimation = Animation(value(2), 10)

  override def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, shootAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.PREPARE, prepareAnimation)

  override def draw(g2d: Graphics2D): Unit =
    if isShowingRange then
      g2d.setColor(Color.RED)
      g2d.draw(attackCircle)
    bulletList.toList.foreach(_.draw(g2d))
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform,
          centerCoords, offsetX, offsetY)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform,
          centerCoords, offsetX, offsetY)
//    gp.getSystemHandler.grid.draw(g2d, this)

object ExploTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ExploTower =
    new ExploTower(gp, level, s"ExploTower0$level", Explo.name, pos, s"ExploTower")

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
  val maxAttackCoolDown: Double = 0
) extends Tower(gp, level):

  protected val imagePath: String = s"towers/ArrowShooter0$level.png"
  protected val jsonPath: String = s"towers/ArrowShooter0$level.json"

  private var idleDownAnimation: Animation =_
  private var shootEndAnimation: Animation =_
  private var shootDownAnimation: Animation =_
  private var shootDownEndAnimation: Animation =_
  private val transform: AffineTransform = AffineTransform()

  override val offsetX: Double = -30
  override val offsetY: Double = -30
  override val drawOffsetX: Double = 2
  override val drawOffsetY: Double = -16

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
//    idleDownAnimation = Animation(value(0), 10)
    idleAnimation = Animation(value(1), 10)
//    shootDownAnimation = Animation(value(2), 10)
//    shootDownEndAnimation = Animation(value(3), 10)
    shootAnimation =  Animation(value(4), 15, 2, 8)
//    shootEndAnimation = Animation(value(5), 10)

  override def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.PREPARE, idleAnimation) ++
//      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleDownAnimation) ++
//      Tools.fillMap(upAnimation, State.ATTACK, shootAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, shootAnimation)
  
  override def bulletPosition: (Double, Double) = (centerCoords._1 + drawOffsetX, centerCoords._2 + drawOffsetY)

object ArrowTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ArrowTower =
    new ArrowTower(gp, level, s"ArrowShooter0$level", Arrow.name, pos, s"ArrowTower")

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
  val maxAttackCoolDown: Double = 0
) extends Tower(gp, level):

  protected val jsonPath: String = s"towers/MagicWizard.json"
  protected val imagePath: String = s"towers/MagicWizard.png"
  private var idleDownAnimation: Animation =_
  private var shootDownAnimation: Animation =_
  private val transform: AffineTransform = AffineTransform()

  private var shootEndAnimation: Animation = _

  override val offsetX: Double = -25
  override val offsetY: Double = -30
  override val drawOffsetX: Double = 2
  override val drawOffsetY: Double = -23

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
//    idleDownAnimation = Animation(value(0), 10)
    idleAnimation = Animation(value(0), 10)
    shootAnimation = Animation(value(2), 10, 5, 8)
//    shootAnimation =  Animation(value(3), 10, 5, 8)

  override def setUpImages(): Unit =
    val downAnimation: Seq[Direction] = Seq(Direction.RIGHT, Direction.DOWN)
    val upAnimation: Seq[Direction] = Seq(Direction.LEFT, Direction.UP)
    this.images =
//      Tools.fillMap(downAnimation, State.IDLE, idleDownAnimation) ++
//      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, shootAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.PREPARE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, shootAnimation)

  override def bulletPosition: (Double, Double) = (centerCoords._1 + drawOffsetX, centerCoords._2 + drawOffsetY)

object MagicTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): MagicTower =
    new MagicTower(gp, level, s"MagicWizard", MagicBullet.name, pos, s"MagicTower")


class BarrackTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  var pos: (Double, Double),
  val towerImagePath: String,
) extends Tower(gp, level):

  val weaponType: String = ""
  val maxAttackCounter: Int = 0
  val maxPrepareCounter: Int = 0
  val maxAttackCoolDown: Double = 0
  val range: Double = 120

  protected val jsonPath: String = s"towers/BarrackTower$level.json"
  protected val imagePath: String = s"towers/BarrackTower$level.png"
  private val allianceType: String = Soldier01.name
  private var prepareAnimation: Animation = _
  private val transform: AffineTransform = AffineTransform()
  private val barrackTrainers: Vector[BarrackTrainer] =
    Vector(BarrackTrainer((pos._1 + 10, pos._2 + 10)), BarrackTrainer((pos._1 + 5, pos._2 + 10)), BarrackTrainer((pos._1 + 7, pos._2 + 15)))

  override def update(): Unit =
    barrackTrainers.foreach(_.update())
    // Update soldier
    barrackTrainers.flatMap(_.getCurrentSoldier).foreach(_.update())

  override def draw(g2d: Graphics2D): Unit =
    Tools.drawFrame(g2d, towerImage, transform, centerCoords, offsetX, offsetY)
    barrackTrainers.flatMap(_.getCurrentSoldier).foreach(_.draw(g2d))

  class BarrackTrainer (var pos: (Double, Double)):
    private val soldierTrainingTime = 10 * 60 // 10 seconds
    private var trainingCounter = soldierTrainingTime
    private var training: Boolean = false
    private var currentSoldier: Option[Alliance] = Alliance.allianceOfName(allianceType, pos)

    def getCurrentSoldier: Option[Alliance] = currentSoldier
    def update(): Unit =
      currentSoldier match
        case Some(soldier) =>
          if soldier.getHealth <= 0 then currentSoldier = None
        case None => startTraining()

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

object BarrackTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): BarrackTower =
    new BarrackTower(gp, level, s"BarrackTower0$level", pos, s"BarrackTower")
