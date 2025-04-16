package entity

import entity.creature.enemy.{Creep, Enemy}
import entity.weapon.{Arrow, Explo, MagicBullet, Weapon}
import game.GamePanel
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.Cache

import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

class WeaponTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach:
  class TestGamePanel extends GamePanel:
    var playedSounds: List[String] = List()
    def playSE(sound: String): Unit =
      playedSounds = playedSounds :+ sound

  val baseCreepParams: Map[String, Any] = Map(
    "name" -> "TestCreep",
    "maxHealth" -> 100.0,
    "playerDamage" -> 10.0,
    "apDmg" -> 5.0,
    "apDefense" -> 2.0,
    "adDmg" -> 8.0,
    "adDefense" -> 3.0,
    "range" -> 50.0,
    "speed" -> 2.0,
    "maxAttackCoolDown" -> 5.0,
    "maxDeadCounter" -> 10.0,
    "coin" -> 5.0,
    "jsonPath" -> "enemies/Boss01.json",
    "imagePath" -> "enemies/Boss01.png",
    "healthOffSet" -> 10
  )

  // Test data
  var testGamePanel: TestGamePanel = _
  var testEnemy: Enemy = _
  var testWeapon: Weapon = _
  val testRect = new Rectangle2D.Double(0, 0, 32, 32)


  override def beforeEach(): Unit =
    testGamePanel = new TestGamePanel()
    testEnemy = Creep(
      name = baseCreepParams("name").asInstanceOf[String],
      maxHealth = baseCreepParams("maxHealth").asInstanceOf[Double],
      playerDamage = baseCreepParams("playerDamage").asInstanceOf[Double],
      apDmg = baseCreepParams("apDmg").asInstanceOf[Double],
      apDefense = baseCreepParams("apDefense").asInstanceOf[Double],
      adDmg = baseCreepParams("adDmg").asInstanceOf[Double],
      adDefense = baseCreepParams("adDefense").asInstanceOf[Double],
      range = baseCreepParams("range").asInstanceOf[Double],
      speed = baseCreepParams("speed").asInstanceOf[Double],
      maxAttackCoolDown = baseCreepParams("maxAttackCoolDown").asInstanceOf[Double],
      maxDeadCounter = baseCreepParams("maxDeadCounter").asInstanceOf[Double],
      coin = baseCreepParams("coin").asInstanceOf[Double],
      jsonPath = baseCreepParams("jsonPath").asInstanceOf[String],
      imagePath = baseCreepParams("imagePath").asInstanceOf[String],
      rect = testRect,
      gp = testGamePanel,
      healthOffSet = (10, 10),
      pos = (100.0, 100.0)
    )

    // Initialize Weapon system
    Weapon.setUp(testGamePanel)

    // Setup test animation
    val testImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
    val mockAnimation = Vector(Vector(testImage), Vector(testImage), Vector(testImage))
    Cache.animationCached = Cache.animationCached + ("Arrow01" -> mockAnimation)
    Cache.animationCached = Cache.animationCached + ("Explo01" -> mockAnimation)
    Cache.animationCached = Cache.animationCached + ("MagicBullet01" -> mockAnimation)

  "Weapon" should "clone Arrow weapon correctly" in {
    val arrow = Weapon.clone("Arrow01", testEnemy, (50.0, 50.0))
    arrow shouldBe a[Arrow]
    arrow.getPosition shouldBe (50.0, 50.0)
    arrow.getCurrentEnemy shouldBe testEnemy
  }

  it should "clone Explo weapon correctly" in {
    val explo = Weapon.clone("Explo01", testEnemy, (50.0, 50.0))
    explo shouldBe a[Explo]
    explo.getPosition shouldBe (50.0, 50.0)
  }

  it should "clone MagicBullet weapon correctly" in {
    val magic = Weapon.clone("MagicBullet01", testEnemy, (50.0, 50.0))
    magic shouldBe a[MagicBullet]
    magic.getPosition shouldBe (50.0, 50.0)
  }

  it should "move along attack curve" in {
    val arrow = Weapon.clone("Arrow01", testEnemy, (50.0, 50.0))
    val initialPos = arrow.getPosition

    arrow.update() // Starts attack
    arrow.update() // Moves along curve

    arrow.getPosition should not be initialPos
  }