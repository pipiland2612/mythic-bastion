package entity.creature.enemy

import entity.creature.Creature
import game.GamePanel
import utils.{Animation, Tools}
import entity.{Direction, State}

import java.awt.Graphics2D
import java.awt.image.BufferedImage

abstract class Enemy(gp: GamePanel) extends Creature(gp):
  protected val playerDamage: Double

  var haveReachBase: Boolean = false
  var walkingAnimation: Animation = _
  var walkingUpAnimation: Animation = _
  var walkingDownAnimation: Animation = _
  var fightingAnimation: Animation = _
  var deadAnimation: Animation = _
  scaleFactor = 1.25

  private var path: Option[Vector[(Double, Double)]] = None
  private var index = 0

  def setPath(path: Vector[(Double, Double)]) = this.path = Some(path)

  override def setUpImages(): Unit =
    val mirroredDirections = Seq(Direction.LEFT, Direction.UP_LEFT, Direction.DOWN_LEFT)
    this.images =
      Tools.fillMap(Direction.allCreatureDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(mirroredDirections, State.RUN, Tools.flipAnimation(walkingAnimation)) ++
      Tools.fillMap(Direction.allCreatureDirections.diff(mirroredDirections), State.RUN, walkingAnimation) ++
      Map(
        (Direction.DOWN, State.RUN) -> walkingDownAnimation,
        (Direction.UP, State.RUN) -> walkingUpAnimation
      ) ++
      Tools.fillMap(Direction.allCreatureDirections, State.ATTACK, fightingAnimation) ++
      Tools.fillMap(Direction.allCreatureDirections, State.DEAD, deadAnimation)

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    walkingAnimation = Animation(value(0), 10)
    walkingUpAnimation = Animation(value(1), 10)
    walkingDownAnimation = Animation(value(2), 10)
    idleAnimation = Animation(value(3), 10)
    fightingAnimation = Animation(value(4), 10)
    deadAnimation = Animation(value(5), 10)

  def attackPlayer(): Unit =
    gp.stageManager.currentPlayer.foreach(player =>
      player.updateHealth(-(this.playerDamage.toInt))
    )

  def followPath(goal: (Double, Double)): Unit =
    val (xDist, yDist) = (goal._1 - this.pos._1, goal._2 - this.pos._2)
    val absX = Math.abs(xDist)
    val absY = Math.abs(yDist)

    if (absX <= this.speed && absY <= this.speed) then
      index += 1
      return

    // Move diagonally if it brings us closer
    if (absX > 0 && absY > 0) then
      direction =
        if (xDist < 0 && yDist < 0) Direction.UP_LEFT
        else if (xDist > 0 && yDist < 0) Direction.UP_RIGHT
        else if (xDist < 0 && yDist > 0) Direction.DOWN_LEFT
        else Direction.DOWN_RIGHT
    else
      direction =
        if (absX > absY)
          if (xDist < 0) Direction.LEFT else Direction.RIGHT
        else
          if (yDist < 0) Direction.UP else Direction.DOWN

  override def update(): Unit =
    super.update()
    if this.state != State.DEAD then
      path.foreach(path =>
        if index < path.length then
          followPath(path(index))
          continueMove()
        else this.haveReachBase = true
      )

      if this.haveReachBase then attackPlayer()


end Enemy

object Enemy:

  var gp: GamePanel = _

  def enemyOfName(key: String, difficulty: Int): Option[Enemy] =
    val enemyData = Map(
      Monster01.name -> (Monster01.data, Monster01.jsonPath, Monster01.imagePath),
      Monster02.name -> (Monster02.data, Monster02.jsonPath, Monster02.imagePath),
      Monster03.name -> (Monster03.data, Monster03.jsonPath, Monster03.imagePath)
    )

    enemyData.get(key).map ((initialData, jsonData, imageData) =>
      val data: Vector[Double] = initialData.map(_ * difficulty)
      Creep(key, data(0), data(1), data(2), data(3), data(4), data(5), data(6), data(7), data(8) / difficulty, data(9), jsonData, imageData, gp)
    )

  def clone(enemy: Enemy): Enemy =
    Creep(
      enemy.getName, enemy.getMaxHealth, enemy.getHealth, enemy.playerDamage,
      enemy.getApDmg, enemy.getApDefense,enemy.getAdDmg, enemy.getAdDefense,
      enemy.getRange, enemy.getSpeed, enemy.maxAttackCoolDown, enemy.getJsonPath, enemy.getImagePath, gp
    )
