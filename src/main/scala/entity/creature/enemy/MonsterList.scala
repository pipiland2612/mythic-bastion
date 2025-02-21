package entity.creature.enemy

import game.GamePanel

case class Creep(
  name: String,
  maxHealth: Double,
  var health: Double,
  playerDamage: Double,
  apDmg: Double,
  adDmg: Double,
  range: Double,
  speed: Double,
  jsonPath: String,
  imagePath: String,
  gp: GamePanel
) extends Enemy(gp):

  var pos: (Double, Double) = (0, 0)
  this.haveReachBase = false
  currentAnimation = Some(idleAnimation)
end Creep

// data = [maxHealth, health, playerDamage, apDmg, adDmg, range, speed]
object Monster01:
  val data: Vector[Double] = Vector(100, 100, 1, 0, 10, 2, 0.5)
  val name: String = "Monster01"
  val jsonPath: String = "enemies/Monster01.json"
  val imagePath: String = "enemies/Monster01.png"

object Monster02:
  val data: Vector[Double] = Vector(100, 100, 1, 20, 0, 5, 1)
  val name: String = "Monster02"
  val jsonPath: String = "enemies/Monster02.json"
  val imagePath: String = "enemies/Monster02.png"

object Monster03:
  val data: Vector[Double] = Vector(1000, 1000, 1, 0, 10, 1, 0.5)
  val name: String = "Monster03"
  val jsonPath: String = "enemies/Monster03.json"
  val imagePath: String = "enemies/Monster03.png"