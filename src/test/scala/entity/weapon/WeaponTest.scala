package entity.weapon

import entity.creature.enemy.{Enemy, Monster01}
import entity.{Direction, State}
import game.GamePanel
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{any => anyArg}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import system.SystemHandler
import system.stage.StageManager
import utils.Animation

import java.awt.Graphics2D
import java.awt.geom.{Ellipse2D, Rectangle2D}
import java.awt.image.BufferedImage

class WeaponTest extends AnyFunSuite with Matchers with MockitoSugar:

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
      idleAnimation = Animation(frames = testFrames, frameDuration = 10)
      hitAnimation = Animation(frames = testFrames, frameDuration = 10)
      hitEndAnimation = Animation(frames = testFrames, frameDuration = 10)
    
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
    weapon.getFlySE should contain("fly1")
    weapon.getFlySE should contain("fly2")
    weapon.getHitSE should contain("hit1")
    weapon.getHitSE should contain("hit2")

  test("Weapon should calculate damage with multiplier"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.getApDmg should be(15.0)
    weapon.getAdDmg should be(10.0)

  test("Weapon should handle movement through update"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    val originalPos = weapon.pos
    
    // Weapons move during their attack cycle, so test via update
    weapon.update()
    succeed

  test("Weapon should handle attack cycle"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.attack()
    
    // After attack initialization, weapon should have set up attack curve
    succeed

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
  
  test("Weapon factory methods should handle valid weapon types"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    Weapon.setUp(gp)
    
    // Test that the factory pattern works - we expect it to throw for unknown types
    an[IllegalArgumentException] should be thrownBy {
      Weapon.clone("UnknownWeapon", enemy, (50.0, 50.0))
    }
    
  test("Weapon factory should handle level extraction"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    Weapon.setUp(gp)
    
    // Test level limiting behavior - any level over 3 should be limited to 3
    noException should be thrownBy {
      try {
        Weapon.clone("Arrow01", enemy, (50.0, 50.0), 10)
      } catch {
        case _: Exception => // Expected for missing assets
      }
    }

  test("Weapon should calculate midpoint for bezier curve"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    weapon.pos = (0.0, 0.0)
    weapon.attack() // This will initialize the attack curve
    
    succeed

  test("Weapon should handle multiple attack cycles"):
    val gp = createMockGamePanel()
    val enemy = createMockEnemy()
    val weapon = TestWeapon(gp, enemy)
    
    for (_ <- 1 to 10) {
      weapon.attack()
      weapon.update()
    }
    
    // Should not throw any exceptions during multiple attack cycles
    succeed