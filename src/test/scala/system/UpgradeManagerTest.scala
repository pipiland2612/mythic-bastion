package system

import game.Player
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import system.upgrade.{AbilityUpgrade, MultiplierUpgrade, UpgradeManager, UpgradeTowerType, UpgradeType}

class UpgradeManagerTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach:
  
  var upgradeManager: UpgradeManager = _
  var player: Player = _
  var testMultiplierUpgrade: MultiplierUpgrade = _
  var expensiveUpgrade: AbilityUpgrade = _

  override def beforeEach(): Unit =
    upgradeManager = new UpgradeManager()
    player = new Player()
    player.stars = 100 // Give player some stars to work with
    
    testMultiplierUpgrade = MultiplierUpgrade(
      name = "Arrow Damage Boost",
      towerType = UpgradeTowerType.ARROW,
      effectType = UpgradeType.DAMAGE,
      multiplier = 1.5,
      level = 1,
      cost = 50
    )
    
    expensiveUpgrade = AbilityUpgrade(
      name = "Magic Range Extension",
      towerType = UpgradeTowerType.MAGE,
      ability = UpgradeType.RANGE,
      cost = 200,
      level = 2
    )

  "UpgradeManager" should "initialize with empty purchased upgrades" in {
    upgradeManager.getPurchasedUpgrades shouldBe empty
  }

  it should "allow purchasing valid upgrades" in {
    val result = upgradeManager.purchaseUpgrade(testMultiplierUpgrade, player)
    
    result shouldBe true
    player.stars shouldBe 50 // 100 - 50
    upgradeManager.getPurchasedUpgrades should contain(testMultiplierUpgrade)
  }

  it should "reject purchase when player has insufficient stars" in {
    val result = upgradeManager.purchaseUpgrade(expensiveUpgrade, player)
    
    result shouldBe false
    player.stars shouldBe 100 // Should remain unchanged
    upgradeManager.getPurchasedUpgrades should not contain expensiveUpgrade
  }

  it should "reject duplicate purchases" in {
    upgradeManager.purchaseUpgrade(testMultiplierUpgrade, player)
    val secondPurchase = upgradeManager.purchaseUpgrade(testMultiplierUpgrade, player)
    
    secondPurchase shouldBe false
    player.stars shouldBe 50 // Should not deduct twice
    upgradeManager.getPurchasedUpgrades.count(_ == testMultiplierUpgrade) shouldBe 1
  }

  it should "find purchased upgrades correctly" in {
    upgradeManager.purchaseUpgrade(testMultiplierUpgrade, player)
    
    upgradeManager.findUpgrade(UpgradeTowerType.ARROW, 1) shouldBe true
    upgradeManager.findUpgrade(UpgradeTowerType.MAGE, 1) shouldBe false
    upgradeManager.findUpgrade(UpgradeTowerType.ARROW, 2) shouldBe false
  }

  it should "calculate cumulative multipliers correctly" in {
    val upgrade1 = MultiplierUpgrade(
      "Arrow Damage 1", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.2, 1, 30
    )
    val upgrade2 = MultiplierUpgrade(
      "Arrow Damage 2", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.3, 2, 50
    )
    
    player.stars = 200
    upgradeManager.purchaseUpgrade(upgrade1, player)
    upgradeManager.purchaseUpgrade(upgrade2, player)
    
    val multiplier = upgradeManager.getCumulativeMultiplier(UpgradeTowerType.ARROW, UpgradeType.DAMAGE)
    multiplier shouldBe (1.2 * 1.3) +- 0.001
  }

  it should "return 1.0 multiplier for non-purchased upgrades" in {
    val multiplier = upgradeManager.getCumulativeMultiplier(UpgradeTowerType.BARRACK, UpgradeType.RANGE)
    multiplier shouldBe 1.0
  }

  it should "handle multiple upgrade types for same tower" in {
    val damageUpgrade = MultiplierUpgrade(
      "Arrow Damage", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.5, 1, 40
    )
    val rangeUpgrade = MultiplierUpgrade(
      "Arrow Range", UpgradeTowerType.ARROW, UpgradeType.RANGE, 1.3, 1, 30
    )
    
    player.stars = 200
    upgradeManager.purchaseUpgrade(damageUpgrade, player)
    upgradeManager.purchaseUpgrade(rangeUpgrade, player)
    
    upgradeManager.getCumulativeMultiplier(UpgradeTowerType.ARROW, UpgradeType.DAMAGE) shouldBe 1.5
    upgradeManager.getCumulativeMultiplier(UpgradeTowerType.ARROW, UpgradeType.RANGE) shouldBe 1.3
  }

  it should "handle edge case with zero cost upgrade" in {
    val freeUpgrade = MultiplierUpgrade(
      "Free Boost", UpgradeTowerType.EXPLO, UpgradeType.DAMAGE, 1.1, 1, 0
    )
    
    val initialStars = player.stars
    val result = upgradeManager.purchaseUpgrade(freeUpgrade, player)
    
    result shouldBe true
    player.stars shouldBe initialStars
    upgradeManager.getPurchasedUpgrades should contain(freeUpgrade)
  }

  it should "handle ability upgrades" in {
    player.stars = 300
    val result = upgradeManager.purchaseUpgrade(expensiveUpgrade, player)
    
    result shouldBe true
    player.stars shouldBe 100 // 300 - 200
    upgradeManager.getPurchasedUpgrades should contain(expensiveUpgrade)
  }

  it should "distinguish between different upgrade types" in {
    val multiplierUpgrade = MultiplierUpgrade(
      "Test Multiplier", UpgradeTowerType.ARROW, UpgradeType.DAMAGE, 1.2, 1, 30
    )
    val abilityUpgrade = AbilityUpgrade(
      "Test Ability", UpgradeTowerType.ARROW, UpgradeType.RANGE, 30, 1
    )
    
    player.stars = 100
    upgradeManager.purchaseUpgrade(multiplierUpgrade, player)
    upgradeManager.purchaseUpgrade(abilityUpgrade, player)
    
    upgradeManager.getPurchasedUpgrades should have size 2
    upgradeManager.getPurchasedUpgrades should contain(multiplierUpgrade)
    upgradeManager.getPurchasedUpgrades should contain(abilityUpgrade)
  }