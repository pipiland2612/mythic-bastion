package entity.creature.enemy

import game.GamePanel

case class Creep(
  name: String,
  maxHealth: Double,
  var health: Double,
  playerDamage: Double,
  apDmg: Double,
  apDefense: Double,
  adDmg: Double,
  adDefense: Double,
  range: Double,
  speed: Double,
  maxAttackCoolDown: Double,
  jsonPath: String,
  imagePath: String,
  gp: GamePanel,
  var pos: (Double, Double) = (0, 0)
) extends Enemy(gp):

  this.haveReachBase = false
  currentAnimation = Some(idleAnimation)
end Creep

// data = [maxHealth, health, playerDamage, apDmg, apDefense, adDmg, adDefense, range, speed, attackCoolDown]
object Monster01:
  val data: Vector[Double] = Vector(10, 10, 1, 0, 0, 5, 2, 2, 0.25, 1 * 60)
  val name: String = "Monster01"
  val jsonPath: String = "enemies/Monster01.json"
  val imagePath: String = "enemies/Monster01.png"

object Monster02:
  val data: Vector[Double] = Vector(10, 10, 1, 20, 10, 0, 0, 5, 0.5, 1 * 60)
  val name: String = "Monster02"
  val jsonPath: String = "enemies/Monster02.json"
  val imagePath: String = "enemies/Monster02.png"

object Monster03:
  val data: Vector[Double] = Vector(1000, 1000, 1, 0, 0 , 10, 5, 1, 0.5, 1 * 60)
  val name: String = "Monster03"
  val jsonPath: String = "enemies/Monster03.json"
  val imagePath: String = "enemies/Monster03.png"