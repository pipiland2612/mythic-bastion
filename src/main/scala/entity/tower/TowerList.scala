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
  val weaponType: Weapon,
  var pos: (Double, Double),
  val range: Double = 120,
  val maxAttackCoolDown: Double = 3 * 60
) extends Tower(gp, level):

  protected val jsonPath: String = s"towers/ExploTower$level.json"
  protected val imagePath: String = s"towers/ExploTower$level.png"

  var shootAnimation: Animation = _
  var shootEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    shootAnimation = Animation(value(1), 10)
    shootEndAnimation = Animation(value(2), 10)

  override def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, shootAnimation)


object ExploTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ExploTower =
    new ExploTower(gp, level, s"ExploTower0$level", Explo(gp), pos)

class ArrowTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  val weaponType: Weapon,
  var pos: (Double, Double),
  val range: Double = 200,
  val maxAttackCoolDown: Double = 1 * 60
) extends Tower(gp, level):

  protected val jsonPath: String = s"towers/ArrowTower$level.json"
  protected val imagePath: String = s"towers/ArrowTower$level.png"

  var shootAnimation: Animation = _
  var shootEndAnimation: Animation = _
  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit = {}
  override def setUpImages(): Unit = {}

object ArrowTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): ArrowTower =
    new ArrowTower(gp, level, s"ArrowTower0$level", Arrow(gp), pos: (Double, Double))

class MagicTower(
  gp: GamePanel,
  level: Int,
  val name: String,
  val weaponType: Weapon,
  var pos: (Double, Double),
  val range: Double = 100,
  val maxAttackCoolDown: Double = 2 * 60
) extends Tower(gp, level):

  protected val jsonPath: String = s"towers/MagicTower$level.json"
  protected val imagePath: String = s"towers/MagicTower$level.png"

  var shootAnimation: Animation = _
  var shootEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit = {}
  override def setUpImages(): Unit = {}

object MagicTower :
  def apply(gp: GamePanel, level: Int, pos: (Double, Double)): MagicTower =
    new MagicTower(gp, level, s"MagicTower0$level", MagicBullet(gp), pos)