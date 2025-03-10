package system.upgrade

import system.Player

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.mutable


class UpgradeManager:
  private val purchasedUpgrades: mutable.Set[PermanentUpgrade] = mutable.Set()

  def purchaseUpgrade(upgrade: PermanentUpgrade, player: Player): Boolean =
    if (player.stars >= upgrade.cost && !purchasedUpgrades.contains(upgrade)) then
      player.stars -= upgrade.cost
      purchasedUpgrades += upgrade
      true
    else
      false

  def getCumulativeMultiplier(towerType: UpgradeTowerType, effectType: UpgradeType): Double =
    purchasedUpgrades.collect {
      case u: MultiplierUpgrade if u.towerType == towerType && u.effectType == effectType => u.multiplier
    }.product

  def getAbilities(towerType: UpgradeTowerType): List[UpgradeType] =
    purchasedUpgrades.collect {
      case u: AbilityUpgrade if u.towerType == towerType => u.ability
    }.toList

  def saveUpgrades(): Unit =
    try
      val oos = new ObjectOutputStream(new FileOutputStream(new File("src/main/resources/upgrade_save.dat")))
      oos.writeObject(purchasedUpgrades)
      oos.close()
    catch
      case e: Exception => e.printStackTrace()

  def loadUpgrades(): Unit =
    try
      val ois = new ObjectInputStream(new FileInputStream(new File("src/main/resources/upgrade_save.dat")))
      val loadedUpgrades = ois.readObject().asInstanceOf[mutable.Set[PermanentUpgrade]]
      purchasedUpgrades.clear()
      purchasedUpgrades ++= loadedUpgrades
      ois.close()
    catch
      case e: Exception => e.printStackTrace()