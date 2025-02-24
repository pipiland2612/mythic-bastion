package entity.creature.enemy

import entity.creature.Creature
import game.GamePanel
import utils.{Animation, Tools}
import entity.{Direction, State}

import java.awt.image.BufferedImage

abstract class Enemy(gp: GamePanel) extends Creature(gp):
  // immutable id to ensure giving exact hashcode
  private val id: Int = Enemy.nextId()

  def getId: Int = id

  protected val playerDamage: Double

  protected var walkingAnimation: Animation = _
  protected var walkingUpAnimation: Animation = _
  protected var walkingDownAnimation: Animation = _
  protected var fightingAnimation: Animation = _
  protected var deadAnimation: Animation = _
  scaleFactor = 1.25

  private var path: Option[Vector[(Double, Double)]] = None
  private var index = 0
  var haveReachBase: Boolean = false

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
    gp.systemHandler.stageManager.currentPlayer.foreach(player =>
      player.updateHealth(-(this.playerDamage.toInt))
    )

  private def followPath(goal: (Double, Double)): Unit =
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
      gp.systemHandler.grid.updateEnemyPosition(this, (lastPosition._1.toInt, lastPosition._2.toInt))
      if this.haveReachBase then attackPlayer()

    if this.health <= 0 then
      gp.systemHandler.grid.remove(this)

  override def hashCode(): Int = id.hashCode()
  override def equals(obj: Any): Boolean = obj match
    case other: Enemy => this.id == other.id
    case _            => false

end Enemy

object Enemy:

  var gp: GamePanel = _
  private var idCounter: Int = 0
  private def nextId(): Int =
    idCounter += 1
    idCounter

  def enemyOfName(key: String, difficulty: Int): Option[Enemy] =
    val enemyData = Map(
      Monster01.name -> (Monster01.data, Monster01.jsonPath, Monster01.imagePath, Monster01.rect),
      Monster02.name -> (Monster02.data, Monster02.jsonPath, Monster02.imagePath, Monster02.rect),
      Monster03.name -> (Monster03.data, Monster03.jsonPath, Monster03.imagePath, Monster03.rect)
    )

    enemyData.get(key).map ((initialData, jsonData, imageData, rect) =>
      val data: Vector[Double] = initialData.map(_ * difficulty)
      Creep(key, data(0), data(1), data(2), data(3), data(4), data(5), data(6), data(7), data(8) / difficulty, data(9), data(10), jsonData, imageData, rect, gp)
    )

  def clone(enemy: Enemy): Enemy =
    Creep(
      enemy.getName, enemy.getMaxHealth, enemy.getHealth, enemy.playerDamage,
      enemy.getApDmg, enemy.getApDefense,enemy.getAdDmg, enemy.getAdDefense,
      enemy.getRange, enemy.getSpeed, enemy.getMaxAttackCoolDown, enemy.getMaxDeadCounter, enemy.getJsonPath, enemy.getImagePath, enemy.getRect, gp
    )