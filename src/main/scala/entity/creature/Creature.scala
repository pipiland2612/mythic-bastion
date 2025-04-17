package entity.creature

import entity.{Attacker, Defender, Direction, Entity, State}
import game.GamePanel
import utils.{Animation, SoundConstant}

import java.awt.geom.{Ellipse2D, Rectangle2D}
import java.awt.{Color, Graphics2D}
import scala.collection.mutable.ListBuffer
import scala.util.Random

/** Abstract base class for creatures in the game, handling movement, combat, and health.
 * Extends Entity for rendering and position management, Attacker for dealing damage, and Defender for receiving damage.
 * @param gp The GamePanel instance managing the game.
 */
abstract class Creature(gp: GamePanel) extends Entity(gp) with Attacker with Defender:
  protected val maxHealth: Double
  protected var health: Double
  protected val rect: Rectangle2D
  protected val maxDeadCounter: Double
  private var lastPosition: (Double, Double) = (0, 0)
  private var hasDied: Boolean = false
  private var deadCounter: Int = 0
  protected var isGoing: Boolean = true
  protected var walkingAnimation: Animation = _
  protected var fightingAnimation: Animation = _
  protected var deadAnimation: Animation = _
  private var attackCounter = 0
  private val maxAttackCounter = 40
  protected val healthOffSet: (Int, Int)
  private val dieSE: Array[String] = Array(SoundConstant.MONSTER_DIE1, SoundConstant.MONSTER_DIE2, SoundConstant.MONSTER_DIE3, SoundConstant.MONSTER_DIE4)
  currentAnimation = Some(idleAnimation)

  def getMaxDeadCounter: Double = maxDeadCounter
  def getRect: Rectangle2D = rect
  def hasDie: Boolean = hasDied
  def getMaxHealth: Double = maxHealth
  def getHealth: Double = health
  def getHealthOffSet: (Int, Int) = healthOffSet

  /** Returns the attack range circle for the creature.
   * @return An Ellipse2D representing the creature's attack range.
   */
  def attackCircle: Ellipse2D =
    Ellipse2D.Double(pos._1 + 5, pos._2, getRange * 2, getRange * 4 / 3)

  /** Returns the bounding box for the creature's attack interactions.
   * @return A Rectangle2D representing the creature's attack box.
   */
  def attackBox: Rectangle2D =
    new Rectangle2D.Double(pos._1 + rect.getX, pos._2 + rect.getY, rect.getWidth, rect.getHeight)

  /** Moves the creature by the specified offsets.
   * @param dx The change in x-coordinate.
   * @param dy The change in y-coordinate.
   */
  protected def move(dx: Double, dy: Double): Unit =
    state = State.RUN
    this.pos = (pos._1 + dx, pos._2 + dy)
    needsAnimationUpdate = true

  /** Continues moving the creature in its current direction, if movement is enabled. */
  protected def continueMove(): Unit =
    if isGoing then
      direction match
        case Direction.UP => this.move(0, -this.speed)
        case Direction.DOWN => this.move(0, this.speed)
        case Direction.LEFT => this.move(-this.speed, 0)
        case Direction.RIGHT => this.move(this.speed, 0)
        case Direction.UP_LEFT => this.move(-this.speed, -this.speed)
        case Direction.UP_RIGHT => this.move(this.speed, -this.speed)
        case Direction.DOWN_LEFT => this.move(-this.speed, this.speed)
        case Direction.DOWN_RIGHT => this.move(this.speed, this.speed)

  /** Initiates an attack on a target creature if conditions are met.
   * @param creature The target creature to attack.
   */
  private def attack(creature: Creature): Unit =
    if state != State.ATTACK && this.attackCoolDown <= 0 then
      state = State.ATTACK
      needsAnimationUpdate = true
      attackCoolDown = attackCoolDown
      if fightingAnimation.isInAttackInterval then
        dealDamage(creature)

  /** Deals damage to a target creature, accounting for defenses.
   * @param creature The target creature to damage.
   */
  def dealDamage(creature: Creature): Unit =
    val apDamage = Math.max(getApDmg - creature.getApDefense, 0)
    val adDamage = Math.max(getAdDmg - creature.getAdDefense, 0)
    creature.takeDamage(adDamage + apDamage)

  /** Applies damage to the creature's health.
   * @param damage The amount of damage to take.
   */
  def takeDamage(damage: Double): Unit =
    health -= damage

  /** Finds enemies within the creature's attack range.
   * @return An optional list of creatures in range.
   */
  protected def findEnemy[T <: Creature](): Option[ListBuffer[T]]

  protected def updateGridPosition(): Unit =
    gp.getSystemHandler.getStageManager.getGrid match
      case Some(grid) => grid.updateCreaturePosition(this, (lastPosition._1.toInt, lastPosition._2.toInt))
      case _ =>

  protected def removeGrid(): Unit =
    gp.getSystemHandler.getStageManager.getGrid match
      case Some(grid) => grid.remove(this)
      case _ =>

  protected def setAction(): Unit =
    findEnemy() match
      case Some(list) if list.nonEmpty => attack(list.head)
      case _ =>

  /** Determines the direction of movement based on distance to a target.
   * @param xDist The x-distance to the target.
   * @param yDist The y-distance to the target.
   * @return The calculated direction.
   */
  protected def determineDirection(xDist: Double, yDist: Double): Direction =
    val (absX, absY) = (Math.abs(xDist), Math.abs(yDist))
    if absX > 0 && absY > 0 then
      if xDist < 0 && yDist < 0 then Direction.UP_LEFT
      else if xDist > 0 && yDist < 0 then Direction.UP_RIGHT
      else if xDist < 0 && yDist > 0 then Direction.DOWN_LEFT
      else Direction.DOWN_RIGHT
    else if absX > absY then
      if xDist < 0 then Direction.LEFT else Direction.RIGHT
    else if yDist < 0 then Direction.UP else Direction.DOWN

  protected def handleAttackAnimation(): Unit =
    attackCounter += 1
    if attackCounter >= maxAttackCounter then
      attackCounter = 0
      state = State.IDLE
    needsAnimationUpdate = true

  /** Updates the creature's state, including position and death status. */
  override def update(): Unit =
    super.update()
    updateLastPosition()
    checkDeathStatus()

  /** Renders the creature and its health bar.
   * @param g2d The Graphics2D context for rendering.
   */
  override def draw(g2d: Graphics2D): Unit =
    super.draw(g2d)
    val g2dCopy = g2d.create().asInstanceOf[Graphics2D]
    val maxBarWidth = 16
    val healthRatio = this.getHealth.toFloat / this.getMaxHealth.toFloat
    val healthBarWidth = (maxBarWidth * healthRatio).toInt.max(1)
    g2d.setColor(Color.GREEN)
    g2d.fillRect(attackBox.getX.toInt - healthOffSet._1, attackBox.getY.toInt - healthOffSet._2, healthBarWidth, 1)

  /** Updates the creature's last known position for grid tracking. */
  private def updateLastPosition(): Unit =
    lastPosition = (attackBox.getCenterX, attackBox.getCenterY)

  private var hasPlayDieSE = false
  /** Checks if the creature has died and plays a death sound effect if applicable. */
  private def checkDeathStatus(): Unit =
    if health <= 0 then
      needsAnimationUpdate = true
      this.state = State.DEAD
      deadCounter += 1
      if deadCounter >= maxDeadCounter then
        if !hasPlayDieSE then
          hasPlayDieSE = true
          val num = Random.nextInt(dieSE.length)
          gp.getSystemHandler.playSE(dieSE(num))
        hasDied = true