package entity.weapon

import entity.State
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.{Animation, SoundConstant, Tools}

import java.awt.Graphics2D
import java.awt.geom.{AffineTransform, Ellipse2D}
import java.awt.image.BufferedImage

case class Explo(
  gp: GamePanel,
  level: Int,
  name: String,
  jsonPath: String,
  imagePath: String,
  enemy: Enemy,
  var pos: (Double, Double),
  protected val apDmg: Double,
  protected val adDmg: Double,
  protected val speed: Double,
  protected val aoeDmg: Double,
  protected val curveConst: Double = 2,
  private val aoeRange: Double
) extends Weapon(gp, enemy):
  private val yDrawOffSet: Double = 45
  override def attackCircle: Ellipse2D = Ellipse2D.Double(pos._1 - (3 + 2 * level), pos._2, aoeRange * 2, aoeRange * 4 / 3)
  protected val flySoundEffect: Array[String] = Array(SoundConstant.EXPLO_FIRESTART1)
  protected val hitSoundEffect: Array[String] = Array(SoundConstant.EXPLO_FIREEND1)

  protected def getDamageMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier("explo", "damage")
  protected def getRangeMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier("explo", "range")

  override protected def dealDamage(): Unit =
    super.dealDamage()
    gp.getSystemHandler.getStageManager.getGrid match
      case Some(grid) =>
        val enemiesInrange = grid.scanForEnemiesInRange(this)
        enemiesInrange.foreach(_.takeDamage(aoeDmg))
      case _ =>

  override def finalizeAttack(): Unit =
    super.finalizeAttack()
    if attackT >= 1.0 then
      this.pos = (enemy.getPosition._1, enemy.getPosition._2 - yDrawOffSet)

object Explo:
  private val baseStats = Map(
//  (apDmg, adDmg, speed, aoeDmg, aoeRange)
    1 -> (0.0, 20.0, 0.7, 20, 40),
    2 -> (0.0, 30.0, 0.6, 30, 50),
    3 -> (0.0, 40.0, 0.6, 40, 55)
  )

  def get(gp: GamePanel, enemy: Enemy, pos: (Double, Double), level: Int = 1): Explo =
    require(level >= 1 && level <= 3, "Level must be between 1 and 3")
    val name = s"Explo0$level"
    val (apDmg, adDmg, speed, aoeDmg, aoeRange) = baseStats(level)
    new Explo(
      gp = gp, level = level, name = name, jsonPath = s"weapons/$name.json", imagePath = s"weapons/$name.png", enemy = enemy,
      pos = pos, apDmg = apDmg, adDmg = adDmg, speed = speed, aoeDmg = aoeDmg, aoeRange = aoeRange
    )

case class Arrow(
  gp: GamePanel,
  level: Int,
  name: String,
  jsonPath: String,
  imagePath: String,
  enemy: Enemy,
  var pos: (Double, Double),
  protected val apDmg: Double,
  protected val adDmg: Double,
  protected val speed: Double,
  protected val curveConst: Double,
) extends Weapon(gp, enemy) :
  private val transform = new AffineTransform()
  override protected val deadDuration = 30
  override protected val weight = 0.5

  protected def getDamageMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier("arrow", "damage")
  protected def getRangeMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier("arrow", "range")

  protected val flySoundEffect: Array[String] = Array(SoundConstant.ARROW_FIRE1, SoundConstant.ARROW_FIRE2)
  protected val hitSoundEffect: Array[String] = Array(SoundConstant.ARROW_HIT1, SoundConstant.ARROW_HIT2)

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

object Arrow :
  private val baseStats = Map(
    1 -> (0.0, 10.0, 2.0, 0.5),  // (apDmg, adDmg, speed, curveConst, yDrawOffSet)
    2 -> (0.0, 15.0, 2.2, 0.5),  // Example stats for level 2
    3 -> (0.0, 20.0, 2.4, 0.5)   // Example stats for level 3
  )

  // Main apply method with required level
  def get(gp: GamePanel, enemy: Enemy, pos: (Double, Double), l: Int): Arrow =
    val level = Math.min(3, l)
    val name = s"Arrow0$level"
    val (apDmg, adDmg, speed, curveConst) = baseStats(level)
    new Arrow(
      gp = gp, level = level, name = name, jsonPath = s"weapons/$name.json", imagePath = s"weapons/$name.png", enemy = enemy,
      pos = pos, apDmg = apDmg, adDmg = adDmg, speed = speed, curveConst = curveConst
    )

case class MagicBullet(
  gp: GamePanel,
  level: Int,
  name: String,
  jsonPath: String,
  imagePath: String,
  enemy: Enemy,
  var pos: (Double, Double),
  apDmg: Double,
  protected val adDmg: Double = 0,
  protected val speed: Double = 2,
  protected val curveConst: Double = 0
) extends Weapon(gp, enemy):
  override protected val deadDuration = 30
  protected val flySoundEffect: Array[String] = Array(SoundConstant.MAGIC_FIRE1)
  protected val hitSoundEffect: Array[String] = Array()

  protected def getDamageMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier("magic", "damage")
  protected def getRangeMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier("magic", "range")

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

  private val baseStats = Map(
    1 -> (20.0),  // (apDmg)
    2 -> (25.0),
    3 -> (30.0)
  )

  def get(gp: GamePanel, enemy: Enemy, pos: (Double, Double), l: Int = 1): MagicBullet =
    val apDmg = baseStats(l)
    new MagicBullet(gp, l, name, jsonPath, imagePath, enemy, pos, apDmg)