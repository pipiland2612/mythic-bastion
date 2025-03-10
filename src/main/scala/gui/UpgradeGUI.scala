package gui

import system.upgrade.{AbilityUpgrade, MultiplierUpgrade, PermanentUpgrade, UpgradeTowerType, UpgradeType}
import utils.Tools

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage
import scala.collection.immutable.ListMap

case class UpgradeComponent(
  name: String,
  private val image: BufferedImage,
  description: String,
  cost: Int,
  val upgrade: PermanentUpgrade,
  var condition: Boolean = false,
  var hasBought: Boolean = false
):
  private val originalImage: BufferedImage = image
  private var currentImage: BufferedImage = image

  def reloadImage(): Unit =
    currentImage = if (condition) Tools.applyGrayscale(originalImage) else originalImage

  def getCurrentImage: BufferedImage = currentImage

object UpgradeCategory:
  sealed trait Category:
    def icon: BufferedImage
    def upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)]

  case object Arrow extends Category:
    val icon: BufferedImage = Image.arrow
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "SteadyHand"   -> ("arrow/level1", "Increase marksman attack damage by 10%", 2,
        MultiplierUpgrade("SteadyHand", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.10, 2)),
      "LumberMill"   -> ("arrow/level2", "Increase marksman attack damage by 12%", 3,
        MultiplierUpgrade("LumberMill", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.12, 3)),
      "PiercingShot" -> ("arrow/level3", "Increase marksman attack damage by 15%", 2,
        MultiplierUpgrade("PiercingShot", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.15, 2)),
      "SharpFocus"   -> ("arrow/level4", "Increase marksman attack range by 10%", 3,
        MultiplierUpgrade("SharpFocus", UpgradeTowerType.ARROW, UpgradeType.RANGE, 1.10, 3)),
      "EagleEye"     -> ("arrow/level5", "Increase marksman attack range by 15%", 4,
        MultiplierUpgrade("EagleEye", UpgradeTowerType.ARROW, UpgradeType.RANGE, 1.15, 4))
    )

  case object Barrack extends Category:
    val icon: BufferedImage = Image.barrack
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "BarrackBoost"    -> ("barrack/level1", "Increase infantry health by 3%", 2,
        MultiplierUpgrade("BarrackBoost", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.03, 2)),
      "FortifiedWalls"  -> ("barrack/level2", "Increase infantry health by 5%", 3,
        MultiplierUpgrade("FortifiedWalls", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.05, 3)),
      "BattleHardened"  -> ("barrack/level3", "Increase infantry health by 7%", 3,
        MultiplierUpgrade("BattleHardened", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.07, 3)),
      "ShieldWall"      -> ("barrack/level4", "Increase infantry health by 8%", 4,
        MultiplierUpgrade("ShieldWall", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.08, 4)),
      "Unbreakable"     -> ("barrack/level5", "Increase infantry health by 10%", 4,
        MultiplierUpgrade("Unbreakable", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.1, 4))
    )

  case object Explo extends Category:
    val icon: BufferedImage = Image.explo
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "ExplosiveTrap"    -> ("explo/level1", "Increase explosive damage by 3%", 2,
        MultiplierUpgrade("ExplosiveTrap", UpgradeTowerType.EXPLO, UpgradeType.DAMAGE, 1.03, 2)),
      "DemolitionExpert" -> ("explo/level2", "Increase explosive damage by 4%", 3,
        MultiplierUpgrade("DemolitionExpert", UpgradeTowerType.EXPLO, UpgradeType.DAMAGE, 1.04, 3)),
      "Firestorm"        -> ("explo/level3", "Increase explosive damage by 5%", 3,
        MultiplierUpgrade("Firestorm", UpgradeTowerType.EXPLO, UpgradeType.DAMAGE, 1.05, 3)),
      "ChainReaction"    -> ("explo/level4", "Increase explosive range by 5%", 4,
        MultiplierUpgrade("ChainReaction", UpgradeTowerType.EXPLO, UpgradeType.RANGE, 1.05, 4)),
      "MegaBlast"        -> ("explo/level5", "Increase explosive impact and damage by 10%", 4,
        MultiplierUpgrade("MegaBlast", UpgradeTowerType.EXPLO, UpgradeType.DAMAGE, 1.10, 4))
    )

  case object Mage extends Category:
    val icon: BufferedImage = Image.mage
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "ArcaneMastery"  -> ("mage/level1", "Increase mage spell damage by 2%", 2,
        MultiplierUpgrade("ArcaneMastery", UpgradeTowerType.MAGE, UpgradeType.DAMAGE, 1.02, 2)),
      "ManaInfusion"   -> ("mage/level2", "Increase mage spell damage by 3%", 3,
        MultiplierUpgrade("ManaInfusion", UpgradeTowerType.MAGE, UpgradeType.DAMAGE, 1.03, 3)),
      "ElementalSurge" -> ("mage/level3", "Increase mage spell damage by 6%", 3,
        MultiplierUpgrade("ElementalSurge", UpgradeTowerType.MAGE, UpgradeType.DAMAGE, 1.06, 3)),
      "MysticBarrier"  -> ("mage/level4", "Increase mage spell range by 3%", 4,
        MultiplierUpgrade("MysticBarrier", UpgradeTowerType.MAGE, UpgradeType.RANGE, 1.03, 4)),
      "EldritchWisdom" -> ("mage/level5", "Increase mage spell range by 10%", 4,
        MultiplierUpgrade("EldritchWisdom", UpgradeTowerType.MAGE, UpgradeType.RANGE, 1.10, 4))
    )

  case object Rock extends Category:
    val icon: BufferedImage = Image.rock
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "RockSolid"      -> ("rock/level1", "Increase fortress durability by 5%", 2,
        MultiplierUpgrade("RockSolid", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1.05, 2)),
      "StoneFist"      -> ("rock/level2", "Increase melee unit resistance by 4%", 3,
        MultiplierUpgrade("StoneFist", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1.04, 3)),
      "Earthquake"     -> ("rock/level3", "Chance to knock back enemies on impact", 3,
        AbilityUpgrade("Earthquake", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1, 3)),
      "TitanStrength"  -> ("rock/level4", "Increase melee unit attack power by 6%", 4,
        MultiplierUpgrade("TitanStrength", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1.06, 4)),
      "UnyieldingForce" -> ("rock/level5", "Increase overall defense and structure stability by 10%", 4,
        MultiplierUpgrade("UnyieldingForce", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1.10, 4))
    )

  case object Alliance extends Category:
    val icon: BufferedImage = Image.alliance
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "UnitedFront"      -> ("alliance/level1", "Increase allied unit cooperation and attack coordination", 2,
        AbilityUpgrade("UnitedFront", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 1, 2)),
      "WarCouncil"       -> ("alliance/level2", "Increase allied strategy efficiency and response time", 3,
        AbilityUpgrade("WarCouncil", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 1, 3)),
      "SharedWisdom"     -> ("alliance/level3", "Increase knowledge transfer among allies, boosting skill effectiveness", 3,
        AbilityUpgrade("SharedWisdom", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 1, 3)),
      "ReinforcementCall" -> ("alliance/level4", "Reduce reinforcement arrival time by 15%", 4,
        MultiplierUpgrade("ReinforcementCall", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 0.85, 4)),
      "UnbreakableBond"  -> ("alliance/level5", "Significantly increase overall allied strength and support effectiveness", 4,
        MultiplierUpgrade("UnbreakableBond", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 1.10, 4))
    )

  val categories: List[Category] = List(Arrow, Barrack, Explo, Mage, Rock, Alliance)

