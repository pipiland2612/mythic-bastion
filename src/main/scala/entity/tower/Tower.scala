package entity.tower

import entity.Entity
import entity.creature.enemy.Enemy
import game.GamePanel

import java.awt.Graphics2D
import java.awt.geom.AffineTransform


abstract class Tower(gp: GamePanel, var level: Int) extends Entity(gp):
  var pos: (Double, Double) = (0,0)

  val towerType: String
  protected val apDmg: Double
  protected val adDmg: Double
  protected val range: Double

  override def getName: String = s"${towerType}0$level"
  override def getImagePath: String = s"towers/${getName}.png"
  override def getJsonPath: String = s"towers/${getName}.json"
  override def getAdDmg: Double = adDmg * level
  override def getApDmg: Double = apDmg * level
  override def getRange: Double = range * level
  private val transform: AffineTransform = AffineTransform()

  def attack(enemy: Enemy): Unit = {}

  override def draw(g2d: Graphics2D): Unit =
    def draw(g2d: Graphics2D): Unit =
    currentAnimation.foreach(animation =>
      val x = pos._1 - animation.getCurrentFrame.getWidth / 2
      val y = pos._2 - animation.getCurrentFrame.getHeight / 2
      transform.setToTranslation(x, y)
      g2d.drawImage(animation.getCurrentFrame, transform, None.orNull)
    )
