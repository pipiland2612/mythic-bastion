package entity.tower

import entity.{Attacker, Entity}
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.Tools

import java.awt.Graphics2D
import java.awt.geom.AffineTransform


abstract class Tower(gp: GamePanel, var level: Int) extends Entity(gp) with Attacker:
  var pos: (Double, Double) = (0,0)

  protected val apDmg: Double
  protected val adDmg: Double
  protected val range: Double

  override def getName: String = s"$name"
  override def getImagePath: String = s"towers/${getName}.png"
  override def getJsonPath: String = s"towers/${getName}.json"
  override def getAdDmg: Double = adDmg * level
  override def getApDmg: Double = apDmg * level
  override def getRange: Double = range * level
  private val transform: AffineTransform = AffineTransform()
  protected val offsetX: Double = 0
  protected val offsetY: Double = -10

  def attack(enemy: Enemy): Unit = {}


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