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
  protected val speed: Double = 5,
) extends Weapon(gp, enemy):

  private var attackT: Double = 0.0
  private var attackInProgress: Boolean = false
  private var attackCurve: Option[((Double, Double), (Double, Double), (Double, Double))] = None
  private var angleOffset: Double = 0

  private def initializeAttackCurve(): Unit =
    val start = pos
    val end = (enemy.attackBox.getCenterX, enemy.attackBox.getCenterY)
    val mid = calculateMidPoint(start, end)

    attackCurve = Some((start, mid, end))
    attackT = 0.0
    attackInProgress = true
    angleOffset = Math.toRadians(40)

  private def calculateMidPoint(start: (Double, Double), end: (Double, Double)): (Double, Double) =
    val xOffset = Math.random() * 40 - 20
    val distance = Math.sqrt(Math.pow(end._1 - start._1, 2) + Math.pow(end._2 - start._2, 2))
    val yOffset = -distance * 1.8  // make the curve higher in the middle (adjust for more or less curve)

    ((start._1 + end._1) / 2 + xOffset, (start._2 + end._2) / 2 + yOffset)

  private def moveAlongCurve(): Unit =
    attackCurve match
      case Some((start, mid, end)) =>
        attackT = (attackT + 0.02).min(1.0)
        pos = Tools.bezier(attackT, start, mid, end)
      case None =>
        attackInProgress = false

  private def updateProjectileMovement(): Unit =
    val angle = Tools.getAngle(pos, enemy.getPosition) - angleOffset
    move(angle)
    angleOffset *= 0.98 // gradually reduce the angle for the projectile to straighten

  private def finalizeAttack(): Unit =
    if (attackT >= 1.0)
      attackInProgress = false
      dealDamage()
      this.state = State.ATTACK
      needsAnimationUpdate = true
      checkAnimationUpdate()
      this.pos = (enemy.getPosition._1, enemy.getPosition._2 - 45) // set the final position to the enemy's position to play the boom animation

  override def attack(): Unit =
    if (!attackInProgress) then
      initializeAttackCurve()
    else
      moveAlongCurve()
      updateProjectileMovement()
      finalizeAttack()

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
  protected val adDmg: Double = 10,
  protected val speed: Double = 5,
) extends Weapon(gp, enemy):

  private var missAnimation: Animation = _
  private var missEndAnimation: Animation = _
  private val missIndex = 3
  private val missEndIndex = 4

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    super.parseInformation(value)
    missAnimation = Animation(value(3), 10)
    missEndAnimation = Animation(value(4), 10)

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
    idleAnimation = Animation(value(0), 10)
    travelEnd = Animation(value(1), 10)
    hitAnimation = Animation(value(2), 10)
    hitEndAnimation = Animation(value(3), 10)

object MagicBullet:
  val name = "MagicBullet"
  private val jsonPath: String = s"weapons/MagicBullet.json"
  private val imagePath: String = s"weapons/MagicBullet.png"
  def apply(gp: GamePanel, enemy: Enemy, pos: (Double, Double)): MagicBullet = new MagicBullet(gp, name, jsonPath, imagePath, enemy,pos)