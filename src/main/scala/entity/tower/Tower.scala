package entity.tower

import entity.{Attacker, Entity, State}
import entity.creature.enemy.Enemy
import entity.weapon.Weapon
import game.GamePanel
import utils.Tools

import java.awt.{Color, Graphics2D}
import java.awt.geom.{AffineTransform, Ellipse2D}
import scala.collection.mutable.ListBuffer


abstract class Tower(gp: GamePanel, var level: Int) extends Entity(gp) with Attacker:

  override def getName: String = s"$name"
  override def getImagePath: String = s"towers/${getName}.png"
  override def getJsonPath: String = s"towers/${getName}.json"
  override def getAdDmg: Double = adDmg * level
  override def getApDmg: Double = apDmg * level
  override def getRange: Double = range * level
  private val offsetX: Double = 0
  private val offsetY: Double = -10
  private val transform: AffineTransform = AffineTransform()
  private val centerCoords: (Double, Double) = Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame)
  private val attackCircle: Ellipse2D =
    new Ellipse2D.Double(
      centerCoords._1 - (getRange*2 - idleAnimation.getCurrentFrame.getWidth())/2,
      centerCoords._2 - (getRange*4/3 - idleAnimation.getCurrentFrame.getHeight())/2,
      getRange*2, getRange*4/3
    )
  var isShowingRange: Boolean = true

//  protected val attackCircle: Circle
  protected val weaponType: Weapon

  var bulletList: ListBuffer[Weapon] = ListBuffer()

  def dealDamage(enemy: Enemy): Unit =
    val adDamage = getAdDmg - enemy.getAdDefense
    val apDamge = getApDmg - enemy.getApDefense
    enemy.takeDamage(adDamage + apDamge)

  def attack(enemy: Enemy): Unit =
    this.state = State.ATTACK
    needsAnimationUpdate = true

  override def update(): Unit =
    super.update()
    gp.stageManager.currentStage.foreach(stage =>
      for enemy <- stage.enemyList do
        if attackCircle.contains(enemy.pos._1, enemy.pos._2) then
          println(s"Enemy ${enemy.getName} enters")
    )

  override def draw(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform,
          centerCoords, offsetX, offsetY)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform,
          centerCoords, offsetX, offsetY)

    if isShowingRange then
      g2d.setColor(Color.RED)
      g2d.draw(attackCircle)