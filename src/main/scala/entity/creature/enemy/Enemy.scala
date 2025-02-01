package entity.creature.enemy

import entity.creature.Creature

abstract class Enemy extends Creature:

  val playerDamage: Int

  def attackPlayer(): Unit = {}

end Enemy

object Enemy:

  def enemyOfName(key: String, difficulty: Int): Option[Enemy] =
    val initialData: Vector[Int] = key match
      case Monster01.name => Monster01.data
      case _ => return None
    val data: Vector[Int] = initialData.map(element => element * difficulty)
    Some(Creep(data(0), data(1), data(2), data(3), data(4), data(5)))
