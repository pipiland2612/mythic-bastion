package entity.tower

import entity.Entity
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.Tools

import java.awt.{Color, Graphics2D}
import java.awt.geom.AffineTransform


abstract class Tower(gp: GamePanel, var level: Int) extends Entity(gp):
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

  override def draw(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) =>
        val (x,y) = Tools.getCenterCoords(pos._1, pos._2, animation.getCurrentFrame)
        transform.setToTranslation(x, y)
        g2d.drawImage(animation.getCurrentFrame, transform, None.orNull)
      case _ =>
        val (x,y) = Tools.getCenterCoords(pos._1, pos._2, idleAnimation.getCurrentFrame)
        transform.setToTranslation(x + offsetX, y + offsetY)
        g2d.drawImage(idleAnimation.getCurrentFrame, transform, None.orNull)