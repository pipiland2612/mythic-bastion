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

  var shootEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    shootAnimation = Animation(value(1), 10, 4, 6)
    shootEndAnimation = Animation(value(2), 10)

  override def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, shootAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.PREPARE, shootEndAnimation)

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