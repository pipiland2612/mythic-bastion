package entity.creature

import entity.{Attacker, Defender, Direction, Entity, State}
import game.GamePanel

import java.awt.geom.Rectangle2D
import java.awt.{Color, Graphics2D}

abstract class Creature(gp: GamePanel) extends Entity(gp) with Attacker with Defender:
  protected val maxHealth: Double
  protected var health: Double
  protected val rect: Rectangle2D
  protected val maxDeadCounter: Double
  private var deadCounter: Int = 0

  protected var isCollided: Boolean = false
  protected var lastPosition: (Double, Double) = (0,0)

  var hasDied: Boolean = false

  def getMaxDeadCounter: Double = maxDeadCounter
  def getRect: Rectangle2D = rect

  def attackBox: Rectangle2D = new Rectangle2D.Double(
    pos._1 + rect.getX, pos._2 + rect.getY,
    rect.getWidth, rect.getHeight
  )
  def getMaxHealth: Double = maxHealth
  def getHealth: Double = health

  def takeDamage(damage: Double) = health -= damage

  def move(dx: Double, dy: Double): Unit =
    state = State.RUN
    this.pos = (pos._1 + dx, pos._2 + dy)
    needsAnimationUpdate = true

  def continueMove(): Unit =
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
    lastPosition = pos
    if health <= 0 then
      needsAnimationUpdate = true
      this.state = State.DEAD
      deadCounter += 1
      if deadCounter >= maxDeadCounter then hasDied = true
    super.update()

  override def draw(g2d: Graphics2D): Unit =
    super.draw(g2d)
    g2d.setColor(Color.GREEN)
    g2d.drawRect(attackBox.getX.toInt, attackBox.getY.toInt, attackBox.getWidth.toInt, attackBox.getHeight.toInt)
