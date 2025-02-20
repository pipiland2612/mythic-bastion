package entity.creature.enemy

import entity.creature.{Creature, Direction, State}
import utils.{Animation, Tools}

import java.awt.Graphics2D
import java.awt.image.BufferedImage

abstract class Enemy extends Creature:

  var walkingAnimation: Animation = _
  var walkingUpAnimation: Animation = _
  var walkingDownAnimation: Animation = _
  var idleAnimation: Animation = _
  var fightingAnimation: Animation = _
  var deadAnimation: Animation = _

  val name: String
  val scaleFactor: Double = 1.25
  val playerDamage: Int
  val jsonPath, imagePath: String
  var path: Vector[(Int, Int)] = _
  var index = 0

  override def images: Map[(Direction, State), Animation] =
      Map(
        (Direction.RIGHT, State.IDLE) -> idleAnimation,
        (Direction.DOWN, State.IDLE) -> idleAnimation,
        (Direction.LEFT, State.IDLE) -> idleAnimation,
        (Direction.UP, State.IDLE) -> idleAnimation,

        (Direction.RIGHT, State.RUN) -> walkingAnimation,
        (Direction.DOWN, State.RUN) -> walkingDownAnimation,
        (Direction.LEFT, State.RUN) -> Tools.flipAnimation(walkingAnimation),
        (Direction.UP, State.RUN) -> walkingUpAnimation,

        (Direction.UP, State.ATTACK) -> fightingAnimation,
        (Direction.DOWN, State.ATTACK) -> fightingAnimation,
        (Direction.LEFT, State.ATTACK) -> fightingAnimation,
        (Direction.RIGHT, State.ATTACK) -> fightingAnimation
      )

  def enemyParse(): Unit =
    Tools.parser(jsonPath, imagePath, scaleFactor) match
      case Some(value) =>
        walkingAnimation = Animation(value(0), 10)
        walkingUpAnimation = Animation(value(1), 10)
        walkingDownAnimation = Animation(value(2), 10)
        idleAnimation = Animation(value(3), 10)
        fightingAnimation = Animation(value(4), 10)
        deadAnimation = Animation(value(5), 10)
      case _ =>

  def attackPlayer(): Unit = {}

  def followPath(goal: (Int, Int)): Unit =
    val xDist: Int = Math.abs(this.pos._1 - goal._1)
    val yDist: Int = Math.abs(this.pos._2 - goal._2)

    if xDist <= this.speed && yDist <= this.speed then
      index += 1
    if xDist > yDist then
      if goal._1 < this.pos._1 then
        this.direction = Direction.LEFT
      else
        this.direction = Direction.RIGHT
    else if xDist < yDist then
      if goal._2 < this.pos._2 then
        this.direction = Direction.UP
      else
        this.direction = Direction.DOWN

  override def update(): Unit =
    super.update()
    if index < path.length then
      followPath(path(index))
      continueMove()

  override def draw(g2d: Graphics2D): Unit =
    super.draw(g2d)

end Enemy

object Enemy:

  def enemyOfName(key: String, difficulty: Int): Option[Enemy] =
    val enemyData = Map(
      Monster01.name -> (Monster01.data, Monster01.jsonPath, Monster01.imagePath),
      Monster02.name -> (Monster02.data, Monster02.jsonPath, Monster02.imagePath),
      Monster03.name -> (Monster03.data, Monster03.jsonPath, Monster03.imagePath)
    )

    enemyData.get(key).map ((initialData, jsonData, imageData) =>
      val data: Vector[Int] = initialData.map(_ * difficulty)
      Creep(key, data(0), data(1), data(2), data(3), data(4), data(5), data(6) / difficulty, jsonData, imageData)
    )

  def clone(enemy: Enemy): Enemy =
    Creep(enemy.name, enemy.maxHealth, enemy.health, enemy.playerDamage, enemy.apDmg, enemy.adDmg, enemy.range, enemy.speed, enemy.jsonPath, enemy.imagePath)
