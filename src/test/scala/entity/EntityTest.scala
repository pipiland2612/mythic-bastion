package entity

import entity.creature.enemy.Creep
import game.GamePanel
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.Cache

import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

class EntityTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach:
  class TestGamePanel extends GamePanel:
    var testValue: Int = 0 // Just to have something testable

  // Test data
  val testRect = new Rectangle2D.Double(0, 0, 32, 32)
  val testImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
  val healthOffset: (Int, Int) = (0, 0)

  // Sample creep parameters
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
    "healthOffSet" -> healthOffset
  )

  var testCreep: Creep = _
  var testGamePanel: TestGamePanel = _

  override def beforeEach(): Unit =
    // Reset Entity ID counter
    val field = classOf[Entity.type].getDeclaredField("idCounter")
    field.setAccessible(true)
    field.set(Entity, 0)

    // Create test game panel
    testGamePanel = new TestGamePanel()

    // Create test creep
    testCreep = Creep(
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
      healthOffSet = baseCreepParams("healthOffSet").asInstanceOf[(Int, Int)],
      pos = (100.0, 100.0)
    )

    // Setup test animation
    val mockAnimation = Vector(Vector(testImage))
    if !Cache.animationCached.contains(testCreep.name) then
      Cache.animationCached = Cache.animationCached + (testCreep.name -> mockAnimation)

  it should "update state and animation correctly" in {
    testCreep.setState(State.ATTACK)
    testCreep.getState shouldBe State.ATTACK

    testCreep.update()
  }

  it should "maintain proper equality and hashcode" in {
    val creep2 = Creep(
      baseCreepParams("name").asInstanceOf[String],
      baseCreepParams("maxHealth").asInstanceOf[Double],
      baseCreepParams("playerDamage").asInstanceOf[Double],
      baseCreepParams("apDmg").asInstanceOf[Double],
      baseCreepParams("apDefense").asInstanceOf[Double],
      baseCreepParams("adDmg").asInstanceOf[Double],
      baseCreepParams("adDefense").asInstanceOf[Double],
      baseCreepParams("range").asInstanceOf[Double],
      baseCreepParams("speed").asInstanceOf[Double],
      baseCreepParams("maxAttackCoolDown").asInstanceOf[Double],
      baseCreepParams("maxDeadCounter").asInstanceOf[Double],
      baseCreepParams("coin").asInstanceOf[Double],
      baseCreepParams("jsonPath").asInstanceOf[String],
      baseCreepParams("imagePath").asInstanceOf[String],
      testRect,
      testGamePanel,
      healthOffset,
      pos = (200.0, 200.0)
    )

    testCreep should equal(testCreep)
    testCreep should not equal creep2
    testCreep.hashCode shouldBe testCreep.getId.hashCode
  }

  it should "initialize with correct combat stats" in {
    testCreep.apDmg shouldBe 5.0
    testCreep.apDefense shouldBe 2.0
    testCreep.adDmg shouldBe 8.0
    testCreep.adDefense shouldBe 3.0
    testCreep.playerDamage shouldBe 10.0
  }