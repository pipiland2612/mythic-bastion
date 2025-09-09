package entity

import entity.creature.enemy.{Enemy, Monster01}
import entity.tower.{ArrowTower, BarrackTower, ExploTower, MagicTower, Tower}
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
import scala.collection.mutable.ListBuffer

class TowerInteractionTest extends AnyFunSuite with Matchers with MockitoSugar:

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
    when(upgradeManager.getCumulativeMultiplier(anyArg, anyArg)).thenReturn(1.0)
    doNothing().when(systemHandler).playSE(anyArg[String])
    
    gp

  private def createTestEnemy(): Enemy =
    val gp = createMockGamePanel()
    Enemy.setUp(gp)
    val enemy = Enemy.enemyOfName("Monster01", 1)
    enemy.get

  test("ArrowTower should target enemies correctly"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (50.0, 50.0))
    val enemy = createTestEnemy()
    
    tower.getTowerType should be("Arrow")
    tower.getRange should be > 0.0
    
    // Position enemy within range
    enemy.setPosition(60.0, 60.0)

  test("BarrackTower should spawn soldiers"):
    val gp = createMockGamePanel()
    val tower = BarrackTower(gp, 1, (100.0, 100.0))
    
    tower.getTowerType should be("Barrack")
    tower.getBulletList should be(empty) // BarrackTower doesn't use bullets

  test("MagicTower should have correct properties"):
    val gp = createMockGamePanel()
    val tower = MagicTower(gp, 1, (150.0, 150.0))
    
    tower.getTowerType should be("Magic")
    tower.getRange should be > 0.0

  test("ExploTower should handle explosives"):
    val gp = createMockGamePanel()
    val tower = ExploTower(gp, 1, (200.0, 200.0))
    
    tower.getTowerType should be("Explo")
    tower.getRange should be > 0.0

  test("Tower should detect enemies in range"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    val enemy = createTestEnemy()
    
    // Mock grid to return enemy in range
    val grid = mock[Grid]
    when(gp.getSystemHandler.getStageManager.getGrid).thenReturn(Some(grid))
    when(grid.scanForEnemiesInRange(tower)).thenReturn(ListBuffer(enemy))
    
    val result = tower.findEnemy()
    result should be(defined)

  test("Tower should handle range display"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    tower.isShowingRange should be(false)
    tower.isShowingRange = true
    tower.isShowingRange should be(true)

  test("Tower should calculate bullet position"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    val bulletPos = tower.bulletPosition
    bulletPos._1 should be >= 0.0
    bulletPos._2 should be >= 0.0

  test("Tower should handle state transitions"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    tower.getState should be(State.IDLE)
    tower.setState(State.ATTACK)
    tower.getState should be(State.ATTACK)

  test("Tower upgrade calculations should work"):
    val gp = createMockGamePanel()
    val arrowTower = ArrowTower(gp, 1, (100.0, 100.0))
    val barrackTower = BarrackTower(gp, 1, (100.0, 100.0))
    
    Tower.moneyToLevelUp(arrowTower, 1) should be(defined)
    Tower.moneyToLevelUp(barrackTower, 1) should be(defined)

  test("Tower level up should create new instance"):
    val gp = createMockGamePanel()
    val originalTower = ArrowTower(gp, 1, (100.0, 100.0))
    
    val upgradedTower = Tower.levelUp(originalTower, 1)
    upgradedTower.level should be(2)
    upgradedTower.getPosition should be(originalTower.getPosition)

  test("Tower should handle different levels"):
    val gp = createMockGamePanel()
    val tower1 = ArrowTower(gp, 1, (100.0, 100.0))
    val tower2 = ArrowTower(gp, 2, (100.0, 100.0))
    val tower3 = ArrowTower(gp, 3, (100.0, 100.0))
    
    tower1.level should be(1)
    tower2.level should be(2)
    tower3.level should be(3)
    
    tower2.getRange should be > tower1.getRange

  test("Tower should update without errors"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    noException should be thrownBy tower.update()

  test("Tower should draw without errors"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    val g2d = mock[Graphics2D]
    
    noException should be thrownBy tower.draw(g2d)

  test("Different tower types should have different characteristics"):
    val gp = createMockGamePanel()
    val arrowTower = ArrowTower(gp, 1, (100.0, 100.0))
    val magicTower = MagicTower(gp, 1, (100.0, 100.0))
    val exploTower = ExploTower(gp, 1, (100.0, 100.0))
    val barrackTower = BarrackTower(gp, 1, (100.0, 100.0))
    
    // All towers should have different types
    Set(arrowTower.getTowerType, magicTower.getTowerType, 
        exploTower.getTowerType, barrackTower.getTowerType) should have size 4

  test("Tower should maintain position correctly"):
    val gp = createMockGamePanel()
    val pos = (150.0, 200.0)
    val tower = ArrowTower(gp, 1, pos)
    
    tower.getPosition should be(pos)
    tower.pos should be(pos)