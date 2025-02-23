package entity.tower

import entity.{Entity, State}
import entity.creature.enemy.Enemy
import entity.weapon.Weapon
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.{Color, Graphics2D}
import java.awt.geom.{AffineTransform, Ellipse2D}
import scala.collection.mutable.ListBuffer


abstract class Tower(gp: GamePanel, var level: Int) extends Entity(gp):

  override def getName: String = s"$name"
  override def getImagePath: String = s"towers/${getName}.png"
  override def getJsonPath: String = s"towers/${getName}.json"
  override def getRange: Double = range * level
  private val offsetX: Double = 0
  private val offsetY: Double = -10
  private val transform: AffineTransform = AffineTransform()
  val centerCoords: (Double, Double) = Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame)

  var bulletList: ListBuffer[Weapon] = ListBuffer()

  protected val weaponType: String
  var attackCounter: Int = 0
  var prepareCounter: Int = 0
  var shootAnimation: Animation = _
  var hasShoot = false

  val attackCircle: Ellipse2D =
    new Ellipse2D.Double(
      centerCoords._1 - (getRange*2 - idleAnimation.getCurrentFrame.getWidth())/2,
      centerCoords._2 - (getRange*4/3 - idleAnimation.getCurrentFrame.getHeight())/2,
      getRange*2, getRange*4/3
    )
  var isShowingRange: Boolean = false

  def attack(enemy: Enemy): Unit =
    if attackCoolDown <= 0 && this.state != State.PREPARE then
      state = State.ATTACK
      attackCoolDown = maxAttackCoolDown
      needsAnimationUpdate = true

      if shootAnimation.isInAttackInterval && !hasShoot then
        val pos = (centerCoords._1 + idleAnimation.getCurrentFrame.getWidth() / 2, centerCoords._2 + idleAnimation.getCurrentFrame.getHeight() / 4)
        val bullet = Weapon.clone(weaponType, enemy, pos)
        bulletList += bullet
        hasShoot = true

  def handleAttackState(): Unit =
    if this.state == State.ATTACK then
      attackCounter += 1
      if (attackCounter >= 100) then
        attackCounter = 0
        currentAnimation.foreach(_.reset())
        state = State.PREPARE
        hasShoot = false
      needsAnimationUpdate = true

  def handlePrepareState(): Unit =
    if this.state == State.PREPARE then
      prepareCounter += 1
      if prepareCounter >= 70 then
        currentAnimation.foreach(_.reset())
        prepareCounter = 0
        this.state = State.IDLE
      needsAnimationUpdate = true

  override def update(): Unit =
    if attackCoolDown > 0 then
      attackCoolDown -= 1
    super.update()
    findEnemy().headOption.foreach(attack(_))
    handleAttackState()
    bulletList.toList.foreach(_.update())
    bulletList.filterInPlace(!_.hasHit)
    handlePrepareState()

  override def draw(g2d: Graphics2D): Unit =
    bulletList.toList.foreach(_.draw(g2d))
    if isShowingRange then
      g2d.setColor(Color.RED)
      g2d.draw(attackCircle)
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform,
          centerCoords, offsetX, offsetY)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform,
          centerCoords, offsetX, offsetY)
//    gp.systemHandler.grid.draw(g2d, this)

  def findEnemy(): ListBuffer[Enemy] =
    gp.systemHandler.grid.scanForEnemiesInRange(this)
