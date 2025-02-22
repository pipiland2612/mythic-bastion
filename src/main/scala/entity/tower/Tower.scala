package entity.tower

import entity.{Attacker, Entity, State}
import entity.creature.enemy.Enemy
import entity.weapon.Weapon
import game.GamePanel
import utils.Tools

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import scala.collection.mutable.ListBuffer


abstract class Tower(gp: GamePanel, var level: Int) extends Entity(gp) with Attacker:
  var pos: (Double, Double) = (0,0)

  override def getName: String = s"$name"
  override def getImagePath: String = s"towers/${getName}.png"
  override def getJsonPath: String = s"towers/${getName}.json"
  override def getAdDmg: Double = adDmg * level
  override def getApDmg: Double = apDmg * level
  override def getRange: Double = range * level
  private val offsetX: Double = 0
  private val offsetY: Double = -10
  private val transform: AffineTransform = AffineTransform()

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

  override def draw(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform,
          Tools.getCenterCoords(pos, animation.getCurrentFrame), offsetX, offsetY)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform,
          Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame), offsetX, offsetY)
