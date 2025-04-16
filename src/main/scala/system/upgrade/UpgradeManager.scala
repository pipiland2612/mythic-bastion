package system.upgrade

import game.Player
import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.mutable

/** Manages permanent upgrades for the game, including purchasing, tracking, and saving/loading upgrades.
 *
 * This class handles the logic for purchasing upgrades, calculating cumulative effects, and persisting
 * upgrade data to a file.
 */
class UpgradeManager:
  private val purchasedUpgrades: mutable.Set[PermanentUpgrade] = mutable.Set()

  /** Retrieves the set of purchased upgrades.
   *
   * @return A mutable Set containing all purchased PermanentUpgrade instances.
   */
  def getPurchasedUpgrades: mutable.Set[PermanentUpgrade] = purchasedUpgrades

  /** Checks if a specific upgrade for a tower type and level has been purchased.
   *
   * @param towerType The type of tower the upgrade applies to.
   * @param level The level of the upgrade.
   * @return True if the upgrade is purchased, false otherwise.
   */
  def findUpgrade(towerType: UpgradeTowerType, level: Int): Boolean =
    purchasedUpgrades.find(upgrade => upgrade.towerType == towerType && upgrade.level == level) match
      case Some(upgrade) => true
      case _ => false

  /** Attempts to purchase an upgrade for the player.
   *
   * The upgrade is purchased if the player has sufficient stars and the upgrade has not already been
   * purchased. The player's stars are deducted, and the upgrade is added to the purchased set.
   *
   * @param upgrade The PermanentUpgrade to purchase.
   * @param player The Player attempting to purchase the upgrade.
   * @return True if the purchase is successful, false otherwise.
   */
  def purchaseUpgrade(upgrade: PermanentUpgrade, player: Player): Boolean =
    if player.stars >= upgrade.cost && !purchasedUpgrades.contains(upgrade) then
      player.stars -= upgrade.cost
      purchasedUpgrades += upgrade
      true
    else
      false

  /** Calculates the cumulative multiplier for a specific tower type and effect type.
   *
   * This method collects all purchased MultiplierUpgrades for the given tower and effect type and
   * computes their product to determine the total multiplier.
   *
   * @param towerType The type of tower to calculate the multiplier for.
   * @param effectType The type of effect (e.g., damage, speed) to calculate the multiplier for.
   * @return The cumulative multiplier as a Double.
   */
  def getCumulativeMultiplier(towerType: UpgradeTowerType, effectType: UpgradeType): Double =
    purchasedUpgrades.collect {
      case u: MultiplierUpgrade if u.towerType == towerType && u.effectType == effectType => u.multiplier
    }.product

  /** Retrieves the list of abilities purchased for a specific tower type.
   *
   * @param towerType The type of tower to retrieve abilities for.
   * @return A List of UpgradeType representing the purchased abilities.
   */
  def getAbilities(towerType: UpgradeTowerType): List[UpgradeType] =
    purchasedUpgrades.collect {
      case u: AbilityUpgrade if u.towerType == towerType => u.ability
    }.toList

  /** Saves the purchased upgrades to a file.
   *
   * Serializes the purchasedUpgrades set to a file located at "src/main/resources/upgrade_save.dat".
   * Exceptions during the save process are caught and printed to the console.
   */
  def saveUpgrades(): Unit =
    try
      val oos = new ObjectOutputStream(new FileOutputStream(new File("src/main/resources/upgrade_save.dat")))
      oos.writeObject(purchasedUpgrades)
      oos.close()
    catch
      case e: Exception => e.printStackTrace()

  /** Loads purchased upgrades from a file.
   *
   * Deserializes the purchasedUpgrades set from a file located at "src/main/resources/upgrade_save.dat".
   * The current purchasedUpgrades set is cleared and replaced with the loaded data. Exceptions during
   * the load process are silently ignored.
   */
  def loadUpgrades(): Unit =
    try
      val ois = new ObjectInputStream(new FileInputStream(new File("src/main/resources/upgrade_save.dat")))
      val loadedUpgrades = ois.readObject().asInstanceOf[mutable.Set[PermanentUpgrade]]
      purchasedUpgrades.clear()
      purchasedUpgrades ++= loadedUpgrades
      ois.close()
    catch
      case e: Exception =>