package entity.tower

import game.GamePanel
import utils.Animation

import java.awt.image.BufferedImage

class ExploTower(gp: GamePanel, level: Int) extends Tower(gp, level):

  val towerType = "ExploTower"
  protected val apDmg: Double = 0
  protected val adDmg: Double = 10
  protected val range: Double = 2
  protected val speed: Double = 0.5
  protected val name: String = s"${towerType}0$level"
  protected val jsonPath: String = s"towers/${towerType}$level.json"
  protected val imagePath: String = s"towers/${towerType}$level.png"

  var idleAnimation: Animation = _
  var shootAnimation: Animation = _
  var shootEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    shootAnimation = Animation(value(1), 10)
    shootEndAnimation = Animation(value(2), 10)


class ArrowTower(gp: GamePanel, level: Int) extends Tower(gp, level):

  val towerType = "ArrowTower"
  protected val apDmg: Double = 0
  protected val adDmg: Double = 10
  protected val range: Double = 2
  protected val speed: Double = 0.5
  protected val name: String = s"${towerType}$level"
  protected val jsonPath: String = s"towers/${towerType}$level.json"
  protected val imagePath: String = s"towers/${towerType}$level.png"

  var idleAnimation: Animation = _
  var shootAnimation: Animation = _
  var shootEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit = {}

class MagicTower(gp: GamePanel, level: Int) extends Tower(gp, level):

  val towerType = "MagicTower"
  protected val apDmg: Double = 0
  protected val adDmg: Double = 10
  protected val range: Double = 2
  protected val speed: Double = 0.5
  protected val name: String = s"${towerType}$level"
  protected val jsonPath: String = s"towers/${towerType}$level.json"
  protected val imagePath: String = s"towers/${towerType}$level.png"

  var idleAnimation: Animation = _
  var shootAnimation: Animation = _
  var shootEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit = {}
