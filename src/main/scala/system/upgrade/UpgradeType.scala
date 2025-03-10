package system.upgrade

abstract class PermanentUpgrade(
  val name: String,
  val towerType: String,
  val cost: Int,
) extends Serializable:
  override def equals(other: Any): Boolean = other match
    case that: PermanentUpgrade => this.name == that.name
    case _ => false
  override def hashCode(): Int = name.hashCode()

class MultiplierUpgrade(
  name: String,
  towerType: String,
  val effectType: String,
  val multiplier: Double,
  cost: Int
) extends PermanentUpgrade(name, towerType, cost):
  require(multiplier > 0, s"Multiplier must be positive, got $multiplier")

class AbilityUpgrade(
  name: String,
  towerType: String,
  val ability: String,
  val level: Int,
  cost: Int
) extends PermanentUpgrade(name, towerType, cost):
  require(level >= 0, s"Level must be non-negative, got $level")