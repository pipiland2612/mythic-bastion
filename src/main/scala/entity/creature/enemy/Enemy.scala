package entity.creature.enemy

import entity.creature.{Creature, Direction}
import utils.{Animation, Tools}

import java.awt.Graphics2D
import java.awt.image.BufferedImage

abstract class Enemy extends Creature:

  var name: String
  var walkingAnimation: Animation = _
  var walkingUpAnimation: Animation = _
  var walkingDownAnimation: Animation = _
  var idleAnimation: Animation = _
  var fightingAnimation: Animation = _
  var deadAnimation: Animation = _

  val scaleFactor: Int = 2
  val playerDamage: Int
  val jsonPath, imagePath: String

  def enemyParse(): Unit =
    Tools.parser(jsonPath, imagePath, scaleFactor) match
      case Some(value) =>
        walkingAnimation = Animation(value(0), 20)
        walkingUpAnimation = Animation(value(1), 20)
        walkingDownAnimation = Animation(value(2), 20)
        idleAnimation = Animation(value(3), 20)
        fightingAnimation = Animation(value(4), 20)
        deadAnimation = Animation(value(5), 20)
      case _ =>

  def attackPlayer(): Unit = {}

  def followPath(map: Vector[(Int, Int)]): Unit =
    var index = 0
    while index <= map.length do
      val currentGoal: (Int, Int) = map(index)
      while this.pos != currentGoal do
        val xDist: Int = Math.abs(this.pos._1 - currentGoal._1)
        val yDist: Int = Math.abs(this.pos._2 - currentGoal._2)
        if xDist > yDist then
          if currentGoal._1 < this.pos._1 then
            this.direction = Direction.LEFT
          else
            this.direction = Direction.RIGHT
        else if xDist < yDist then
          if currentGoal._2 < this.pos._2 then
            this.direction = Direction.UP
          else
            this.direction = Direction.DOWN
      index += 1

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
  
    enemyData.get(key).map { case (initialData, jsonData, imageData) =>
      val data: Vector[Int] = initialData.map(_ * difficulty)
      Creep(key, data(0), data(1), data(2), data(3), data(4), data(5), data(6) / difficulty, jsonData, imageData)
    }


  def clone(enemy: Enemy): Enemy =
    Creep(enemy.name, enemy.maxHealth, enemy.health, enemy.playerDamage, enemy.apDmg, enemy.adDmg, enemy.range, enemy.speed, enemy.jsonPath, enemy.imagePath)
