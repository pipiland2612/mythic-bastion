package entity

import entity.creature.Creature
import game.GamePanel
import utils.Animation

import java.awt.Graphics2D
import java.awt.geom.AffineTransform

abstract class Entity(gp: GamePanel):

  var pos: (Double, Double)
  val apDmg: Double
  val adDmg: Double
  val range: Double

  var currentAnimation: Option[Animation] = None
  val transform = new AffineTransform()

  def setUp(): Unit = {}

  def attack(creature: Creature): Unit = {}

  def update(): Unit = {}

  def draw(g2d: Graphics2D): Unit =
    currentAnimation.foreach(animation =>
      transform.setToTranslation(pos._1, pos._2)
      g2d.drawImage(animation.getCurrentFrame, transform, null)
    )
