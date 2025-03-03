package entity.creature

import entity.{Attacker, Defender, Direction, Entity, State}
import game.GamePanel
import utils.Animation

import java.awt.geom.{Ellipse2D, Rectangle2D}
import java.awt.Graphics2D
import scala.collection.mutable.ListBuffer

abstract class Creature(gp: GamePanel) extends Entity(gp) with Attacker with Defender:
  private val id = Creature.nextId()

  protected val maxHealth: Double
  protected var health: Double
  protected val rect: Rectangle2D
  protected val maxDeadCounter: Double

  protected var isGoing: Boolean = true
  protected var lastPosition: (Double, Double) = (0, 0)
  protected var hasDied: Boolean = false
  private var deadCounter: Int = 0

  protected var walkingAnimation: Animation = _
  protected var fightingAnimation: Animation = _
  protected var deadAnimation: Animation = _

  private var attackCounter = 0
  private val maxAttackCounter = 40

  currentAnimation = Some(idleAnimation)

  def getMaxDeadCounter: Double = maxDeadCounter
  def getRect: Rectangle2D = rect
  def hasDie: Boolean = hasDied
  def getId: Int = id
  def getMaxHealth: Double = maxHealth
  def getHealth: Double = health

  def attackCircle: Ellipse2D =
    Ellipse2D.Double(pos._1 + 5, pos._2, getRange * 2, getRange * 4 / 3)

  def attackBox: Rectangle2D =
    new Rectangle2D.Double(pos._1 + rect.getX, pos._2 + rect.getY, rect.getWidth, rect.getHeight)

  protected def move(dx: Double, dy: Double): Unit =
    state = State.RUN
    this.pos = (pos._1 + dx, pos._2 + dy)
    needsAnimationUpdate = true

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

  private def attack(creature: Creature): Unit =
    if state != State.ATTACK && this.attackCoolDown <= 0 then
      state = State.ATTACK
      needsAnimationUpdate = true
      attackCoolDown = attackCoolDown
      if fightingAnimation.isInAttackInterval then
        dealDamage(creature)

  def dealDamage(creature: Creature): Unit =
    val apDamage = Math.max(getApDmg - creature.getApDefense, 0)
    val adDamage = Math.max(getAdDmg - creature.getAdDefense, 0)
    creature.takeDamage(adDamage + apDamage)

  def takeDamage(damage: Double): Unit =
    health -= damage

  protected def findEnemy[T <: Creature](): ListBuffer[T]

  protected def setAction(): Unit =
    val enemyList = findEnemy()
    if enemyList.nonEmpty then
      attack(enemyList.head)

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

  def handleAttackAnimation(): Unit =
    attackCounter += 1
    if attackCounter >= maxAttackCounter then
      attackCounter = 0
      state = State.IDLE
    needsAnimationUpdate = true

  override def update(): Unit =
    super.update()
    updateLastPosition()
    checkDeathStatus()

  private def updateLastPosition(): Unit =
    lastPosition = (attackBox.getCenterX, attackBox.getCenterY)

  private def checkDeathStatus(): Unit =
    if health <= 0 then
      needsAnimationUpdate = true
      this.state = State.DEAD
      deadCounter += 1
      if deadCounter >= maxDeadCounter then
        hasDied = true

  override def hashCode(): Int = id.hashCode()

  override def equals(obj: Any): Boolean = obj match
    case other: Creature => this.id == other.id
    case _ => false

object Creature:
  private var idCounter: Int = 0

  private def nextId(): Int =
    idCounter += 1
    idCounter