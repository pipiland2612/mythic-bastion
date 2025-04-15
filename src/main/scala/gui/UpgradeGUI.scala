package gui

import game.GamePanel
import system.upgrade.{AbilityUpgrade, MultiplierUpgrade, PermanentUpgrade, UpgradeTowerType, UpgradeType}
import utils.Tools

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage
import scala.collection.immutable.ListMap

case class UpgradeComponent(
  gp: GamePanel,
  name: String,
  private val image: BufferedImage,
  description: String,
  cost: Int,
  upgrade: PermanentUpgrade,
):
  private val originalImage: BufferedImage = image
  private var currentImage: BufferedImage = image

  def hasBought: Boolean = gp.getSystemHandler.getUpgradeManager.findUpgrade(upgrade.towerType, upgrade.level)
  def canBuy: Boolean =
    val condition = if upgrade.level - 1 == 0 then true else gp.getSystemHandler.getUpgradeManager.findUpgrade(upgrade.towerType, upgrade.level-1)
    (gp.getPlayer.stars >= this.cost || hasBought) && condition

  def reloadImage(): Unit =
    currentImage = if (!canBuy) Tools.applyGrayscale(originalImage) else originalImage

  def getCurrentImage: BufferedImage = currentImage

object UpgradeCategory:
  sealed trait Category:
    def icon: BufferedImage
    def upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)]

  case object Arrow extends Category:
    val icon: BufferedImage = Image.arrow
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "Steady Hand"   -> ("arrow/level1", "Increase marksman attack\ndamage by 10%", 2,
        MultiplierUpgrade("Steady Hand", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.10, 1, 2)),
      "Lumber Mill"   -> ("arrow/level2", "Increase marksman attack\ndamage by 12%", 3,
        MultiplierUpgrade("Lumber Mill", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.12, 2, 3)),
      "Piercing Shot" -> ("arrow/level3", "Increase marksman attack\ndamage by 15%", 2,
        MultiplierUpgrade("Piercing Shot", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.15, 3, 2)),
      "Sharp Focus"   -> ("arrow/level4", "Increase marksman attack\nrange by 10%", 3,
        MultiplierUpgrade("Sharp Focus", UpgradeTowerType.ARROW, UpgradeType.RANGE, 1.10, 4, 3)),
      "Eagle Eye"     -> ("arrow/level5", "Increase marksman attack\nrange by 15%", 4,
        MultiplierUpgrade("Eagle Eye", UpgradeTowerType.ARROW, UpgradeType.RANGE, 1.15, 5, 4))
    )

  private case object Barrack extends Category:
    val icon: BufferedImage = Image.barrack
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "Barrack Boost"    -> ("barrack/level1", "Increase infantry health\nby 3%", 2,
        MultiplierUpgrade("Barrack Boost", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.03, 1,2)),
      "Fortified Walls"  -> ("barrack/level2", "Increase infantry health\nby 5%", 3,
        MultiplierUpgrade("Fortified Walls", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.05,2, 3)),
      "Battle Hardened"  -> ("barrack/level3", "Increase infantry health\nby 7%", 3,
        MultiplierUpgrade("Battle Hardened", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.07, 3, 3)),
      "Shield Wall"      -> ("barrack/level4", "Increase infantry health\nby 8%", 4,
        MultiplierUpgrade("Shield Wall", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.08, 4,4)),
      "Unbreakable"     -> ("barrack/level5", "Increase infantry health\nby 10%", 4,
        MultiplierUpgrade("Unbreakable", UpgradeTowerType.BARRACK, UpgradeType.HEALTH, 1.1, 5,4))
    )

  case object Explo extends Category:
    val icon: BufferedImage = Image.explo
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "Explosive Trap"    -> ("explo/level1", "Increase explosive damage\nby 3%", 2,
        MultiplierUpgrade("Explosive Trap", UpgradeTowerType.EXPLO, UpgradeType.DAMAGE, 1.03, 1,2)),
      "Demolition Expert" -> ("explo/level2", "Increase explosive damage\nby 4%", 3,
        MultiplierUpgrade("Demolition Expert", UpgradeTowerType.EXPLO, UpgradeType.DAMAGE, 1.04,2, 3)),
      "Firestorm"        -> ("explo/level3", "Increase explosive damage\nby 5%", 3,
        MultiplierUpgrade("Firestorm", UpgradeTowerType.EXPLO, UpgradeType.DAMAGE, 1.05,3, 3)),
      "Chain Reaction"    -> ("explo/level4", "Increase explosive range\nby 5%", 4,
        MultiplierUpgrade("Chain Reaction", UpgradeTowerType.EXPLO, UpgradeType.RANGE, 1.05, 4,4)),
      "Mega Blast"        -> ("explo/level5", "Increase explosive impact\nand damage by\n10%", 4,
        MultiplierUpgrade("Mega Blast", UpgradeTowerType.EXPLO, UpgradeType.DAMAGE, 1.10,5, 4))
    )

  private case object Mage extends Category:
    val icon: BufferedImage = Image.mage
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "Arcane Mastery"  -> ("mage/level1", "Increase mage spell\ndamage by 2%", 2,
        MultiplierUpgrade("Arcane Mastery", UpgradeTowerType.MAGE, UpgradeType.DAMAGE, 1.02,1, 2)),
      "Mana Infusion"   -> ("mage/level2", "Increase mage spell\ndamage by 3%", 3,
        MultiplierUpgrade("Mana Infusion", UpgradeTowerType.MAGE, UpgradeType.DAMAGE, 1.03,2, 3)),
      "Elemental Surge" -> ("mage/level3", "Increase mage spell\ndamage by 6%", 3,
        MultiplierUpgrade("Elemental Surge", UpgradeTowerType.MAGE, UpgradeType.DAMAGE, 1.06,3, 3)),
      "Mystic Barrier"  -> ("mage/level4", "Increase mage spell\nrange by 3%", 4,
        MultiplierUpgrade("Mystic Barrier", UpgradeTowerType.MAGE, UpgradeType.RANGE, 1.03,4, 4)),
      "Eldritch Wisdom" -> ("mage/level5", "Increase mage spell\nrange by 10%", 4,
        MultiplierUpgrade("Eldritch Wisdom", UpgradeTowerType.MAGE, UpgradeType.RANGE, 1.10,5, 4))
    )

  private case object Rock extends Category:
    val icon: BufferedImage = Image.rock
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "Rock Solid"      -> ("rock/level1", "Increase fortress durability\nby 5%", 2,
        MultiplierUpgrade("Rock Solid", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1.05,1, 2)),
      "Stone Fist"      -> ("rock/level2", "Increase melee unit\nresistance by 4%", 3,
        MultiplierUpgrade("Stone Fist", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1.04,2, 3)),
      "Earthquake"     -> ("rock/level3", "Chance to knock\nback enemies on\nimpact", 3,
        AbilityUpgrade("Earthquake", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1, 3)),
      "Titan Strength"  -> ("rock/level4", "Increase melee unit\nattack power by\n6%", 4,
        MultiplierUpgrade("Titan Strength", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1.06, 4,4)),
      "Unyielding Force" -> ("rock/level5", "Increase overall defense\nand structure stability\nby 10%", 4,
        MultiplierUpgrade("Unyielding Force", UpgradeTowerType.ROCK, UpgradeType.NOT_IMPLEMENTED, 1.10, 5, 4))
    )

  case object Alliance extends Category:
    val icon: BufferedImage = Image.alliance
    val upgrades: ListMap[String, (String, String, Int, PermanentUpgrade)] = ListMap(
      "United Front"      -> ("alliance/level1", "Increase allied unit\ncooperation and attack\ncoordination", 2,
        AbilityUpgrade("United Front", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 1, 1)),
      "War Council"       -> ("alliance/level2", "Increase allied strategy\nefficiency and response\ntime", 3,
        AbilityUpgrade("War Council", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 1, 2)),
      "Shared Wisdom"     -> ("alliance/level3", "Increase knowledge transfer\namong allies, boosting\nskill effectiveness", 3,
        AbilityUpgrade("Shared Wisdom", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 1, 3)),
      "Reinforcement Call" -> ("alliance/level4", "Reduce reinforcement arrival\ntime by 15%", 4,
        MultiplierUpgrade("Reinforcement Call", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 0.85, 4, 4)),
      "Unbreakable Bond"  -> ("alliance/level5", "Significantly increase overall\nallied strength and\nsupport effectiveness", 4,
        MultiplierUpgrade("Unbreakable Bond", UpgradeTowerType.ALLIANCE, UpgradeType.NOT_IMPLEMENTED, 1.10,5, 4))
    )

  val categories: List[Category] = List(Arrow, Barrack, Explo, Mage, Rock, Alliance)

