package entity.weapon

import game.GamePanel
import utils.Animation

import java.awt.image.BufferedImage

class Explo(gp: GamePanel) extends Weapon(gp):

  protected val name: String = "Explo01"
  protected val jsonPath: String = s"weapons/Explo01.json"
  protected val imagePath: String = s"weapons/Explo01.png"

class Arrow(gp: GamePanel) extends Weapon(gp):

  protected val name: String = "Arrow01"
  protected val jsonPath: String = s"weapons/Arrow01.json"
  protected val imagePath: String = s"weapons/Arrow01.png"

  var missAnimation: Animation = _
  var missEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    super.parseInformation(value)
    missAnimation = Animation(value(3), 10)
    missEndAnimation = Animation(value(3), 10)

class MagicBullet(gp: GamePanel) extends Weapon(gp):

  protected val name: String = "MagicBullet"
  protected val jsonPath: String = s"weapons/MagicBullet.json"
  protected val imagePath: String = s"weapons/MagicBullet.png"
  var travelEnd: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    travelEnd = Animation(value(1), 10)
    hitAnimation = Animation(value(2), 10)
    hitEndAnimation = Animation(value(3), 10)
