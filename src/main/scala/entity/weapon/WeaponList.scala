package entity.weapon
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.Animation

import java.awt.image.BufferedImage

case class Explo(
  gp: GamePanel,
  name: String,
  jsonPath: String,
  imagePath: String,
  enemy: Enemy,
  var pos: (Double, Double),
  protected val apDmg: Double = 0,
  protected val adDmg: Double = 20,
  protected val speed: Double = 5,
) extends Weapon(gp, enemy)

object Explo:
  val name = s"Explo01"
  private val jsonPath: String = s"weapons/Explo01.json"
  private val imagePath: String = s"weapons/Explo01.png"

  def apply(gp: GamePanel, enemy: Enemy, pos: (Double, Double)): Explo = new Explo(gp, name, jsonPath, imagePath, enemy,pos)

case class Arrow(
  gp: GamePanel,
  name: String,
  jsonPath: String,
  imagePath: String,
  enemy: Enemy,
  var pos: (Double, Double),
  protected val apDmg: Double = 0,
  protected val adDmg: Double = 10,
  protected val speed: Double = 5,
) extends Weapon(gp, enemy):

  var missAnimation: Animation = _
  var missEndAnimation: Animation = _
  private val missIndex = 3
  private val missEndIndex = 4

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    super.parseInformation(value)
    missAnimation = Animation(value(missIndex), frameDuration)
    missEndAnimation = Animation(value(missEndIndex), frameDuration)

object Arrow:
  val name = s"Arrow01"
  private val jsonPath: String = s"weapons/Arrow01.json"
  private val imagePath: String = s"weapons/Arrow01.png"
  def apply(gp: GamePanel, enemy: Enemy, pos: (Double, Double)): Arrow = new Arrow(gp, name, jsonPath, imagePath, enemy,pos)

case class MagicBullet(
  gp: GamePanel,
  val name: String,
  jsonPath: String,
  imagePath: String,
  enemy: Enemy,
  var pos: (Double, Double),
  val apDmg: Double = 0,
  protected val adDmg: Double = 10,
  protected val speed: Double = 5,
) extends Weapon(gp, enemy):
  var travelEnd: Animation = _

  private val travelEndIndex = 1
  private val hitIndex = 2
  private val hitEndIndex = 3

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(idleIndex), frameDuration)
    travelEnd = Animation(value(travelEndIndex), frameDuration)
    hitAnimation = Animation(value(hitIndex), frameDuration)
    hitEndAnimation = Animation(value(hitEndIndex), frameDuration)

object MagicBullet:
  val name = "MagicBullet"
  private val jsonPath: String = s"weapons/MagicBullet.json"
  private val imagePath: String = s"weapons/MagicBullet.png"
  def apply(gp: GamePanel, enemy: Enemy, pos: (Double, Double)): MagicBullet = new MagicBullet(gp, name, jsonPath, imagePath, enemy,pos)