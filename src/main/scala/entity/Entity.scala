package entity

import entity.creature.Creature
import utils.Animation

import java.awt.Graphics2D
import java.awt.geom.AffineTransform

abstract class Entity:

  var pos: (Double, Double)
  val apDmg: Double
  val adDmg: Double
  val range: Double

  var currentAnimation: Option[Animation] = None

  def attack(creature: Creature): Unit = {}

  def update(): Unit = {}

  def draw(g2d: Graphics2D): Unit =
    currentAnimation.foreach(animation =>
      val transform = new AffineTransform()
      transform.translate(pos._1, pos._2)
      g2d.drawImage(animation.getCurrentFrame, transform, null)
    )
