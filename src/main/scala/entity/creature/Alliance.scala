package entity.creature

import game.GamePanel

abstract class Alliance(gp: GamePanel) extends Creature(gp):
  val maxHealth: Double
  var health: Double

