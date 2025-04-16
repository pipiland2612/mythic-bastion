package entity.weapon

import entity.creature.enemy.Enemy
import entity.{Attacker, Direction, Entity, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import scala.util.Random

/** Abstract base class for weapons in the game, handling projectile behavior and enemy interaction.
 * Extends Entity for rendering and position management, and Attacker for damage dealing.
 *
 * @param gp    The GamePanel instance managing the game.
 * @param enemy The target enemy for this weapon.
 */
abstract class Weapon(gp: GamePanel, enemy: Enemy) extends Entity(gp: GamePanel) with Attacker:
  this.currentAnimation = Some(idleAnimation)
  private var deadCounter: Int = 0
  private var angleOffset: Double = 0
  private var hasHit: Boolean = false
  private var attackInProgress: Boolean = false
  protected val deadDuration: Int = 110
  protected val maxAttackCoolDown: Double = 0
  protected val range: Double = 0
  protected var hitAnimation: Animation = _
  protected var hitEndAnimation: Animation = _
  protected val curveConst: Double
  protected val weight: Double = 2
  protected var attackT: Double = 0.0
  protected var attackCurve: Option[((Double, Double), (Double, Double), (Double, Double))] = None
  private val baseSpeed: Double = 0.02
  protected val flySoundEffect: Array[String]
  protected val hitSoundEffect: Array[String]

  def hit: Boolean = hasHit
  def getCurrentEnemy: Enemy = enemy
  def attackCircle: Ellipse2D = Ellipse2D.Double(0, 0, 0, 0)
  def getFlySE: Array[String] = flySoundEffect
  def getHitSE: Array[String] = hitSoundEffect
  protected def getDamageMultiplier: Double
  override def getApDmg: Double = apDmg * getDamageMultiplier
  override def getAdDmg: Double = adDmg * getDamageMultiplier

  /** Parses animation frames for the weapon.
   *
   * @param value A vector of vectors containing BufferedImage frames for different animations.
   */
  protected def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(0), frameDuration = 10)
    hitAnimation = Animation(frames = value(1), frameDuration = 10)
    hitEndAnimation = Animation(frames = value(2), frameDuration = 10)

  protected def setUpImages(): Unit =
    this.images = WeaponAnimationFactory.createWeaponAnimationMap(
      directions = Direction.allEntityDirections,
      idleAnim = idleAnimation,
      attackAnim = hitAnimation
    )

  /** Deals damage to the target enemy, accounting for their defenses. */
  protected def dealDamage(): Unit =
    val adDamage = Math.max(getAdDmg - enemy.getAdDefense, 0)
    val apDamage = Math.max(getApDmg - enemy.getApDefense, 0)
    enemy.takeDamage(adDamage + apDamage)

  /** Moves the weapon along a specified angle.
   *
   * @param angle The angle of movement in degrees.
   */
  protected def move(angle: Double): Unit =
    val radians = Math.toRadians(angle)
    val x = pos._1 + speed * Math.cos(radians)
    val y = pos._2 + speed * Math.sin(radians)
    this.pos = (x, y)
    needsAnimationUpdate = true

  override def update(): Unit =
    super.update()
    if this.state == State.ATTACK then
      updateDeadState()
    else
      attack()

  private def updateDeadState(): Unit =
    deadCounter += 1
    needsAnimationUpdate = true
    if deadCounter >= deadDuration then
      hasHit = true

  /** Manages the attack process, initializing and following a Bezier curve toward the enemy. */
  def attack(): Unit =
    if !attackInProgress then
      initializeAttackCurve()
    else
      moveAlongCurve()
      updateProjectileMovement()
      finalizeAttack()

  /** Initializes the Bezier curve for the weapon's attack path. */
  private def initializeAttackCurve(): Unit =
    val start = pos
    val end = (enemy.attackBox.getCenterX, enemy.attackBox.getCenterY)
    val mid = calculateMidPoint(start, end)
    attackCurve = Some((start, mid, end))
    attackT = 0.0
    attackInProgress = true
    angleOffset = Math.toRadians(40)

  /** Calculates the midpoint for the Bezier curve with randomized offset.
   *
   * @param start Starting position of the weapon.
   * @param end   Target position (enemy's center).
   * @return The calculated midpoint coordinates.
   */
  private def calculateMidPoint(start: (Double, Double), end: (Double, Double)): (Double, Double) =
    val xOffset = Math.random() * 40 - 20
    val distance = Tools.distance(end, start)
    val yOffset = -distance * curveConst
    ((start._1 + end._1) / 2 + xOffset, (start._2 + end._2) / 2 + yOffset)

  /** Moves the weapon along the Bezier curve, adjusting speed based on distance and weight. */
  private def moveAlongCurve(): Unit =
    attackCurve match
      case Some((start, mid, end)) =>
        val distance = Tools.distance(enemy.getPosition, start)
        val normalizedDist = (distance / 100).max(1.0)
        val dynamicSpeed = baseSpeed * (speed / normalizedDist)
        val isDescending = attackT >= 0.45
        val weightFactor = if isDescending then 1 + (weight * 0.2) else 1.0
        attackT = (attackT + dynamicSpeed * weightFactor).min(1.0)
        pos = Tools.bezier(attackT, start, mid, enemy.getPosition)
      case None =>
        attackInProgress = false

  private def updateProjectileMovement(): Unit =
    val angle = Tools.getAngle(pos, enemy.getPosition) - angleOffset
    move(angle)
    angleOffset *= 0.98

  private var hasPlayHitSound = false

  protected def finalizeAttack(): Unit =
    if attackT >= 1.0 then
      if !hasPlayHitSound then playHitSound()
      attackInProgress = false
      dealDamage()
      this.state = State.ATTACK
      needsAnimationUpdate = true
      checkAnimationUpdate()
      hasPlayHitSound = false

  private def playHitSound(): Unit =
    if hitSoundEffect.nonEmpty then
      hasPlayHitSound = true
      val random = Random.nextInt(hitSoundEffect.length)
      gp.getSystemHandler.playSE(hitSoundEffect(random))

