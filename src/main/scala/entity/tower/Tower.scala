package entity.tower

import entity.{Entity, State}
import entity.creature.enemy.Enemy
import entity.weapon.Weapon
import game.GamePanel
import gui.Image
import utils.{Animation, Tools}

import java.awt.{Color, Graphics2D}
import java.awt.geom.{AffineTransform, Ellipse2D}
import scala.collection.mutable.ListBuffer


abstract class Tower(gp: GamePanel, var level: Int) extends Entity(gp):

  override def getName: String = s"$name"
  override def getImagePath: String = s"towers/${getName}.png"
  override def getJsonPath: String = s"towers/${getName}.json"
  override def getRange: Double = range * level

  private val transform: AffineTransform = AffineTransform()
  private var attackCounter: Int = 0
  private var prepareCounter: Int = 0
  private var hasShoot = false

  protected var towerImage = Tools.loadImage(s"towers/ArrowTower0$level.png")
  protected val offsetX: Double = 0
  protected val offsetY: Double = -10
  protected val bulletList: ListBuffer[Weapon] = ListBuffer()
  protected var shootAnimation: Animation = _
  protected val weaponType: String
  protected val maxAttackCounter: Int
  protected val maxPrepareCounter: Int

  val centerCoords: (Double, Double) = Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame)
  val attackCircle: Ellipse2D =
    new Ellipse2D.Double(
      centerCoords._1 - (getRange*2 - idleAnimation.getCurrentFrame.getWidth())/2,
      centerCoords._2 - (getRange*4/3 - idleAnimation.getCurrentFrame.getHeight())/2,
      getRange*2, getRange*4/3
    )
  var isShowingRange: Boolean = false

  def updateTowerImage(): Unit =
    this.towerImage = Tools.loadImage(s"towers/ArrowTower0$level.png")

  def getBulletList: List[Weapon] = bulletList.toList

  override def update(): Unit =
    if attackCoolDown > 0 then
      attackCoolDown -= 1
    super.update()
    findEnemy().headOption.foreach(attack(_))
    handleAttackState()
    bulletList.toList.foreach(_.update())
    bulletList.filterInPlace(!_.hit)
    handlePrepareState()

  override def draw(g2d: Graphics2D): Unit =
    bulletList.toList.foreach(_.draw(g2d))
    if isShowingRange then
      g2d.setColor(Color.RED)
      g2d.draw(attackCircle)
    Tools.drawFrame(g2d, this.towerImage, transform, centerCoords, offsetX, offsetY)
    currentAnimation match
      case Some(animation) =>
        // Draw archer, wizard
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform, centerCoords, offsetX + 10, offsetY + 100)
      case _ =>

  private def findEnemy(): ListBuffer[Enemy] =
    gp.getSystemHandler.getGrid.scanForEnemiesInRange(this)
 
  private def attack(enemy: Enemy): Unit =
    if attackCoolDown <= 0 && this.state != State.PREPARE then
      state = State.ATTACK
      attackCoolDown = maxAttackCoolDown
      needsAnimationUpdate = true

      if shootAnimation.isInAttackInterval && !hasShoot then
        val pos = (centerCoords._1 + idleAnimation.getCurrentFrame.getWidth() / 2, centerCoords._2 + idleAnimation.getCurrentFrame.getHeight() / 4)
        val bullet = Weapon.clone(weaponType, enemy, pos)
        bulletList += bullet
        hasShoot = true

  private def handleAttackState(): Unit =
    if this.state == State.ATTACK then
      attackCounter += 1
      if (attackCounter >= maxAttackCounter) then
        attackCounter = 0
        currentAnimation.foreach(_.reset())
        state = State.PREPARE
        hasShoot = false
      needsAnimationUpdate = true

  private def handlePrepareState(): Unit =
    if this.state == State.PREPARE then
      prepareCounter += 1
      if prepareCounter >= maxPrepareCounter then
        currentAnimation.foreach(_.reset())
        prepareCounter = 0
        this.state = State.IDLE
      needsAnimationUpdate = true