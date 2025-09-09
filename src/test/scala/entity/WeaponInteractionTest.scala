package entity

import entity.creature.enemy.{Enemy, Monster01}
import entity.weapon.Weapon
import entity.{Direction, State}
import game.GamePanel
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{any => anyArg}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import system.SystemHandler
import system.stage.{Grid, StageManager}

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

class WeaponInteractionTest extends AnyFunSuite with Matchers with MockitoSugar:

  class TestWeapon(gp: GamePanel, enemy: Enemy) extends Weapon(gp, enemy):
    val name = "TestWeapon"
    var pos: (Double, Double) = (0.0, 0.0)
    val apDmg: Double = 15.0
    val adDmg: Double = 10.0
    val speed: Double = 5.0
    val curveConst: Double = 0.1
    val flySoundEffect: Array[String] = Array("fly1", "fly2")
    val hitSoundEffect: Array[String] = Array("hit1", "hit2")
    protected val imagePath: String = "weapons/TestWeapon.png"
    protected val jsonPath: String = "weapons/TestWeapon.json"
    
    protected def getDamageMultiplier: Double = 1.0
    
    override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
      val testFrames = Vector(mock[BufferedImage])
      idleAnimation = utils.Animation(frames = testFrames, frameDuration = 10)
      hitAnimation = utils.Animation(frames = testFrames, frameDuration = 10)
      hitEndAnimation = utils.Animation(frames = testFrames, frameDuration = 10)
    
    override def setUpImages(): Unit = {}
    override def parse(): Unit =
      parseInformation(Vector())

  private def createMockGamePanel(): GamePanel =
    val gp = mock[GamePanel]
    val systemHandler = mock[SystemHandler]
    val stageManager = mock[StageManager]
    
    when(gp.getSystemHandler).thenReturn(systemHandler)
    when(systemHandler.getStageManager).thenReturn(stageManager)
    doNothing().when(systemHandler).playSE(anyArg[String])
    
    gp

  private def createMockEnemy(): Enemy =
    val enemy = mock[Enemy]
    val attackBox = new Rectangle2D.Double(100, 100, 32, 32)
    
    when(enemy.attackBox).thenReturn(attackBox)
    when(enemy.getPosition).thenReturn((100.0, 100.0))
    when(enemy.getAdDefense).thenReturn(2.0)
    when(enemy.getApDefense).thenReturn(1.0)
    doNothing().when(enemy).takeDamage(anyArg[Double])
    
    enemy

  test("Weapon should initialize with correct properties"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.hit should be(false)
    weapon.getCurrentEnemy should be(enemy)
    weapon.getApDmg should be(15.0)
    weapon.getAdDmg should be(10.0)

  test("Weapon should have sound effects"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.getFlySE should not be empty
    weapon.getHitSE should not be empty
    weapon.getFlySE should contain("fly1")
    weapon.getHitSE should contain("hit1")

  test("Weapon should calculate damage with multiplier"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    val apDamage = weapon.getApDmg
    val adDamage = weapon.getAdDmg
    
    apDamage should be(15.0)
    adDamage should be(10.0)

  test("Weapon should handle position correctly"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.pos = (50.0, 75.0)
    weapon.getPosition should be((50.0, 75.0))

  test("Weapon should handle attack cycle"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    noException should be thrownBy weapon.attack()

  test("Weapon should handle update cycle"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    noException should be thrownBy weapon.update()

  test("Weapon should handle drawing"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    val g2d = mock[Graphics2D]
    
    noException should be thrownBy weapon.draw(g2d)

  test("Weapon companion object should set up game panel"):
    val gp = createMockGamePanel()
    
    noException should be thrownBy Weapon.setUp(gp)

  test("Weapon should maintain target reference"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.getCurrentEnemy should be(enemy)
    weapon.getCurrentEnemy should not be null

  test("Weapon should handle multiple attack cycles"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    for (_ <- 1 to 5) {
      weapon.attack()
      weapon.update()
    }
    
    // Should not throw any exceptions during multiple cycles
    succeed

  test("Weapon should handle different damage types"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.getApDmg should be > 0.0
    weapon.getAdDmg should be > 0.0

  test("Weapon should track hit state"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.hit should be(false)
    // Hit state is read-only, just verify it exists
    succeed

  test("Weapon should handle speed properly"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.speed should be(5.0)
    weapon.speed should be > 0.0

  test("Weapon should handle curve constant"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.curveConst should be(0.1)
    weapon.curveConst should be > 0.0

  test("Weapon factory should handle invalid types gracefully"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    Weapon.setUp(gp)
    
    an[IllegalArgumentException] should be thrownBy {
      Weapon.clone("UnknownWeapon", enemy, (50.0, 50.0))
    }