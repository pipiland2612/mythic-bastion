package entity.creature.enemy

import entity.creature.Creature

abstract class Enemy extends Creature:

  val playerDamage: Int

  def attackPlayer(): Unit = {}

end Enemy

object Enemy:
  def enemyOfName(key: String, difficulty: Int): Enemy =
    key match
      case Monster01.name =>
        val data = Monster01.data
        Monster01(data(0), data(1), data(2), data(3), data(4), data(5))
      case _ => null