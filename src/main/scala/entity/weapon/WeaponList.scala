package entity.weapon

import entity.State
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
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
  val name = "Explo01"
  private val jsonPath: String = s"weapons/$name.json"
  private val imagePath: String = s"weapons/$name.png"

  def apply(gp: GamePanel, enemy: Enemy, pos: (Double, Double)): Explo =
    new Explo(gp, name, jsonPath, imagePath, enemy, pos)

case class Arrow(
  gp: GamePanel,
  name: String,
  jsonPath: String,
  imagePath: String,
  enemy: Enemy,
  var pos: (Double, Double),
  protected val apDmg: Double = 0,
  protected val adDmg: Double = 10,
  protected val speed: Double = 2,
  protected val curveConst: Double = 0.5,
  protected val yDrawOffSet: Double = 0
) extends Weapon(gp, enemy):
  private val transform = new AffineTransform()
  override protected val deadDuration = 30
  override protected val hitTime = 0.9
  override protected val weight = 0.5

  override def draw(g2d: Graphics2D): Unit =
    drawProjectile(g2d)
    drawHitAnimation(g2d)

  private def drawProjectile(g2d: Graphics2D): Unit =
    attackCurve match
      case Some((p0, p1, p2)) =>
        val t = attackT
        val tangentX = 2 * (1 - t) * (p1._1 - p0._1) + 2 * t * (p2._1 - p1._1)
        val tangentY = 2 * (1 - t) * (p1._2 - p0._2) + 2 * t * (p2._2 - p1._2)
        val angle = Math.atan2(tangentY, tangentX)
        val img = idleAnimation.getCurrentFrame
        val (imgWidth, imgHeight) = (img.getWidth, img.getHeight)
        val g2dCopy = g2d.create().asInstanceOf[Graphics2D]
        g2dCopy.translate(pos._1, pos._2)
        g2dCopy.rotate(angle, imgWidth / 2, imgHeight / 2)
        g2dCopy.drawImage(img, -imgWidth / 2, -imgHeight / 2, null)
        g2dCopy.dispose()
      case None =>

  private def drawHitAnimation(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) if this.state == State.ATTACK =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform, pos)
      case _ =>

object Arrow:
  val name = "Arrow01"
  private val jsonPath: String = s"weapons/$name.json"
  private val imagePath: String = s"weapons/$name.png"

  def apply(gp: GamePanel, enemy: Enemy, pos: (Double, Double)): Arrow =
    new Arrow(gp, name, jsonPath, imagePath, enemy, pos)

case class MagicBullet(
  gp: GamePanel,
  name: String,
  jsonPath: String,
  imagePath: String,
  enemy: Enemy,
  var pos: (Double, Double),
  apDmg: Double = 20,
  protected val adDmg: Double = 0,
  protected val speed: Double = 2,
  protected val curveConst: Double = 0,
  protected val yDrawOffSet: Double = 0
) extends Weapon(gp, enemy):
  override protected val deadDuration = 30

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(0), frameDuration = 10)
    hitAnimation = Animation(frames = value(2), frameDuration = 10)
    hitEndAnimation = Animation(frames = value(3), frameDuration = 10)

  override def attack(): Unit =
    val angle = Tools.getAngle(pos, enemy.getPosition)
    move(angle)
    checkCollision()

  private def checkCollision(): Unit =
    val (xDist, yDist) = (enemy.getPosition._1 - this.pos._1, enemy.getPosition._2 - this.pos._2)
    if Math.abs(xDist) <= this.speed && Math.abs(yDist) <= this.speed then
      dealDamage()
      this.state = State.ATTACK
      needsAnimationUpdate = true
      checkAnimationUpdate()
      this.pos = enemy.getPosition

object MagicBullet:
  val name = "MagicBullet"
  private val jsonPath: String = s"weapons/$name.json"
  private val imagePath: String = s"weapons/$name.png"

  def apply(gp: GamePanel, enemy: Enemy, pos: (Double, Double)): MagicBullet =
    new MagicBullet(gp, name, jsonPath, imagePath, enemy, pos)