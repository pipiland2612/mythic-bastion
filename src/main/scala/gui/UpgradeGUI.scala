package gui

import utils.Tools

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage
import scala.collection.immutable.ListMap

case class UpgradeComponent(name: String, var image: BufferedImage, description: String, cost: Int):
  var oriImage = image
  var condition = false
  var hasBought = false

  def reloadImage(): Unit =
    if condition then this.image = Tools.applyGrayscale(image)
    else this.image = oriImage

object UpgradeGUI:

  // name, path, desc, cost
  private val list: ListMap[String, (String, String, Int)] = ListMap(
    // Arrow Upgrades
    "SteadyHand"      -> ("arrow/level1", "Increase marksman attack damage by 2%", 2),
    "LumberMill"      -> ("arrow/level2", "Increase marksman attack damage by 3%", 3),
    "PiercingShot"    -> ("arrow/level3", "Increase marksman attack damage by 4%", 2),
    "SharpFocus"      -> ("arrow/level4", "Increase marksman accuracy and range by 5%", 3),
    "EagleEye"        -> ("arrow/level5", "Increase marksman attack range by 10%", 4),

    // Barrack Upgrades
    "BarrackBoost"    -> ("barrack/level1", "Increase infantry health by 5%", 2),
    "FortifiedWalls"  -> ("barrack/level2", "Increase infantry defense by 3%", 3),
    "BattleHardened"  -> ("barrack/level3", "Increase infantry stamina and resistance by 5%", 3),
    "ShieldWall"      -> ("barrack/level4", "Increase infantry block chance by 6%", 4),
    "Unbreakable"     -> ("barrack/level5", "Increase infantry resilience and reduce damage taken by 7%", 4),

    // Explosive Upgrades
    "ExplosiveTrap"   -> ("explo/level1", "Increase explosive damage by 3%", 2),
    "DemolitionExpert" -> ("explo/level2", "Increase explosive damage by 4%", 3),
    "Firestorm"       -> ("explo/level3", "Increase explosive damage by 5%", 3),
    "ChainReaction"   -> ("explo/level4", "Increase explosive range by 5%", 4),
    "MegaBlast"       -> ("explo/level5", "Increase explosive impact and damage by 10%", 4),

    // Mage Upgrades
    "ArcaneMastery"   -> ("mage/level1", "Increase mage spell damage by 2%", 2),
    "ManaInfusion"    -> ("mage/level2", "Increase mage spell damage by 3%", 3),
    "ElementalSurge"  -> ("mage/level3", "Increase mage spell damage by 6%", 3),
    "MysticBarrier"   -> ("mage/level4", "Increase mage spell range by 3%", 4),
    "EldritchWisdom"  -> ("mage/level5", "Increase mage spell range by 10%", 4),

    // Rock Upgrades
    "RockSolid"       -> ("rock/level1", "Increase fortress durability by 5%", 2),
    "StoneFist"       -> ("rock/level2", "Increase melee unit resistance by 4%", 3),
    "Earthquake"      -> ("rock/level3", "Chance to knock back enemies on impact", 3),
    "TitanStrength"   -> ("rock/level4", "Increase melee unit attack power by 6%", 4),
    "UnyieldingForce" -> ("rock/level5", "Increase overall defense and structure stability by 10%", 4),

    // Alliance Upgrades
    "UnitedFront"     -> ("alliance/level1", "Increase allied unit cooperation and attack coordination", 2),
    "WarCouncil"      -> ("alliance/level2", "Increase allied strategy efficiency and response time", 3),
    "SharedWisdom"    -> ("alliance/level3", "Increase knowledge transfer among allies, boosting skill effectiveness", 3),
    "ReinforcementCall" -> ("alliance/level4", "Reduce reinforcement arrival time by 15%", 4),
    "UnbreakableBond" -> ("alliance/level5", "Significantly increase overall allied strength and support effectiveness", 4)
  )

  private val startX = 80
  private val startY = 370
  private val padding = 20
  private val d = (86 * 0.75).toInt

  var upgradeList: Map[(Int, Int), UpgradeComponent] = Map()
  private val allUpgrades: List[UpgradeComponent] = list.map { case (name, (path, desc, cost)) =>
    UpgradeComponent(name, Tools.scaleImage(Tools.loadImage(s"upgrade/$path.png"), 0.75, 0.75), desc, cost)
  }.toList

  private def populateList(startX: Int, startY: Int): Unit =

    var (x, y) = (startX, startY)
    for i <- allUpgrades.indices do
      if i % 5 == 0 then
        y = startY
        x = x + d + padding

      upgradeList += (x, y) -> allUpgrades(i)
      y = y - d - padding

  def setUp(): Unit =
    populateList(startX, startY)
    reload()

  def reload(): Unit =
    upgradeList.foreach{case ((_,_), com) => com.reloadImage()}

  def draw(g2d: Graphics2D): Unit =
    upgradeList.foreach { case ((x, y), component) =>
      g2d.drawImage(component.image, x, y, None.orNull)
      if !component.hasBought then
        var starToDraw = Image.grey_starCost
        var color = Color.GRAY
        if !component.condition then
          starToDraw = Image.starCost
          color = Color.YELLOW
        g2d.setColor(Color.YELLOW)
        g2d.drawImage(starToDraw, x + component.image.getWidth/2, y + component.image.getHeight * 3/4, None.orNull)
        g2d.drawString(component.cost.toString, x + component.image.getWidth * 3/4+ 5, y + component.image.getHeight - 4)
    }
    val x = startX + d + padding
    val y = startY + d + padding
    g2d.drawImage(Image.arrow, x, y, None.orNull)
    g2d.drawImage(Image.barrack, x + padding + d, y , None.orNull)
    g2d.drawImage(Image.explo, x + (padding + d)*2, y, None.orNull)
    g2d.drawImage(Image.mage, x + (padding + d)*3, y, None.orNull)
    g2d.drawImage(Image.rock, x + (padding + d)*4, y, None.orNull)
    g2d.drawImage(Image.alliance, x + (padding + d)*5, y, None.orNull)