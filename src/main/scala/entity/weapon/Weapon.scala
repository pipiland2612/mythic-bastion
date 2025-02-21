package entity.weapon

import entity.Entity
import game.GamePanel

abstract class Weapon(gp: GamePanel) extends Entity(gp: GamePanel):
  var pos: (Double, Double) = (0,0)
  protected val name: String
  protected val jsonPath, imagePath: String
  protected val apDmg: Double
  protected val adDmg: Double
  protected val range: Double
  protected val speed: Double

