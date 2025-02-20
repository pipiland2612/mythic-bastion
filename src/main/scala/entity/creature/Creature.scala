package entity.creature

import entity.Entity
import utils.Animation

abstract class Creature extends Entity:
  val speed: Double
  val maxHealth: Double
  var health: Double

  var state: State = State.IDLE
  var needsAnimationUpdate: Boolean = false
  var direction: Direction = Direction.RIGHT
  var isCollided: Boolean = false

  def images: Map[(Direction, State), Animation] = Map()
//    Map(
//      (Direction.RIGHT, State.IDLE) -> idleAnimations(Direction.RIGHT),
//      (Direction.DOWN, State.IDLE) -> idleAnimations(Direction.DOWN),
//      (Direction.LEFT, State.IDLE) -> idleAnimations(Direction.LEFT),
//      (Direction.UP, State.IDLE) -> idleAnimations(Direction.UP),
//
//      (Direction.RIGHT, State.RUN) -> runAnimations(Direction.RIGHT),
//      (Direction.DOWN, State.RUN) -> runAnimations(Direction.DOWN),
//      (Direction.LEFT, State.RUN) -> runAnimations(Direction.LEFT),
//      (Direction.UP, State.RUN) -> runAnimations(Direction.UP),
//
//      (Direction.UP, State.ATTACK) -> attackAnimations(Direction.UP),
//      (Direction.DOWN, State.ATTACK) -> attackAnimations(Direction.DOWN),
//      (Direction.LEFT, State.ATTACK) -> attackAnimations(Direction.LEFT),
//      (Direction.RIGHT, State.ATTACK) -> attackAnimations(Direction.RIGHT),
//
//    )

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

  def checkAnimationUpdate(): Unit =
    if(needsAnimationUpdate) then
      needsAnimationUpdate = false
      currentAnimation = images.get(this.direction, this.state)
      currentAnimation.foreach(animation => animation.update())

  override def update(): Unit =
    checkAnimationUpdate()
