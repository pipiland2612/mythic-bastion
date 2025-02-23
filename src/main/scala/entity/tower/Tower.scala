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
  protected val attackDuration: Int
  protected val prepareDuration: Int
  val maxAttackCoolDown = 0
  val centerCoords: (Double, Double) = Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame)
  var bulletList: ListBuffer[Weapon] = ListBuffer()

  protected val weaponType: String
  private var attackCounter: Int = 0
  private var prepareCounter: Int = 0
  private var hasShoot = false
  protected var shootAnimation: Animation = _

  def attack(enemy: Enemy): Unit =
    if attackCoolDown <= 0 && this.state != State.PREPARE then
      state = State.ATTACK
      attackCoolDown = maxAttackCoolDown
      needsAnimationUpdate = true

      if shootAnimation.isInAttackInterval && !hasShoot then
        val bullet = Weapon.clone(weaponType, enemy, pos)
        bulletList += bullet
        hasShoot = true

  def handleAttackState(): Unit =
    if this.state == State.ATTACK then
      attackCounter += 1
      if (attackCounter >= attackDuration) then
        attackCounter = 0
        currentAnimation.foreach(_.reset())
        state = State.PREPARE
        hasShoot = false
      needsAnimationUpdate = true

  def handlePrepareState(): Unit =
    if this.state == State.PREPARE then
      prepareCounter += 1
      if prepareCounter >= prepareDuration then
        currentAnimation.foreach(_.reset())
        prepareCounter = 0
        this.state = State.IDLE
      needsAnimationUpdate = true

  override def update(): Unit =
    if attackCoolDown > 0 then
      attackCoolDown -= 1
    super.update()
    TowerScan.findEnemy().foreach(this.attack(_))
    handleAttackState()
    bulletList.toList.foreach(_.update())
    bulletList.filterInPlace(!_.hasHit)
    handlePrepareState()

  override def draw(g2d: Graphics2D): Unit =
    TowerScan.draw(g2d)
    bulletList.toList.foreach(_.draw(g2d))
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform,
          centerCoords, offsetX, offsetY)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform,
          centerCoords, offsetX, offsetY)

  object TowerScan:
    private val attackCircle: Ellipse2D =
      new Ellipse2D.Double(
        // The shape is an ellipse with height = 2/3 * width
        // Set the center coords back to make the range in the middle of the tower
        centerCoords._1 - (getRange*2 - idleAnimation.getCurrentFrame.getWidth())/2,
        centerCoords._2 - (getRange*4/3 - idleAnimation.getCurrentFrame.getHeight())/2,
        getRange*2, getRange*4/3
      )
    var isShowingRange: Boolean = false

    def findEnemy(): Option[Enemy] =
      gp.stageManager.currentStage.flatMap(stage =>
        stage.enemyList.toList.find(enemy =>
          val (x, y) = (enemy.attackBox.getCenterX, enemy.attackBox.getCenterY)
          attackCircle.contains(x, y)
        )
      )

    def draw(g2d: Graphics2D): Unit =
      if isShowingRange then
        g2d.setColor(Color.RED)
        g2d.draw(attackCircle)
