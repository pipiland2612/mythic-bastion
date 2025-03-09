package gui

import utils.Tools

import java.awt.image.BufferedImage
import scala.collection.immutable.ListMap

case class UpgradeComponent(name: String, var image: BufferedImage, description: String):
  var oriImage = image
  var condition = true

  def reloadImage(): Unit =
    if condition then
      this.image = Tools.applyGrayscale(image)

object UpgradeGUI:

  // name, path, desc
  private val list: ListMap[String, (String, String)] = ListMap(
    // Arrow Upgrades
    "SteadyHand"      -> ("arrow/level1", "Increase marksman attack damage by 2%"),
    "LumberMill"      -> ("arrow/level2", "Increase marksman attack damage by 3%"),
    "PiercingShot"    -> ("arrow/level3", "Increase marksman attack damage by 4%"),
    "SharpFocus"      -> ("arrow/level4", "Increase marksman accuracy and range by 5%"),
    "EagleEye"        -> ("arrow/level5", "Increase marksman attack range by 10%"),

    // Barrack Upgrades
    "BarrackBoost"    -> ("barrack/level1", "Increase infantry health by 5%"),
    "FortifiedWalls"  -> ("barrack/level2", "Increase infantry defense by 3%"),
    "BattleHardened"  -> ("barrack/level3", "Increase infantry stamina and resistance by 5%"),
    "ShieldWall"      -> ("barrack/level4", "Increase infantry block chance by 6%"),
    "Unbreakable"     -> ("barrack/level5", "Increase infantry resilience and reduce damage taken by 7%"),

    // Explosive Upgrades
    "ExplosiveTrap"   -> ("explo/level1", "Increase explosive damage by 3%"),
    "DemolitionExpert" -> ("explo/level2", "Increase explosive damage by 4%"),
    "Firestorm"       -> ("explo/level3", "Increase explosive damage by 5%"),
    "ChainReaction"   -> ("explo/level4", "Increase explosive range by 5%"),
    "MegaBlast"       -> ("explo/level5", "Increase explosive impact and damage by 10%"),

    // Mage Upgrades
    "ArcaneMastery"   -> ("mage/level1", "Increase mage spell damage by 2%"),
    "ManaInfusion"    -> ("mage/level2", "Increase mage spell damage by 3%"),
    "ElementalSurge"  -> ("mage/level3", "Increase mage spell damage by 6%"),
    "MysticBarrier"   -> ("mage/level4", "Increase mage spell range by 3%"),
    "EldritchWisdom"  -> ("mage/level5", "Increase mage spell range by 10%"),

    // Rock Upgrades
    "RockSolid"       -> ("rock/level1", "Increase fortress durability by 5%"),
    "StoneFist"       -> ("rock/level2", "Increase melee unit resistance by 4%"),
    "Earthquake"      -> ("rock/level3", "Chance to knock back enemies on impact"),
    "TitanStrength"   -> ("rock/level4", "Increase melee unit attack power by 6%"),
    "UnyieldingForce" -> ("rock/level5", "Increase overall defense and structure stability by 10%"),

    // Alliance Upgrades
    "UnitedFront"     -> ("alliance/level1", "Increase allied unit cooperation and attack coordination"),
    "WarCouncil"      -> ("alliance/level2", "Increase allied strategy efficiency and response time"),
    "SharedWisdom"    -> ("alliance/level3", "Increase knowledge transfer among allies, boosting skill effectiveness"),
    "ReinforcementCall" -> ("alliance/level4", "Reduce reinforcement arrival time by 15%"),
    "UnbreakableBond" -> ("alliance/level5", "Significantly increase overall allied strength and support effectiveness")
  )

  var upgradeList: Map[(Int, Int), UpgradeComponent] = Map()
  private val allUpgrades: List[UpgradeComponent] = list.map { case (name, (path, desc)) =>
    UpgradeComponent(name, Tools.scaleImage(Tools.loadImage(s"upgrade/$path.png"), 0.75, 0.75), desc)
  }.toList

  private def populateList(startX: Int): Unit =
    val startY = 370
    val padding = 20
    val d = (86 * 0.75).toInt

    var (x, y) = (startX, startY)
    for i <- allUpgrades.indices do
      if i % 5 == 0 then
        y = startY
        x = x + d + padding

      upgradeList += (x, y) -> allUpgrades(i)
      y = y - d - padding

  def setUp(): Unit =
    populateList(80)
    reload()

  def reload(): Unit =
    upgradeList.foreach{case ((_,_), com) => com.reloadImage()}