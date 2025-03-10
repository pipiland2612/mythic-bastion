package entity.tower

import entity.{Entity, State}
import entity.creature.enemy.Enemy
import entity.weapon.Weapon
import game.GamePanel
import utils.{Animation, SoundConstant, Tools}

import java.awt.{Color, Graphics2D}
import java.awt.geom.{AffineTransform, Ellipse2D}
import scala.collection.mutable.ListBuffer
import scala.util.Random

abstract class Tower(val gp: GamePanel, var level: Int) extends Entity(gp):
  this.currentAnimation = Some(idleAnimation)

  override def getName: String = s"$name"
  override def getImagePath: String = s"towers/${getName}.png"
  override def getJsonPath: String = s"towers/${getName}.json"
  override def getRange: Double = (range + level * 5) * getRangeMultiplier

  private val transform: AffineTransform = AffineTransform()
  private var attackCounter: Int = 0
  private var hasShoot = false
  private var prepareCounter: Int = 0

  protected val towerType: String
  protected val towerImagePath: String
  protected var towerImage = Tools.loadImage(s"towers/${towerImagePath}0$level.png")
  protected val offsetX: Double = 0
  protected val offsetY: Double = -10
  protected val drawOffsetX: Double = 25
  protected val drawOffsetY: Double = 15
  protected val bulletList: ListBuffer[Weapon] = ListBuffer()
  protected var shootAnimation: Animation = _
  protected val weaponType: String
  protected val maxAttackCounter: Int
  protected val maxPrepareCounter: Int
  protected val readySoundEffect: Array[String]

  val centerCoords: (Double, Double) = calculateCenterCoords()
  val attackCircle: Ellipse2D = createAttackCircle()
  var isShowingRange: Boolean = false

  protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy]

  protected def getRangeMultiplier: Double

  def bulletPosition: (Double, Double) =
    val frame = idleAnimation.getCurrentFrame
    (centerCoords._1 + frame.getWidth() / 2, centerCoords._2 + frame.getHeight() / 4)

  def updateTowerImage(): Unit =
    this.towerImage = Tools.loadImage(s"towers/${towerImagePath}0$level.png")

  def getTowerType: String = towerType
  def getBulletList: List[Weapon] = bulletList.toList

  override def update(): Unit =
    updateAttackCooldown()
    super.update()
    handleEnemyAttack()
    updateBullets()
    handleAttackState()
    handlePrepareState()

  override def draw(g2d: Graphics2D): Unit =
    drawBullets(g2d)
    drawRangeCircle(g2d)
    Tools.drawFrame(g2d, this.towerImage, transform, centerCoords, offsetX, offsetY)
    currentAnimation.foreach(anim =>
      Tools.drawFrame(g2d, anim.getCurrentFrame, transform, centerCoords, drawOffsetX, drawOffsetY)
    )

  private def calculateCenterCoords(): (Double, Double) =
    if Option(idleAnimation).isDefined then
      Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame)
    else
      Tools.getCenterCoords(pos, towerImage)

  private def createAttackCircle(): Ellipse2D =
    val image = if Option(idleAnimation).isDefined then idleAnimation.getCurrentFrame else towerImage
    new Ellipse2D.Double(
      centerCoords._1 - (getRange * 2 - image.getWidth()) / 2,
      centerCoords._2 - (getRange * 4 / 3 - image.getHeight()) / 2,
      getRange * 2,
      getRange * 4 / 3
    )

  private def updateAttackCooldown(): Unit =
    if attackCoolDown > 0 then
      attackCoolDown -= 1

  private def findEnemy(): Option[ListBuffer[Enemy]] =
    gp.getSystemHandler.getStageManager.getGrid match
      case Some(grid) =>
        Some(grid.scanForEnemiesInRange(this))
      case _ => None

  private def attack(enemy: Enemy): Unit =
    if attackCoolDown <= 0 && this.state != State.PREPARE then
      state = State.ATTACK
      attackCoolDown = maxAttackCoolDown
      needsAnimationUpdate = true
      if shootAnimation.isInAttackInterval && !hasShoot then
        val pos = bulletPosition
        val bullet = Weapon.clone(weaponType, enemy, pos)
        bulletList += bullet
        val se = bullet.getFlySE
        if se.nonEmpty then
          val random = Random.nextInt(se.length)
          gp.getSystemHandler.playSE(se(random))
        hasShoot = true

  private def handleEnemyAttack(): Unit =
    findEnemy() match
      case Some(list) if list.nonEmpty => chooseEnemy(list).foreach(attack(_))
      case _ =>

  private def updateBullets(): Unit =
    bulletList.toList.foreach(_.update())
    bulletList.filterInPlace(!_.hit)

  private def handleAttackState(): Unit =
    if this.state == State.ATTACK then
      attackCounter += 1
      if attackCounter >= maxAttackCounter then
        attackCounter = 0
        currentAnimation.foreach(_.reset())
        state = State.PREPARE
        hasShoot = false
      needsAnimationUpdate = true

  private var hasPlayReadySound: Boolean = false
  private def handlePrepareState(): Unit =
    if this.state == State.PREPARE then
      prepareCounter += 1
      if prepareCounter >= maxPrepareCounter then
        currentAnimation.foreach(_.reset())
        prepareCounter = 0
        this.state = State.IDLE
      needsAnimationUpdate = true

  private def playReadySound(): Unit =
    if readySoundEffect.nonEmpty then
      val random = Random.nextInt(readySoundEffect.length)
      gp.getSystemHandler.playSE(readySoundEffect(random))
      hasPlayReadySound = true

  private def drawBullets(g2d: Graphics2D): Unit =
    bulletList.toList.foreach(_.draw(g2d))

  protected def drawRangeCircle(g2d: Graphics2D): Unit =
    if isShowingRange then
      g2d.setColor(Color.RED)
      g2d.draw(attackCircle)

object Tower:
  def moneyToLevelUp(tower: Tower, level: Int): Option[Int] =
    tower.getTowerType match
      case BarrackTower.towerType =>
        BarrackTower.updatePrice(level)
      case ArrowTower.towerType =>
        ArrowTower.updatePrice(level)
      case MagicTower.towerType =>
        MagicTower.updatePrice(level)
      case ExploTower.towerType =>
        ExploTower.updatePrice(level)
      case _ => None

  def levelUp(tower: Tower, currentLevel: Int): Tower =
    tower.towerType match
      case BarrackTower.towerType => BarrackTower(tower.gp, currentLevel + 1, tower.getPosition)
      case ArrowTower.towerType => ArrowTower(tower.gp, currentLevel + 1, tower.getPosition)
      case MagicTower.towerType => MagicTower(tower.gp, currentLevel + 1, tower.getPosition)
      case ExploTower.towerType => ExploTower(tower.gp, currentLevel + 1, tower.getPosition)