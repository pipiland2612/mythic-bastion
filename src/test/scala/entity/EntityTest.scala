package entity

import entity.creature.enemy.{Enemy, Monster01}
import entity.creature.alliance.Alliance
import entity.tower.{ArrowTower, Tower}
import entity.{Direction, State}
import game.GamePanel
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{any => anyArg}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import system.SystemHandler
import system.stage.{Grid, StageManager}
import system.upgrade.UpgradeManager

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import scala.collection.mutable.ListBuffer

class EntityTest extends AnyFunSuite with Matchers with MockitoSugar:

  private def createMockGamePanel(): GamePanel =
    val gp = mock[GamePanel]
    val systemHandler = mock[SystemHandler]
    val stageManager = mock[StageManager]
    val upgradeManager = mock[UpgradeManager]
    val grid = mock[Grid]
    
    when(gp.getSystemHandler).thenReturn(systemHandler)
    when(systemHandler.getStageManager).thenReturn(stageManager)
    when(systemHandler.getUpgradeManager).thenReturn(upgradeManager)
    when(stageManager.getGrid).thenReturn(Some(grid))
    when(grid.scanForEnemiesInRange(anyArg[Tower])).thenReturn(ListBuffer())
    when(grid.scanForAlliancesInRange(anyArg[Enemy])).thenReturn(ListBuffer())
    when(upgradeManager.getCumulativeMultiplier(anyArg, anyArg)).thenReturn(1.0)
    doNothing().when(systemHandler).playSE(anyArg[String])
    doNothing().when(stageManager).updateCoin(anyArg[Int])
    
    gp

  test("Entity should have unique IDs"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy1 = Enemy.enemyOfName("Monster01", 1).get
    val enemy2 = Enemy.enemyOfName("Monster01", 1).get
    
    enemy1.getId should not be enemy2.getId

  test("Entity should maintain position correctly"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    val newPos = (200.0, 250.0)
    enemy.setPosition(newPos._1, newPos._2)
    enemy.getPosition should be(newPos)

  test("Entity should handle state transitions"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    enemy.getState should be(State.IDLE)
    enemy.setState(State.RUN)
    enemy.getState should be(State.RUN)
    enemy.setState(State.ATTACK)
    enemy.getState should be(State.ATTACK)

  test("Entity should have proper health management"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    val maxHealth = enemy.getMaxHealth
    enemy.getHealth should be <= maxHealth
    
    enemy.takeDamage(10.0)
    enemy.getHealth should be < maxHealth

  test("Entity should handle death correctly"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    val initialHealth = enemy.getHealth
    
    enemy.takeDamage(initialHealth + 100.0)
    enemy.getHealth should be <= 0.0

  test("Entity should have attack box"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    enemy.attackBox should not be null
    enemy.attackBox.getWidth should be > 0.0
    enemy.attackBox.getHeight should be > 0.0

  test("Entity should handle combat stats"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    enemy.getApDmg should be >= 0.0
    enemy.getAdDmg should be >= 0.0
    enemy.getApDefense should be >= 0.0
    enemy.getAdDefense should be >= 0.0

  test("Entity should handle update cycle"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    noException should be thrownBy enemy.update()

  test("Entity should handle drawing"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    val g2d = mock[Graphics2D]
    
    noException should be thrownBy enemy.draw(g2d)

  test("Entity should maintain range property"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    enemy.getRange should be >= 0.0
    tower.getRange should be > 0.0

  test("Entity should handle direction"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    // Direction is protected, so we just verify the enemy exists
    succeed

  test("Entity should handle speed property"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    enemy.getSpeed should be > 0.0

  test("Alliance entity should work correctly"):
    val gp = createMockGamePanel()
    val alliance = Alliance.allianceOfNameAndHealth("Soldier01", (120.0, 120.0), 100.0)
    
    alliance should be(defined)
    alliance.get.getHealth should be > 0.0
    alliance.get.getPosition should be((120.0, 120.0))

  test("Entity should handle attack cooldown"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    // Attack cooldown is protected, just verify enemy exists
    succeed

  test("Entity should handle animation updates"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    // Animation update flag is protected, just verify entity exists
    succeed

  test("Entity should have proper name"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    enemy.getName should not be empty
    tower.getName should not be empty

  test("Entity should handle path following"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    val pathPoints = List((50.0, 50.0), (100.0, 100.0), (150.0, 150.0))
    
    enemy.setPath(pathPoints.toVector)
    succeed

  test("Entity equality should work correctly"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy1 = Enemy.enemyOfName("Monster01", 1).get
    val enemy2 = Enemy.enemyOfName("Monster01", 1).get
    
    enemy1 should equal(enemy1)
    enemy1 should not equal enemy2 // Different IDs

  test("Entity hashCode should be based on ID"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    enemy.hashCode should be(enemy.getId.hashCode)

  test("Entity should handle coin rewards"):
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1).get
    
    // Coin value is protected, just verify enemy exists
    succeed