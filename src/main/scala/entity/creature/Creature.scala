package entity.creature

import entity.{Attacker, Direction, Entity, State}
import game.GamePanel

import java.awt.Graphics2D

abstract class Creature(gp: GamePanel) extends Entity(gp) with Attacker:
  protected val maxHealth: Double
  protected var health: Double

  var isCollided: Boolean = false
  var hasDied: Boolean = false

  def getMaxHealth: Double = maxHealth
  def getHealth: Double = health

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
    super.update()
    if health <= 0 then
      this.state = State.DEAD

  override def draw(g2d: Graphics2D): Unit =
    super.draw(g2d)
    if this.state == State.DEAD then
      hasDied = true