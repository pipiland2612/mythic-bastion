package entity.weapon

import entity.State
import entity.creature.enemy.Enemy
import game.GamePanel
import system.upgrade.UpgradeTowerType.{ARROW, EXPLO, MAGE}
import system.upgrade.UpgradeType.{DAMAGE, RANGE}
import utils.{Animation, SoundConstant, Tools}

import java.awt.Graphics2D
import java.awt.geom.{AffineTransform, Ellipse2D}
import java.awt.image.BufferedImage

/** A weapon that delivers explosive area-of-effect damage to enemies upon impact.
 *
 * The Explo projectile targets a specific enemy and deals both direct and area-of-effect (AoE) damage,
 * with stats modified by upgrades.
 */
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

  protected def getDamageMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(EXPLO, DAMAGE)

  /** Deals damage to the target enemy and nearby enemies within the AoE range.
   *
   * Applies direct damage to the target enemy and AoE damage to all enemies within the attack circle.
   */
  override protected def dealDamage(): Unit =
    super.dealDamage()
    gp.getSystemHandler.getStageManager.getGrid match
      case Some(grid) =>
        val enemiesInrange = grid.scanForEnemiesInRange(this)
        enemiesInrange.foreach(_.takeDamage(aoeDmg))
      case _ =>

  /** Updates the projectile's position upon completing its attack.
   *
   * Positions the projectile at the enemy's location with an offset when the attack is finalized.
   */
  override def finalizeAttack(): Unit =
    super.finalizeAttack()
    if attackT >= 1.0 then
      this.pos = (enemy.getPosition._1, enemy.getPosition._2 - yDrawOffSet)

object Explo:
  private val baseStats = Map(
    // (apDmg, adDmg, speed, aoeDmg, aoeRange)
    1 -> (0.0, 20.0, 0.7, 20, 40),
    2 -> (0.0, 30.0, 0.6, 30, 50),
    3 -> (0.0, 40.0, 0.6, 40, 55)
  )

  /** Creates an Explo projectile with stats based on the specified level.
   *
   * @param gp The GamePanel instance.
   * @param enemy The target Enemy.
   * @param pos The initial (x, y) position of the projectile.
   * @param level The level of the projectile (1 to 3).
   * @return A new Explo instance.
   * @throws IllegalArgumentException If the level is not between 1 and 3.
   */
  def get(gp: GamePanel, enemy: Enemy, pos: (Double, Double), level: Int = 1): Explo =
    require(level >= 1 && level <= 3, "Level must be between 1 and 3")
    val name = s"Explo0$level"
    val (apDmg, adDmg, speed, aoeDmg, aoeRange) = baseStats(level)
    new Explo(
      gp = gp, level = level, name = name, jsonPath = s"weapons/$name.json", imagePath = s"weapons/$name.png", enemy = enemy,
      pos = pos, apDmg = apDmg, adDmg = adDmg, speed = speed, aoeDmg = aoeDmg, aoeRange = aoeRange
    )

/** A projectile that follows a curved trajectory to hit a single enemy.
 *
 * The Arrow projectile targets a specific enemy, dealing direct damage with stats modified by upgrades.
 */
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

  protected def getDamageMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(ARROW, DAMAGE)
  protected def getRangeMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(ARROW, RANGE)

  protected val flySoundEffect: Array[String] = Array(SoundConstant.ARROW_FIRE1, SoundConstant.ARROW_FIRE2)
  protected val hitSoundEffect: Array[String] = Array(SoundConstant.ARROW_HIT1, SoundConstant.ARROW_HIT2)

  /** Draws the projectile and its hit animation.
   *
   * Renders the projectile along its curved trajectory, rotating it to align with the tangent of the curve,
   * and draws the hit animation when applicable.
   *
   * @param g2d The Graphics2D context for rendering.
   */
  override def draw(g2d: Graphics2D): Unit =
    drawProjectile(g2d)
    drawHitAnimation(g2d)

  /** Draws the projectile along its curved trajectory.
   *
   * Calculates the tangent of the Bezier curve to rotate the projectile image accordingly.
   *
   * @param g2d The Graphics2D context for rendering.
   */
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

  /** Draws the hit animation when the projectile strikes the enemy.
   *
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawHitAnimation(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) if this.state == State.ATTACK =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform, pos)
      case _ =>

object Arrow:
  private val baseStats = Map(
    1 -> (0.0, 10.0, 2.0, 0.5), // (apDmg, adDmg, speed, curveConst)
    2 -> (0.0, 15.0, 2.2, 0.5),
    3 -> (0.0, 20.0, 2.4, 0.5)
  )

  /** Creates an Arrow projectile with stats based on the specified level.
   *
   * @param gp The GamePanel instance.
   * @param enemy The target Enemy.
   * @param pos The initial (x, y) position of the projectile.
   * @param l The level of the projectile (1 to 3).
   * @return A new Arrow instance.
   */
  def get(gp: GamePanel, enemy: Enemy, pos: (Double, Double), l: Int): Arrow =
    val level = Math.min(3, l)
    val name = s"Arrow0$level"
    val (apDmg, adDmg, speed, curveConst) = baseStats(level)
    new Arrow(
      gp = gp, level = level, name = name, jsonPath = s"weapons/$name.json", imagePath = s"weapons/$name.png", enemy = enemy,
      pos = pos, apDmg = apDmg, adDmg = adDmg, speed = speed, curveConst = curveConst
    )

/** A magical projectile that moves directly toward its target and deals armor-piercing damage.
 *
 * The MagicBullet targets a specific enemy, moving in a straight line and dealing damage upon collision,
 * with stats modified by upgrades.
 */
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

  protected def getDamageMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(MAGE, DAMAGE)
  protected def getRangeMultiplier: Double = gp.getSystemHandler.getUpgradeManager.getCumulativeMultiplier(MAGE, RANGE)

  /** Parses animation data from a vector of frame sequences.
   *
   * Assigns idle, hit, and hit-end animations based on the provided frame sequences.
   *
   * @param value A Vector of Vector[BufferedImage] containing frame sequences for each animation.
   */
  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(0), frameDuration = 10)
    hitAnimation = Animation(frames = value(2), frameDuration = 10)
    hitEndAnimation = Animation(frames = value(3), frameDuration = 10)

  /** Updates the projectile's position and checks for collision.
   *
   * Moves the projectile toward the enemy based on the angle to the target and checks for collision to deal damage.
   */
  override def attack(): Unit =
    val angle = Tools.getAngle(pos, enemy.getPosition)
    move(angle)
    checkCollision()

  /** Checks for collision with the target enemy.
   *
   * If the projectile is close enough to the enemy, deals damage and transitions to the attack state.
   */
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
    1 -> 20.0, // (apDmg)
    2 -> 25.0,
    3 -> 30.0
  )

  /** Creates a MagicBullet projectile with stats based on the specified level.
   *
   * @param gp The GamePanel instance.
   * @param enemy The target Enemy.
   * @param pos The initial (x, y) position of the projectile.
   * @param l The level of the projectile (1 to 3).
   * @return A new MagicBullet instance.
   */
  def get(gp: GamePanel, enemy: Enemy, pos: (Double, Double), l: Int = 1): MagicBullet =
    val apDmg = baseStats(l)
    new MagicBullet(gp, l, name, jsonPath, imagePath, enemy, pos, apDmg)