class UpgradeGUI(gp: GamePanel):
  private val StartX: Int = 140
  private val StartY: Int = 370
  private val Padding: Int = 20
  private val IconSize: Int = (86 * 0.75).toInt

  private def createUpgradeComponents(category: UpgradeCategory.Category): List[UpgradeComponent] =
    category.upgrades.map { case (name, (path, desc, cost, upgrade)) =>
      val scaledImage = Tools.scaleImage(Tools.loadImage(s"upgrade/$path.png"), 0.75, 0.75)
      UpgradeComponent(gp, name, scaledImage, desc, cost, upgrade)
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

  def getNextComponent(comp: UpgradeComponent): Option[UpgradeComponent] =
    upgradeMap.values.find(curr =>
      curr.upgrade.towerType == comp.upgrade.towerType && curr.upgrade.level == comp.upgrade.level + 1
    )

  def reload(): Unit = upgradeMap.values.foreach(_.reloadImage())
  def getUpgradeList: Map[(Int, Int), UpgradeComponent] = upgradeMap

  def draw(g2d: Graphics2D): Unit =
    g2d.setColor(Color.GREEN)
    val coords = upgradeMap.collectFirst{case (k, v) if v == currentComponent => k}.getOrElse((StartX, StartY))
    g2d.drawRect(coords._1 - 1, coords._2 - 1, IconSize + 2, IconSize + 2)

    upgradeMap.foreach{case ((x, y), component) =>
      g2d.drawImage(component.getCurrentImage, x, y, None.orNull)

      if !component.hasBought then
        val (starImage, starColor) =
          if !component.canBuy then (Image.grey_starCost, Color.GRAY)
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
