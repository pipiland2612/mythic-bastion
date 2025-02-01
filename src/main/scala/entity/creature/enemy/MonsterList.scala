package entity.creature.enemy

case class Creep(maxHealth: Int, var health: Int, playerDamage: Int, apDmg: Int, adDmg: Int, range: Int) extends Enemy:
  var pos: (Int, Int) = (0,0)

end Creep

// data = [maxHealth, health, playerDamage, apDmg, adDmg, range]
object Monster01:
  val data: Vector[Int] = Vector(100, 100, 1, 0, 10, 2)
  val name: String = "Monster01"

object Monster02:
  val data: Vector[Int] = Vector(100, 100, 1, 20, 0, 5)
  val name: String = "Monster02"

object Monster03:
  val data: Vector[Int] = Vector(1000, 1000, 1, 0, 10, 1)
  val name: String = "Monster03"