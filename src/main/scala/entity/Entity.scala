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

  var currentAnimation: Animation = _

  def attack(creature: Creature): Unit = {}

  def update(): Unit = {}

  // TODO: Implement this
  def draw(g2d: Graphics2D): Unit =
    val image: BufferedImage = currentAnimation.getCurrentFrame
    g2d.drawImage(image, pos._1, pos._2, null)