/** Companion object for Weapon, handling setup and instantiation of specific weapon types. */
object Weapon:
  private var gp: GamePanel = _

  /** Sets up the GamePanel for weapon instantiation.
   *
   * @param gp The GamePanel instance.
   */
  def setUp(gp: GamePanel): Unit =
    this.gp = gp

  /** Creates a new weapon instance based on the specified type and level.
   *
   * @param weapon The weapon type (e.g., "Explo", "Arrow", "MagicBullet").
   * @param enemy  The target enemy.
   * @param pos    The starting position of the weapon.
   * @param l      The level of the weapon (default is 1).
   * @return A new Weapon instance.
   * @throws IllegalArgumentException if the weapon type is unknown.
   */
  def clone(weapon: String, enemy: Enemy, pos: (Double, Double), l: Int = 1): Weapon =
    val level = Math.min(l, 3)
    val extractedLevel = extractLevel(weapon, level)
    weapon match
      case s if s.startsWith("Explo") =>
        Explo.get(gp, enemy, pos, extractedLevel)
      case s if s.startsWith("Arrow") =>
        Arrow.get(gp, enemy, pos, extractedLevel)
      case s if s.startsWith("MagicBullet") =>
        MagicBullet.get(gp, enemy, pos, extractedLevel)
      case _ => throw new IllegalArgumentException(s"Unknown weapon type: $weapon")

  /** Extracts the weapon level from the weapon name or uses the default.
   *
   * @param weaponName   The name of the weapon.
   * @param defaultLevel The default level if no level is specified.
   * @return The extracted or default level.
   */
  private def extractLevel(weaponName: String, defaultLevel: Int): Int =
    val levelPattern = ".*0(\\d)$".r
    weaponName match
      case levelPattern(levelStr) =>
        levelStr.toInt
      case _ =>
        defaultLevel

/** Factory object for creating weapon animation maps. */
private object WeaponAnimationFactory:
  /** Creates a map of animations for different directions and states.
   *
   * @param directions The directions to map animations for.
   * @param idleAnim   The idle animation.
   * @param attackAnim The attack animation.
   * @return A map of (Direction, State) to Animation.
   */
  def createWeaponAnimationMap(
    directions: Seq[Direction],
    idleAnim: Animation,
    attackAnim: Animation
  ): Map[(Direction, State), Animation] =
    Tools.fillMap(directions, State.IDLE, idleAnim) ++
    Tools.fillMap(directions, State.ATTACK, attackAnim)