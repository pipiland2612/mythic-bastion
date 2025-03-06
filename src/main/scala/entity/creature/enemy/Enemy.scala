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
  private var hasGiveCoin: Boolean = false
  protected val coin: Double

  protected var haveReachBase: Boolean = false
  protected val playerDamage: Double
  protected var walkingUpAnimation: Animation = _
  protected var walkingDownAnimation: Animation = _
  scaleFactor = 1.25
  isGoing = true

  def setPath(path: Vector[(Double, Double)]): Unit = this.path = Some(path)
  def haveReach: Boolean = haveReachBase

  def getCoin: Double = coin

  override def setUpImages(): Unit =
    val mirroredDirections = Seq(Direction.LEFT, Direction.UP_LEFT, Direction.DOWN_LEFT)
    val nonMirroredDirections = Direction.allCreatureDirections.diff(mirroredDirections)
    this.images = EnemyAnimationFactory.createEnemyAnimationMap(
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
    walkingAnimation = Animation(frames = value(0), frameDuration = 10)
    walkingUpAnimation = Animation(frames = value(1), frameDuration = 10)
    walkingDownAnimation = Animation(frames = value(2), frameDuration = 10)
    idleAnimation = Animation(frames = value(3), frameDuration = 10)
    fightingAnimation = Animation(frames = value(4), frameDuration = 10, attackStartFrame = 2, attackEndFrame = 8)
    deadAnimation = Animation(frames = value(5), frameDuration = 10)

  def attackPlayer(): Unit =
    gp.getSystemHandler.getStageManager.updateHealth(-(this.playerDamage.toInt))

  protected def findEnemy[T <: Creature](): Option[ListBuffer[T]] =
    gp.getSystemHandler.getStageManager.getGrid match
      case Some(grid) => Some(grid.scanForAlliancesInRange(this).asInstanceOf[ListBuffer[T]])
      case _ => None

  private def followPath(goal: (Double, Double)): Unit =
    val (xDist, yDist) = (goal._1 - this.pos._1, goal._2 - this.pos._2)
    val absX = Math.abs(xDist)
    val absY = Math.abs(yDist)

    if absX <= this.speed * 3 && absY <= this.speed * 3 then
      index += 1
      return

    direction = determineDirection(xDist, yDist)

  override def setAction(): Unit =
    super.setAction()
    if this.state != State.ATTACK then
      path.foreach(path =>
        if index < path.length then
          followPath(path(index))
          continueMove()
        else this.haveReachBase = true
      )

  override def update(): Unit =
    super.update()
    if this.state != State.DEAD then
      setAction()
      handleAttackAnimation()
      updateGridPosition()
      if this.haveReachBase then attackPlayer()
    checkHealthStatus()

  private def checkHealthStatus(): Unit =
    if this.health <= 0 then
      if !hasGiveCoin then
        hasGiveCoin = true
        gp.getSystemHandler.getStageManager.updateCoin(coin.toInt)
      removeGrid()

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
        speed = data.stats(7),
        maxAttackCoolDown = data.stats(8),
        maxDeadCounter = adjustedData(9),
        coin = data.stats(10),
        jsonPath = data.jsonPath,
        imagePath = data.imagePath,
        rect = data.rect,
        gp = gp,
        healthOffSet = data.healthOffSet
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
      coin = enemy.getCoin,
      jsonPath = enemy.getJsonPath,
      imagePath = enemy.getImagePath,
      rect = enemy.getRect,
      gp = gp,
      healthOffSet = enemy.getHealthOffSet
    )

private object EnemyAnimationFactory:
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

private object EnemyData:
  case class EnemyConfig(
    stats: Vector[Double],
    jsonPath: String,
    imagePath: String,
    rect: Rectangle2D,
    healthOffSet: (Int, Int)
  )

  val registry: Map[String, EnemyConfig] = Map(
    Monster01.name -> EnemyConfig(Monster01.data, Monster01.jsonPath, Monster01.imagePath, Monster01.rect, Monster01.healthOffSet),
    Monster02.name -> EnemyConfig(Monster02.data, Monster02.jsonPath, Monster02.imagePath, Monster02.rect, Monster02.healthOffSet),
    Monster03.name -> EnemyConfig(Monster03.data, Monster03.jsonPath, Monster03.imagePath, Monster03.rect, Monster03.healthOffSet),
    Monster04.name -> EnemyConfig(Monster04.data, Monster04.jsonPath, Monster04.imagePath, Monster04.rect, Monster04.healthOffSet),
    Monster05.name -> EnemyConfig(Monster05.data, Monster05.jsonPath, Monster05.imagePath, Monster05.rect, Monster05.healthOffSet),
    Monster06.name -> EnemyConfig(Monster06.data, Monster06.jsonPath, Monster06.imagePath, Monster06.rect, Monster06.healthOffSet),
    Monster07.name -> EnemyConfig(Monster07.data, Monster07.jsonPath, Monster07.imagePath, Monster07.rect, Monster07.healthOffSet),
    Monster08.name -> EnemyConfig(Monster08.data, Monster08.jsonPath, Monster08.imagePath, Monster08.rect, Monster08.healthOffSet),
    Monster09.name -> EnemyConfig(Monster09.data, Monster09.jsonPath, Monster09.imagePath, Monster09.rect, Monster09.healthOffSet),
    Monster10.name -> EnemyConfig(Monster10.data, Monster10.jsonPath, Monster10.imagePath, Monster10.rect, Monster10.healthOffSet),
    Monster11.name -> EnemyConfig(Monster11.data, Monster11.jsonPath, Monster11.imagePath, Monster11.rect, Monster11.healthOffSet),
    Monster12.name -> EnemyConfig(Monster12.data, Monster12.jsonPath, Monster12.imagePath, Monster12.rect, Monster12.healthOffSet),
    Monster13.name -> EnemyConfig(Monster13.data, Monster13.jsonPath, Monster13.imagePath, Monster13.rect, Monster13.healthOffSet),
    Monster14.name -> EnemyConfig(Monster14.data, Monster14.jsonPath, Monster14.imagePath, Monster14.rect, Monster14.healthOffSet),
    Monster15.name -> EnemyConfig(Monster15.data, Monster15.jsonPath, Monster15.imagePath, Monster15.rect, Monster15.healthOffSet)
  )