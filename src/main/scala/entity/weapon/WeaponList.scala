package entity.weapon
import entity.State
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.{Animation, Tools}

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
  protected val speed: Double = 0.7,
  protected val curveConst: Double = 1.8,
  protected val yDrawOffSet: Double = 45
) extends Weapon(gp, enemy)


object Explo:
  val name = s"Explo01"
  private val jsonPath: String = s"weapons/Explo01.json"
  private val imagePath: String = s"weapons/Explo01.png"

  def apply(gp: GamePanel, enemy: Enemy, pos: (Double, Double)): Explo = new Explo(gp, name, jsonPath, imagePath, enemy, pos)

case class Arrow(
  gp: GamePanel,
  name: String,
  jsonPath: String,
  imagePath: String,
  enemy: Enemy,
  var pos: (Double, Double),
  protected val apDmg: Double = 0,
  protected val adDmg: Double = 15,
  protected val speed: Double = 2,
  protected val curveConst: Double = 0.5,
  protected val yDrawOffSet: Double = 0
) extends Weapon(gp, enemy):
  override protected val deadDuration = 30
  override protected val hitTime = 0.9
  override protected val weight = 0.5

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
  val apDmg: Double = 20,
  protected val adDmg: Double = 0,
  protected val speed: Double = 2,
  protected val curveConst: Double = 0,
  protected val yDrawOffSet: Double = 0
) extends Weapon(gp, enemy):
  override protected val deadDuration = 30

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    hitAnimation = Animation(value(2), 10)
    hitEndAnimation = Animation(value(3), 10)

  override def attack(): Unit =
    val angle = Tools.getAngle(pos, enemy.getPosition)
    move(angle)
    val (xDist, yDist) = (enemy.getPosition._1 - this.pos._1, enemy.getPosition._2 - this.pos._2)
    if (Math.abs(xDist) <= this.speed && Math.abs(yDist) <= this.speed) then
      dealDamage()
      this.state = State.ATTACK
      needsAnimationUpdate = true
      checkAnimationUpdate()
      this.pos = (enemy.getPosition._1, enemy.getPosition._2)

object MagicBullet:
  val name = "MagicBullet"
  private val jsonPath: String = s"weapons/MagicBullet.json"
  private val imagePath: String = s"weapons/MagicBullet.png"
  def apply(gp: GamePanel, enemy: Enemy, pos: (Double, Double)): MagicBullet = new MagicBullet(gp, name, jsonPath, imagePath, enemy,pos)