package entity.tower

import entity.{Attacker, Entity, State}
import entity.creature.enemy.Enemy
import entity.weapon.Weapon
import game.GamePanel
import utils.Tools

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

  def attack(enemy: Enemy): Unit =
    if this.state != State.ATTACK && attackCoolDown <= 0 then
      this.state = State.ATTACK
      needsAnimationUpdate = true
      attackCoolDown = maxAttackCoolDown

      val bullet: Weapon = Weapon.clone(weaponType, enemy, pos)
      bulletList += bullet
      this.state = State.IDLE

  override def update(): Unit =
    if attackCoolDown > 0 then
      attackCoolDown -= 1
    super.update()
    TowerScan.findEnemy().foreach(attack(_))
    bulletList.toList.foreach(_.update())

    bulletList.filterInPlace(bullet => !bullet.hasHit)

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
        centerCoords._1 - (getRange*2 - idleAnimation.getCurrentFrame.getWidth())/2,
        centerCoords._2 - (getRange*4/3 - idleAnimation.getCurrentFrame.getHeight())/2,
        getRange*2, getRange*4/3
      )
    var isShowingRange: Boolean = false

    def findEnemy(): Option[Enemy] =
      gp.stageManager.currentStage.flatMap(stage =>
        stage.enemyList.find(enemy =>
          val (x, y) = (enemy.attackBox.getCenterX, enemy.attackBox.getCenterY)
          attackCircle.contains(x, y)
        )
      )

    def draw(g2d: Graphics2D): Unit =
      if isShowingRange then
        g2d.setColor(Color.RED)
        g2d.draw(attackCircle)
