package entity.weapon
import game.GamePanel
import utils.Animation

import java.awt.image.BufferedImage

case class Explo(
  gp: GamePanel,
  name: String,
  jsonPath: String,
  imagePath: String,
  protected val apDmg: Double = 0,
  protected val adDmg: Double = 20,
  protected val speed: Double = 0.5,
) extends Weapon(gp)

object Explo:
  val name = s"Explo01"
  private val jsonPath: String = s"weapons/Explo01.json"
  private val imagePath: String = s"weapons/Explo01.png"
  def apply(gp: GamePanel): Explo =
    new Explo(gp, name, jsonPath, imagePath)

case class Arrow(
  gp: GamePanel,
  name: String,
  jsonPath: String,
  imagePath: String,
  protected val apDmg: Double = 0,
  protected val adDmg: Double = 10,
  protected val speed: Double = 0.5,
) extends Weapon(gp):
  var missAnimation: Animation = _
  var missEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    super.parseInformation(value)
    missAnimation = Animation(value(3), 10)
    missEndAnimation = Animation(value(3), 10)

object Arrow:
  val name = s"Arrow01"
  private val jsonPath: String = s"weapons/Arrow01.json"
  private val imagePath: String = s"weapons/Arrow01.png"
  def apply(gp: GamePanel): Arrow = new Arrow(gp, name, jsonPath, imagePath)

case class MagicBullet(
  gp: GamePanel,
  val name: String,
  jsonPath: String,
  imagePath: String,
  val apDmg: Double = 0,
  protected val adDmg: Double = 10,
  protected val speed: Double = 0.5,
) extends Weapon(gp):
  var travelEnd: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    travelEnd = Animation(value(1), 10)
    hitAnimation = Animation(value(2), 10)
    hitEndAnimation = Animation(value(3), 10)

object MagicBullet:
  val name = "MagicBullet"
  private val jsonPath: String = s"weapons/MagicBullet.json"
  private val imagePath: String = s"weapons/MagicBullet.png"
  def apply(gp: GamePanel): MagicBullet = new MagicBullet(gp, name, jsonPath, imagePath)