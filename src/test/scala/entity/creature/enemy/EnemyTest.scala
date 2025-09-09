package entity.creature.enemy

import entity.{Direction, State}
import game.GamePanel
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{any => anyArg}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import system.SystemHandler
import system.stage.{Grid, StageManager}
import utils.Animation

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

class EnemyTest extends AnyFunSuite with Matchers with MockitoSugar:

  class TestEnemy(gp: GamePanel) extends Enemy(gp):
    val name = "TestEnemy"
    var pos: (Double, Double) = (50.0, 50.0)
    val maxHealth: Double = 100.0
    protected var health: Double = maxHealth
    val coin: Double = 10.0
    val playerDamage: Double = 5.0
    val apDmg: Double = 20.0
    val apDefense: Double = 5.0
    val adDmg: Double = 15.0
    val adDefense: Double = 3.0
    val range: Double = 50.0
    val speed: Double = 2.0
    val maxAttackCoolDown: Double = 30.0
    val maxDeadCounter: Double = 60.0
    val imagePath: String = "enemies/TestEnemy.png"
    val jsonPath: String = "enemies/TestEnemy.json"
    val rect: Rectangle2D = new Rectangle2D.Double(0, 0, 32, 32)
    val healthOffSet: (Int, Int) = (0, -10)
    
    override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
      val testFrames = Vector(mock[BufferedImage])
      walkingAnimation = Animation(frames = testFrames, frameDuration = 10)
      idleAnimation = Animation(frames = testFrames, frameDuration = 10)
      fightingAnimation = Animation(frames = testFrames, frameDuration = 10)
      deadAnimation = Animation(frames = testFrames, frameDuration = 10)
    
    override def setUpImages(): Unit = {}
    override def parse(): Unit =
      parseInformation(Vector())

  private def createMockGamePanel(): GamePanel =
    val gp = mock[GamePanel]
    val systemHandler = mock[SystemHandler]
    val stageManager = mock[StageManager]
    val grid = mock[Grid]
    
    when(gp.getSystemHandler).thenReturn(systemHandler)
    when(systemHandler.getStageManager).thenReturn(stageManager)
    when(stageManager.getGrid).thenReturn(Some(grid))
    doNothing().when(stageManager).updateCoin(anyArg[Int])
    doNothing().when(stageManager).updateHealth(anyArg[Int])
    when(grid.scanForAlliancesInRange(anyArg)).thenReturn(ListBuffer())
    
    gp

  test("Enemy should initialize with correct properties"):
    val gp = createMockGamePanel()
    val enemy = TestEnemy(gp)
    
    enemy.getName should be("TestEnemy")
    enemy.getPosition should be((50.0, 50.0))
    enemy.getMaxHealth should be(100.0)
    enemy.getCoin should be(10.0)
    enemy.haveReach should be(false)

  test("Enemy should set and follow path correctly"):
    val gp = createMockGamePanel()
    val enemy = TestEnemy(gp)
    val path = Vector((100.0, 100.0), (200.0, 200.0), (300.0, 300.0))
    
    enemy.setPath(path)
    enemy.haveReach should be(false)

  test("Enemy should take damage correctly"):
    val gp = createMockGamePanel()
    val enemy = TestEnemy(gp)
    
    enemy.getHealth should be(100.0)
    enemy.takeDamage(50.0)
    enemy.getHealth should be(50.0)

  test("Enemy should handle death and award coins"):
    val gp = createMockGamePanel()
    val enemy = TestEnemy(gp)
    
    enemy.takeDamage(150.0) // Kill the enemy
    enemy.getHealth should be <= 0.0
    
    enemy.update() // Trigger coin award logic
    verify(gp.getSystemHandler.getStageManager).updateCoin(10)

  test("Enemy should not award coins multiple times"):
    val gp = createMockGamePanel()
    val enemy = TestEnemy(gp)
    
    enemy.takeDamage(150.0) // Kill the enemy
    enemy.update()
    enemy.update() // Second update should not award coins again
    
    verify(gp.getSystemHandler.getStageManager, times(1)).updateCoin(10)

  test("Enemy should handle animation updates"):
    val gp = createMockGamePanel()
    val enemy = TestEnemy(gp)
    
    noException should be thrownBy enemy.update()

  test("Enemy should handle drawing"):
    val gp = createMockGamePanel()
    val enemy = TestEnemy(gp)
    val g2d = mock[Graphics2D]
    
    noException should be thrownBy enemy.draw(g2d)

  test("Enemy companion object should set up game panel"):
    val gp = createMockGamePanel()
    
    noException should be thrownBy Enemy.setUp(gp)

  test("Enemy companion object should create enemy from name"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    
    val enemy = Enemy.enemyOfName("Monster01", 1)
    enemy should be(defined)
    enemy.get.getName should be("Monster01")

  test("Enemy companion object should return None for unknown enemy"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    
    val enemy = Enemy.enemyOfName("UnknownEnemy", 1)
    enemy should be(empty)

  test("Enemy companion object should scale enemy stats with difficulty"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    
    val easyEnemy = Enemy.enemyOfName("Monster01", 1)
    val hardEnemy = Enemy.enemyOfName("Monster01", 2)
    
    easyEnemy should be(defined)
    hardEnemy should be(defined)
    
    // Hard enemy should have more health (scaled by difficulty)
    hardEnemy.get.getMaxHealth should be > easyEnemy.get.getMaxHealth

  test("Enemy companion object should clone enemy correctly"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    
    val originalEnemy = Enemy.enemyOfName("Monster01", 1)
    originalEnemy should be(defined)
    
    if originalEnemy.isDefined then
      val clonedEnemy = Enemy.clone(originalEnemy.get)
      clonedEnemy.getName should be(originalEnemy.get.getName)
      clonedEnemy.getMaxHealth should be(originalEnemy.get.getMaxHealth)
      clonedEnemy.getCoin should be(originalEnemy.get.getCoin)

  test("Enemy should handle no grid available for alliance scanning"):
    val gp = mock[GamePanel]
    val systemHandler = mock[SystemHandler]
    val stageManager = mock[StageManager]
    
    when(gp.getSystemHandler).thenReturn(systemHandler)
    when(systemHandler.getStageManager).thenReturn(stageManager)
    when(stageManager.getGrid).thenReturn(None)
    
    val enemy = TestEnemy(gp)
    noException should be thrownBy enemy.update()

  test("Enemy should handle state transitions correctly"):
    val gp = createMockGamePanel()
    val enemy = TestEnemy(gp)
    
    // Test that the enemy can update without throwing exceptions
    noException should be thrownBy enemy.update()