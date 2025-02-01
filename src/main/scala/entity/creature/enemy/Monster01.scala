package entity.creature.enemy

case class Monster01(maxHealth: Int, var health: Int, playerDamage: Int, apDmg: Int, adDmg: Int, range: Int) extends Enemy:
  var pos: (Int, Int) = (0,0)

end Monster01

object Monster01:
  val data: Vector[Int] = Vector(100, 100, 1, 0, 10, 2)
  val name: String = "Monster01"