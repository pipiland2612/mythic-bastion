package entity.creature

import entity.Entity

import java.awt.image.BufferedImage

abstract class Creature extends Entity:
  val maxHealth: Int
  var health: Int
