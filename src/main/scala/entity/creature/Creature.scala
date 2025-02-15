package entity.creature

import entity.Entity

import java.awt.image.BufferedImage

abstract class Creature extends Entity:
  val maxHealth: Int
  var health: Int
  var state: State = State.IDLE
  var needsAnimationUpdate: Boolean = false
  
  def move(dx: Int, dy: Int): Unit =
    state = State.RUN
    this.pos = (pos._1 + dx, pos._2 + dy)
    needsAnimationUpdate = true
