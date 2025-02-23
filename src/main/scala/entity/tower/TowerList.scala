package entity.tower

import entity.weapon.{Arrow, Explo, MagicBullet, Weapon}
import entity.{Direction, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.image.BufferedImage

class ExploTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  val weaponType: String,
  var pos: (Double, Double),
  val range: Double = 120,
  val attackDuration: Int = 100,
  val prepareDuration: Int = 70,
) extends Tower(gp, level):

  protected val jsonPath: String = s"towers/ExploTower$level.json"
  protected val imagePath: String = s"towers/ExploTower$level.png"

  private val idleIndex = 0
  private val shootIndex = 1
  private val prepareIndex = 2
  private val frameDuration = 10
  private val attackFrame = (4,6)

  var prepareAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(idleIndex), frameDuration)
    shootAnimation = Animation(value(shootIndex), frameDuration, attackFrame._1, attackFrame._2)
    prepareAnimation = Animation(value(prepareIndex), frameDuration)

  override def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, shootAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.PREPARE, prepareAnimation)

object ExploTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ExploTower =
    new ExploTower(gp, level, s"ExploTower0$level", Explo.name, pos)

class ArrowTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  val weaponType: String,
  var pos: (Double, Double),
  val range: Double = 200,
  val attackDuration: Int = 100,
  val prepareDuration: Int = 70,
) extends Tower(gp, level):

  protected val jsonPath: String = s"towers/ArrowTower$level.json"
  protected val imagePath: String = s"towers/ArrowTower$level.png"

  var shootEndAnimation: Animation = _
  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit = {}
  override def setUpImages(): Unit = {}

object ArrowTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ArrowTower =
    new ArrowTower(gp, level, s"ArrowTower0$level", Arrow.name, pos)

class MagicTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  val weaponType: String,
  var pos: (Double, Double),
  val range: Double = 100,
  val attackDuration: Int = 100,
  val prepareDuration: Int = 70,
) extends Tower(gp, level):

  protected val jsonPath: String = s"towers/MagicTower$level.json"
  protected val imagePath: String = s"towers/MagicTower$level.png"

  var shootEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit = {}
  override def setUpImages(): Unit = {}

object MagicTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): MagicTower =
    new MagicTower(gp, level, s"MagicTower0$level", MagicBullet.name, pos)