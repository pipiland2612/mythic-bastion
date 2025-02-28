package entity.tower

import entity.weapon.{Arrow, Explo, MagicBullet}
import entity.{Direction, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.geom.AffineTransform
import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

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
  private var prepareCounter: Int = 0

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    shootAnimation = Animation(value(1), 10, 4, 6)
    prepareAnimation = Animation(value(2), 10)

  override def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, shootAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.PREPARE, prepareAnimation)

  private def handlePrepareState(): Unit =
    if this.state == State.PREPARE then
      prepareCounter += 1
      if prepareCounter >= maxPrepareCounter then
        currentAnimation.foreach(_.reset())
        prepareCounter = 0
        this.state = State.IDLE
      needsAnimationUpdate = true

  override def update(): Unit =
    super.update()
    handlePrepareState()

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
  val maxAttackCounter: Int = 20,
  val maxPrepareCounter: Int = 10,
  val maxAttackCoolDown: Double = 1 * 60
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
    idleDownAnimation = Animation(value(0), 10)
    idleAnimation = Animation(value(1), 10)
    shootDownAnimation = Animation(value(2), 10)
    shootDownEndAnimation = Animation(value(3), 10)
    shootAnimation =  Animation(value(4), 10)
    shootEndAnimation = Animation(value(5), 10)

  override def setUpImages(): Unit =
    val upAnimation: Seq[Direction] = Seq(Direction.LEFT, Direction.UP)
    val downAnimation: Seq[Direction] = Seq(Direction.RIGHT, Direction.DOWN)
    this.images =
      Tools.fillMap(upAnimation, State.IDLE, idleAnimation) ++
      Tools.fillMap(downAnimation, State.IDLE, idleDownAnimation) ++
      Tools.fillMap(upAnimation, State.ATTACK, shootAnimation) ++
      Tools.fillMap(downAnimation, State.ATTACK, shootDownAnimation)

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
  val range: Double = 100,
  val maxAttackCounter: Int = 70,
  val maxPrepareCounter: Int = 70,
  val maxAttackCoolDown: Double = 2 * 60
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
    idleDownAnimation = Animation(value(0), 10)
    idleAnimation = Animation(value(1), 10)
    shootDownAnimation = Animation(value(2), 10)
    shootAnimation =  Animation(value(3), 10)

  override def setUpImages(): Unit =
    val downAnimation: Seq[Direction] = Seq(Direction.RIGHT, Direction.DOWN)
    val upAnimation: Seq[Direction] = Seq(Direction.LEFT, Direction.UP)
    this.images =
      Tools.fillMap(downAnimation, State.IDLE, idleDownAnimation) ++
      Tools.fillMap(upAnimation, State.ATTACK, shootAnimation) ++
      Tools.fillMap(upAnimation, State.IDLE, idleAnimation) ++
      Tools.fillMap(downAnimation, State.ATTACK, shootDownAnimation)

object MagicTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): MagicTower =
    new MagicTower(gp, level, s"MagicWizard", MagicBullet.name, pos, s"MagicTower")