object UpgradeGUI:
  private val StartX: Int = 170
  private val StartY: Int = 370
  private val Padding: Int = 20
  private val IconSize: Int = (86 * 0.75).toInt

  private def createUpgradeComponents(category: UpgradeCategory.Category): List[UpgradeComponent] =
    category.upgrades.map { case (name, (path, desc, cost, upgrade)) =>
      val scaledImage = Tools.scaleImage(Tools.loadImage(s"upgrade/$path.png"), 0.75, 0.75)
      UpgradeComponent(name, scaledImage, desc, cost, upgrade)
    }.toList

  private def layoutUpgrades(
    components: List[UpgradeComponent],
    startX: Int,
    startY: Int
  ): Map[(Int, Int), UpgradeComponent] =
    components.zipWithIndex.map((component, index) =>
      val column = index / 5
      val row = index % 5
      val x = startX + column * (IconSize + Padding)
      val y = startY - row * (IconSize + Padding)
      (x, y) -> component
    ).toMap

  // Main state
  private val upgradeMap: Map[(Int, Int), UpgradeComponent] =
    val allComponents = UpgradeCategory.categories.flatMap(createUpgradeComponents)
    layoutUpgrades(allComponents, StartX, StartY)

  private var currentComponent: UpgradeComponent = upgradeMap.head._2
  def setCurrentFrame(comp: UpgradeComponent): Unit = currentComponent = comp
  def getCurrentFrame: UpgradeComponent = currentComponent

  def reload(): Unit = upgradeMap.values.foreach(_.reloadImage())
  def getUpgradeList: Map[(Int, Int), UpgradeComponent] = upgradeMap

  def draw(g2d: Graphics2D): Unit =
    g2d.setColor(Color.GREEN)
    val coords = upgradeMap.collectFirst{case (k, v) if v == currentComponent => k}.getOrElse((StartX, StartY))
    g2d.drawRect(coords._1 - 1, coords._2 - 1, IconSize + 2, IconSize + 2)

    upgradeMap.foreach{case ((x, y), component) =>
      g2d.drawImage(component.getCurrentImage, x, y, None.orNull)

      if (!component.hasBought) then
        val (starImage, starColor) =
          if (component.condition) then (Image.grey_starCost, Color.GRAY)
          else (Image.starCost, Color.YELLOW)

        val starX = x + component.getCurrentImage.getWidth / 2
        val starY = y + component.getCurrentImage.getHeight * 3 / 4

        g2d.setColor(starColor)
        g2d.drawImage(starImage, starX, starY, None.orNull)
        g2d.drawString(component.cost.toString,
          x + component.getCurrentImage.getWidth * 3 / 4 + 5,
          y + component.getCurrentImage.getHeight - 4
        )
      val categoryY = StartY + IconSize + Padding
      UpgradeCategory.categories.zipWithIndex.foreach((category, index) =>
        val x = StartX + (Padding + IconSize) * index
        g2d.drawImage(category.icon, x, categoryY, None.orNull)
      )
    }
