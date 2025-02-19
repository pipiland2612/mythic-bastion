package entity

import entity.creature.Creature
import utils.Animation

import java.awt.Graphics2D
import java.awt.image.BufferedImage

abstract class Entity:

  var pos: (Int, Int)
  val apDmg: Int
  val adDmg: Int
  val range: Int

  var currentAnimation: Option[Animation] = None

  def attack(creature: Creature): Unit = {}

  def update(): Unit = {}

  // TODO: Implement this
  def draw(g2d: Graphics2D): Unit =
    val image: BufferedImage = currentAnimation match
      case Some(animation) => animation.getCurrentFrame
      case None => return 
    g2d.drawImage(image, pos._1, pos._2, null)
