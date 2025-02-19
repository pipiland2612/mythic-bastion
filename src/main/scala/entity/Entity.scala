package entity

import entity.creature.Creature
import utils.Animation

import java.awt.Graphics2D

abstract class Entity:

  var pos: (Int, Int)
  val apDmg: Int
  val adDmg: Int
  val range: Int

  var currentAnimation: Option[Animation] = None

  def attack(creature: Creature): Unit = {}

  def update(): Unit = {}

  def draw(g2d: Graphics2D): Unit =
    currentAnimation.foreach(animation =>
      g2d.drawImage(animation.getCurrentFrame, pos._1, pos._2, null)
    )
