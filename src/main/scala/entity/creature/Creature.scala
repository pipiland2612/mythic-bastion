package entity.creature

import entity.Entity

abstract class Creature extends Entity:
  val speed: Int
  val maxHealth: Int
  var health: Int

  var state: State = State.IDLE
  var needsAnimationUpdate: Boolean = false
  var direction: Direction = Direction.RIGHT
  var isCollided: Boolean = false

  def move(dx: Int, dy: Int): Unit =
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