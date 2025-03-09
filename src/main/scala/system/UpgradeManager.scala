package system
import java.io.{File, FileOutputStream, ObjectInputStream, ObjectOutputStream, FileInputStream}
import scala.collection.mutable

case class PermanentUpgrade(
  name: String,
  towerType: String,
  effectType: String,
  level: Int,
  cost: Int,
  effectMultiplier: Double
) extends Serializable

class UpgradeManager:
  private val purchasedUpgrades: mutable.Map[String, List[PermanentUpgrade]] = mutable.Map()

  def purchaseUpgrade(upgrade: PermanentUpgrade, player: Player): Boolean =
    if (player.stars >= upgrade.cost)
      player.stars -= upgrade.cost
      val key = s"${upgrade.towerType}_${upgrade.effectType}"
      val currentUpgrades = purchasedUpgrades.getOrElse(key, List())
      purchasedUpgrades(key) = currentUpgrades :+ upgrade
      true
    else
      false

  def getCumulativeMultiplier(towerType: String, effectType: String): Double =
    val key = s"${towerType}_${effectType}"
    purchasedUpgrades.getOrElse(key, List()).foldLeft(1.0)((total, upgrade) =>
      total * upgrade.effectMultiplier
    )

  def saveUpgrades(): Unit =
    try
      val oos = ObjectOutputStream(FileOutputStream(File("src/main/resources/save.dat")))
      oos.writeObject(purchasedUpgrades)
      oos.close()
    catch
      case e: Exception => e.printStackTrace()

  // Load upgrades from a file
  def loadUpgrades(): Unit =
    try
      val ois = ObjectInputStream(FileInputStream(File("src/main/resources/save.dat")))
      purchasedUpgrades.clear()
      purchasedUpgrades ++= ois.readObject().asInstanceOf[mutable.Map[String, List[PermanentUpgrade]]]
      ois.close()
    catch
      case e: Exception => e.printStackTrace()
