package entity.creature.enemy

case class Creep(name: String, maxHealth: Int, var health: Int, playerDamage: Int, apDmg: Int, adDmg: Int, range: Int, speed: Int, jsonPath: String, imagePath: String) extends Enemy:
  var pos: (Int, Int) = (100, 100)
  enemyParse()
  currentAnimation = idleAnimation
end Creep

// data = [maxHealth, health, playerDamage, apDmg, adDmg, range, speed]
object Monster01:
  val data: Vector[Int] = Vector(100, 100, 1, 0, 10, 2, 3)
  val name: String = "Monster01"
  val jsonPath: String = "enemies/Monster01.json"
  val imagePath: String = "enemies/Monster01.png"

object Monster02:
  val data: Vector[Int] = Vector(100, 100, 1, 20, 0, 5, 10)
  val name: String = "Monster02"
  val jsonPath: String = "enemies/Monster01.json"
  val imagePath: String = "enemies/Monster01.png"

object Monster03:
  val data: Vector[Int] = Vector(1000, 1000, 1, 0, 10, 1, 3)
  val name: String = "Monster03"
  val jsonPath: String = "enemies/Monster01.json"
  val imagePath: String = "enemies/Monster01.png"