package entity.creature.enemy

import entity.{Direction, State}
import entity.creature.Creature
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

abstract class Enemy(gp: GamePanel) extends Creature(gp):
  private var path: Option[Vector[(Double, Double)]] = None
  private var index = 0

  protected var haveReachBase: Boolean = false
  protected val playerDamage: Double
  protected var walkingUpAnimation: Animation = _
  protected var walkingDownAnimation: Animation = _
  scaleFactor = 1.25

  def setPath(path: Vector[(Double, Double)]): Unit = this.path = Some(path)
  def haveReach: Boolean = haveReachBase

  override def setUpImages(): Unit =
    val mirroredDirections = Seq(Direction.LEFT, Direction.UP_LEFT, Direction.DOWN_LEFT)
    val nonMirroredDirections = Direction.allCreatureDirections.diff(mirroredDirections)
    this.images = AnimationFactory.createEnemyAnimationMap(
      allDirections = Direction.allCreatureDirections,
      mirroredDirections = mirroredDirections,
      nonMirroredDirections = nonMirroredDirections,
      idleAnim = idleAnimation,
      walkAnim = walkingAnimation,
      walkUpAnim = walkingUpAnimation,
      walkDownAnim = walkingDownAnimation,
      fightAnim = fightingAnimation,
      deadAnim = deadAnimation
    )

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    walkingAnimation = Animation(value(0), frameDuration = 10)
    walkingUpAnimation = Animation(value(1), frameDuration = 10)
    walkingDownAnimation = Animation(value(2), frameDuration = 10)
    idleAnimation = Animation(value(3), frameDuration = 10)
    fightingAnimation = Animation(value(4), frameDuration = 10, attackStartFrame = 2, attackEndFrame = 8)
    deadAnimation = Animation(value(5), frameDuration = 10)

  def attackPlayer(): Unit =
    gp.getSystemHandler.getStageManager.getCurrentPlayer.foreach(player =>
      player.updateHealth(-(this.playerDamage.toInt))
    )

  protected def findEnemy[T <: Creature](): ListBuffer[T] =
    gp.getSystemHandler.getGrid.scanForAlliancesInRange(this).asInstanceOf[ListBuffer[T]]

  private def followPath(goal: (Double, Double)): Unit =
    val (xDist, yDist) = (goal._1 - this.pos._1, goal._2 - this.pos._2)
    val absX = Math.abs(xDist)
    val absY = Math.abs(yDist)

    if absX <= this.speed && absY <= this.speed then
      index += 1
      return

    direction = determineDirection(xDist, yDist, absX, absY)

  private def determineDirection(xDist: Double, yDist: Double, absX: Double, absY: Double): Direction =
    if absX > 0 && absY > 0 then
      if xDist < 0 && yDist < 0 then Direction.UP_LEFT
      else if xDist > 0 && yDist < 0 then Direction.UP_RIGHT
      else if xDist < 0 && yDist > 0 then Direction.DOWN_LEFT
      else Direction.DOWN_RIGHT
    else if absX > absY then
      if xDist < 0 then Direction.LEFT else Direction.RIGHT
    else if yDist < 0 then Direction.UP else Direction.DOWN

  override def setAction(): Unit =
    super.setAction()
    if this.state != State.ATTACK then
      path.foreach(path =>
        if index < path.length then
          followPath(path(index))
          continueMove()
        else
          this.haveReachBase = true
      )

  override def update(): Unit =
    super.update()
    if this.state != State.DEAD then
      setAction()
      handleAttackAnimation()
      updateGridPosition()
      if this.haveReachBase then attackPlayer()
    checkHealthStatus()

  private def updateGridPosition(): Unit =
    gp.getSystemHandler.getGrid.updateCreaturePosition(this, (lastPosition._1.toInt, lastPosition._2.toInt))

  private def checkHealthStatus(): Unit =
    if this.health <= 0 then
      gp.getSystemHandler.getGrid.remove(this)

end Enemy

object Enemy:
  private var gp: GamePanel = _

  def setUp(gp: GamePanel): Unit = this.gp = gp

  def enemyOfName(key: String, difficulty: Int): Option[Enemy] =
    EnemyData.registry.get(key).map(data =>
      val adjustedData = data.stats.map(_ * difficulty)
      Creep(
        name = key,
        maxHealth = adjustedData(0),
        playerDamage = adjustedData(1),
        apDmg = adjustedData(2),
        apDefense = adjustedData(3),
        adDmg = adjustedData(4),
        adDefense = adjustedData(5),
        range = adjustedData(6),
        speed = adjustedData(7),
        maxAttackCoolDown = adjustedData(8) / difficulty,
        maxDeadCounter = adjustedData(9),
        jsonPath = data.jsonPath,
        imagePath = data.imagePath,
        rect = data.rect,
        gp = gp
      )
    )

  def clone(enemy: Enemy): Enemy =
    Creep(
      name = enemy.getName,
      maxHealth = enemy.getMaxHealth,
      playerDamage = enemy.playerDamage,
      apDmg = enemy.getApDmg,
      apDefense = enemy.getApDefense,
      adDmg = enemy.getAdDmg,
      adDefense = enemy.getAdDefense,
      range = enemy.getRange,
      speed = enemy.getSpeed,
      maxAttackCoolDown = enemy.getMaxAttackCoolDown,
      maxDeadCounter = enemy.getMaxDeadCounter,
      jsonPath = enemy.getJsonPath,
      imagePath = enemy.getImagePath,
      rect = enemy.getRect,
      gp = gp
    )

object AnimationFactory:
  def createEnemyAnimationMap(
    allDirections: Seq[Direction],
    mirroredDirections: Seq[Direction],
    nonMirroredDirections: Seq[Direction],
    idleAnim: Animation,
    walkAnim: Animation,
    walkUpAnim: Animation,
    walkDownAnim: Animation,
    fightAnim: Animation,
    deadAnim: Animation
  ): Map[(Direction, State), Animation] =
    Tools.fillMap(allDirections, State.IDLE, idleAnim) ++
    Tools.fillMap(mirroredDirections, State.RUN, Tools.flipAnimation(walkAnim)) ++
    Tools.fillMap(nonMirroredDirections, State.RUN, walkAnim) ++
    Map(
      (Direction.DOWN, State.RUN) -> walkDownAnim,
      (Direction.UP, State.RUN) -> walkUpAnim
    ) ++
    Tools.fillMap(allDirections, State.ATTACK, fightAnim) ++
    Tools.fillMap(allDirections, State.DEAD, deadAnim)

object EnemyData:
  case class EnemyConfig(
    stats: Vector[Double],
    jsonPath: String,
    imagePath: String,
    rect: Rectangle2D
  )

  val registry: Map[String, EnemyConfig] = Map(
    Monster01.name -> EnemyConfig(Monster01.data, Monster01.jsonPath, Monster01.imagePath, Monster01.rect),
    Monster02.name -> EnemyConfig(Monster02.data, Monster02.jsonPath, Monster02.imagePath, Monster02.rect),
    Monster03.name -> EnemyConfig(Monster03.data, Monster03.jsonPath, Monster03.imagePath, Monster03.rect)
  )