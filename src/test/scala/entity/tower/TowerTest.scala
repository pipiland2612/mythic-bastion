package entity.tower

import entity.creature.enemy.{Enemy, Monster01}
import entity.{Direction, State}
import game.{GamePanel, GameState}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{any => anyArg}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import system.SystemHandler
import system.stage.{Grid, Stage, StageManager}
import system.upgrade.UpgradeManager
import utils.Animation

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

class TowerTest extends AnyFunSuite with Matchers with MockitoSugar:

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
    
    gp

  test("Tower should initialize with correct default values"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    tower.level should be(1)
    tower.getName should startWith("Arrow")
    tower.getTowerType should be("Arrow")
    tower.getRange should be > 0.0
    tower.pos should be((100.0, 100.0))

  test("Tower should calculate correct range with level bonus"):
    val gp = createMockGamePanel()
    val tower1 = ArrowTower(gp, 1, (100.0, 100.0))
    val tower2 = ArrowTower(gp, 2, (100.0, 100.0))
    
    tower2.getRange should be > tower1.getRange

  test("Tower should update tower image when level changes"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    tower.level = 2
    noException should be thrownBy tower.updateTowerImage()

  test("Tower should have empty bullet list initially"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    tower.getBulletList should be(empty)

  test("Tower should calculate bullet position"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    val bulletPos = tower.bulletPosition
    bulletPos._1 should be >= 0.0
    bulletPos._2 should be >= 0.0

  test("Tower should find enemies when grid is available"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    val mockEnemies = ListBuffer(mock[Enemy])
    
    val systemHandler = mock[SystemHandler]
    val stageManager = mock[StageManager]
    val grid = mock[Grid]
    
    when(gp.getSystemHandler).thenReturn(systemHandler)
    when(systemHandler.getStageManager).thenReturn(stageManager)
    when(stageManager.getGrid).thenReturn(Some(grid))
    when(grid.scanForEnemiesInRange(tower)).thenReturn(mockEnemies)
    
    val result = tower.findEnemy()
    result should be(defined)
    result.get should have size 1

  test("Tower should return None when no grid is available"):
    val gp = mock[GamePanel]
    val systemHandler = mock[SystemHandler]
    val stageManager = mock[StageManager]
    val upgradeManager = mock[UpgradeManager]
    
    when(gp.getSystemHandler).thenReturn(systemHandler)
    when(systemHandler.getStageManager).thenReturn(stageManager)
    when(systemHandler.getUpgradeManager).thenReturn(upgradeManager)
    when(stageManager.getGrid).thenReturn(None)
    when(upgradeManager.getCumulativeMultiplier(anyArg, anyArg)).thenReturn(1.0)
    
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    val result = tower.findEnemy()
    result should be(empty)

  test("Tower should manage range display state"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    tower.isShowingRange should be(false)
    tower.isShowingRange = true
    tower.isShowingRange should be(true)

  test("Tower should handle update cycle"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    noException should be thrownBy tower.update()

  test("Tower should handle draw cycle"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    val g2d = mock[Graphics2D]
    
    noException should be thrownBy tower.draw(g2d)

  test("Tower companion object should calculate correct level up costs"):
    val gp = createMockGamePanel()
    val arrowTower = ArrowTower(gp, 1, (100.0, 100.0))
    val barackTower = BarrackTower(gp, 1, (100.0, 100.0))
    val magicTower = MagicTower(gp, 1, (100.0, 100.0))
    val exploTower = ExploTower(gp, 1, (100.0, 100.0))
    
    Tower.moneyToLevelUp(arrowTower, 1) should be(Some(110))
    Tower.moneyToLevelUp(barackTower, 1) should be(Some(110))
    Tower.moneyToLevelUp(magicTower, 1) should be(Some(160))
    Tower.moneyToLevelUp(exploTower, 1) should be(Some(220))

  test("Tower companion object should return None for invalid level"):
    val gp = createMockGamePanel()
    val arrowTower = ArrowTower(gp, 4, (100.0, 100.0))
    
    Tower.moneyToLevelUp(arrowTower, 4) should be(None)

  test("Tower companion object should level up towers correctly"):
    val gp = createMockGamePanel()
    val originalTower = ArrowTower(gp, 1, (100.0, 100.0))
    
    val leveledTower = Tower.levelUp(originalTower, 1)
    leveledTower.level should be(2)
    leveledTower.getPosition should be(originalTower.getPosition)

  test("Tower should have correct type hierarchy"):
    val gp = createMockGamePanel()
    val arrowTower = ArrowTower(gp, 1, (100.0, 100.0))
    val barackTower = BarrackTower(gp, 1, (100.0, 100.0))
    val magicTower = MagicTower(gp, 1, (100.0, 100.0))
    val exploTower = ExploTower(gp, 1, (100.0, 100.0))
    
    arrowTower.getTowerType should be("Arrow")
    barackTower.getTowerType should be("Barrack")
    magicTower.getTowerType should be("Magic")
    exploTower.getTowerType should be("Explo")

  test("Tower should handle different levels"):
    val gp = createMockGamePanel()
    val tower1 = ArrowTower(gp, 1, (100.0, 100.0))
    val tower2 = ArrowTower(gp, 2, (100.0, 100.0))
    val tower3 = ArrowTower(gp, 3, (100.0, 100.0))
    
    tower1.level should be(1)
    tower2.level should be(2)
    tower3.level should be(3)

  test("Tower should maintain position"):
    val gp = createMockGamePanel()
    val pos = (150.0, 200.0)
    val tower = ArrowTower(gp, 1, pos)
    
    tower.getPosition should be(pos)
    tower.pos should be(pos)

  test("Tower should provide bullet list access"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    val bulletList = tower.getBulletList
    bulletList should not be null
    bulletList should be(empty)

  test("Tower should support range display"):
    val gp = createMockGamePanel()
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    // Should be able to toggle range display
    tower.isShowingRange = true
    tower.isShowingRange should be(true)
    
    tower.isShowingRange = false
    tower.isShowingRange should be(false)

  test("Tower should calculate range with multipliers"):
    val gp = createMockGamePanel()
    val upgradeManager = mock[UpgradeManager]
    val systemHandler = mock[SystemHandler]
    
    when(gp.getSystemHandler).thenReturn(systemHandler)
    when(systemHandler.getUpgradeManager).thenReturn(upgradeManager)
    when(upgradeManager.getCumulativeMultiplier(anyArg, anyArg)).thenReturn(2.0)
    
    val tower = ArrowTower(gp, 1, (100.0, 100.0))
    
    // Range should be affected by multipliers
    tower.getRange should be > 100.0