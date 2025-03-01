package entity.creature

import entity.{Attacker, Defender, Direction, Entity, State}
import game.GamePanel
import utils.Animation

import java.awt.geom.{Ellipse2D, Rectangle2D}
import java.awt.Graphics2D

abstract class Creature(gp: GamePanel) extends Entity(gp) with Attacker with Defender:
  currentAnimation = Some(idleAnimation)
  private val id = Creature.nextId()
  protected val maxHealth: Double
  protected var health: Double
  protected val rect: Rectangle2D
  protected val maxDeadCounter: Double

  protected var isCollided: Boolean = false
  protected var lastPosition: (Double, Double) = (0,0)
  protected var hasDied: Boolean = false

  private var deadCounter: Int = 0

  protected var walkingAnimation: Animation = _
  protected var fightingAnimation: Animation = _
  protected var deadAnimation: Animation = _
  def attackCircle: Ellipse2D = Ellipse2D.Double(pos._1 + 10, pos._2, getRange*2, getRange*4/3)

  def getMaxDeadCounter: Double = maxDeadCounter
  def getRect: Rectangle2D = rect
  def hasDie: Boolean = hasDied
  def getId: Int = id

  def attackBox: Rectangle2D = new Rectangle2D.Double(
    pos._1 + rect.getX, pos._2 + rect.getY,
    rect.getWidth, rect.getHeight
  )
  def getMaxHealth: Double = maxHealth
  def getHealth: Double = health

  def takeDamage(damage: Double) = health -= damage
  def dealDamage(creature: Creature): Unit =
    val apDamge = Math.max(getApDmg - creature.getApDefense, 0)
    val adDamage = Math.max(getAdDmg - creature.getAdDefense, 0)
    creature.takeDamage(adDamage + apDamge)

  protected def move(dx: Double, dy: Double): Unit =
    state = State.RUN
    this.pos = (pos._1 + dx, pos._2 + dy)
    needsAnimationUpdate = true

  protected def continueMove(): Unit =
    if !isCollided then
      direction match
        case Direction.UP => this.move(0, -this.speed)
        case Direction.DOWN => this.move(0, this.speed)
        case Direction.LEFT => this.move(-this.speed, 0)
        case Direction.RIGHT => this.move(this.speed, 0)
        case Direction.UP_LEFT => this.move(-this.speed, -this.speed)
        case Direction.UP_RIGHT => this.move(this.speed, -this.speed)
        case Direction.DOWN_LEFT => this.move(-this.speed, this.speed)
        case Direction.DOWN_RIGHT => this.move(this.speed, this.speed)

  override def update(): Unit =
    lastPosition = (attackBox.getCenterX, attackBox.getCenterY)
    if health <= 0 then
      needsAnimationUpdate = true
      this.state = State.DEAD
      deadCounter += 1
      if deadCounter >= maxDeadCounter then hasDied = true
    super.update()

  override def draw(g2d: Graphics2D): Unit =
    super.draw(g2d)
    g2d.draw(attackCircle)
    g2d.drawRect(attackBox.getX.toInt, attackBox.getY.toInt, attackBox.getWidth.toInt, attackBox.getHeight.toInt)

  override def hashCode(): Int = id.hashCode()
  override def equals(obj: Any): Boolean = obj match
    case other: Creature => this.id == other.id
    case _            => false

object Creature:
  private var idCounter: Int = 0
  private def nextId(): Int =
    idCounter += 1
    idCounter
