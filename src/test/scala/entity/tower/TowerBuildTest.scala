package entity.tower

import game.GamePanel
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import system.SystemHandler
import system.stage.StageManager

import java.awt.Graphics2D
import java.awt.image.BufferedImage

class TowerBuildTest extends AnyFunSuite with Matchers with MockitoSugar:

  private def createMockGamePanel(): GamePanel =
    val gp = mock[GamePanel]
    val systemHandler = mock[SystemHandler]
    val stageManager = mock[StageManager]
    
    when(gp.getSystemHandler).thenReturn(systemHandler)
    when(systemHandler.getStageManager).thenReturn(stageManager)
    
    gp

  private def createMockImage(): BufferedImage =
    new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)

  test("TowerBuild should initialize with correct properties"):
    val gp = createMockGamePanel()
    val pos = (100.0, 150.0)
    val image = createMockImage()
    
    val towerBuild = TowerBuild(gp, pos, image)
    
    towerBuild.pos should be(pos)
    towerBuild.towerBuildImage should be(image)
    towerBuild.hasTower should be(false)

  test("TowerBuild should have valid draw coordinates"):
    val gp = createMockGamePanel()
    val pos = (100.0, 150.0)
    val image = createMockImage()
    
    val towerBuild = TowerBuild(gp, pos, image)
    
    towerBuild.drawCoords should not be null
    towerBuild.drawCoords._1 should be >= 0.0
    towerBuild.drawCoords._2 should be >= 0.0

  test("TowerBuild should detect points within build range"):
    val gp = createMockGamePanel()
    val pos = (100.0, 100.0)
    val image = createMockImage()
    
    val towerBuild = TowerBuild(gp, pos, image)
    
    // Point at center of build area should be in range
    val centerX = (towerBuild.drawCoords._1 + 10 + 30).toInt
    val centerY = (towerBuild.drawCoords._2 + 10 + 15).toInt
    
    towerBuild.isInBuildRange(centerX, centerY) should be(true)

  test("TowerBuild should detect points outside build range"):
    val gp = createMockGamePanel()
    val pos = (100.0, 100.0)
    val image = createMockImage()
    
    val towerBuild = TowerBuild(gp, pos, image)
    
    // Points far from build area should be out of range
    towerBuild.isInBuildRange(-1000, -1000) should be(false)
    towerBuild.isInBuildRange(10000, 10000) should be(false)

  test("TowerBuild should handle hasTower flag"):
    val gp = createMockGamePanel()
    val pos = (50.0, 50.0)
    val image = createMockImage()
    
    val towerBuild = TowerBuild(gp, pos, image)
    
    towerBuild.hasTower should be(false)
    
    towerBuild.hasTower = true
    towerBuild.hasTower should be(true)
    
    towerBuild.hasTower = false
    towerBuild.hasTower should be(false)

  test("TowerBuild should handle drawing without errors"):
    val gp = createMockGamePanel()
    val pos = (75.0, 125.0)
    val image = createMockImage()
    val g2d = mock[Graphics2D]
    
    val towerBuild = TowerBuild(gp, pos, image)
    
    noException should be thrownBy towerBuild.draw(g2d)

  test("TowerBuild should have consistent range properties"):
    val gp = createMockGamePanel()
    val pos = (200.0, 200.0)
    val image = createMockImage()
    
    val towerBuild = TowerBuild(gp, pos, image)
    
    // Range should be deterministic based on position and image
    val coords1 = towerBuild.drawCoords
    val towerBuild2 = TowerBuild(gp, pos, image)
    val coords2 = towerBuild2.drawCoords
    
    coords1 should be(coords2)

  test("TowerBuild should work with different positions"):
    val gp = createMockGamePanel()
    val image = createMockImage()
    
    val positions = List((0.0, 0.0), (50.0, 100.0), (300.0, 400.0))
    
    positions.foreach { pos =>
      val towerBuild = TowerBuild(gp, pos, image)
      
      towerBuild.pos should be(pos)
      towerBuild.drawCoords should not be null
      towerBuild.hasTower should be(false)
    }

  test("TowerBuild should work with different image sizes"):
    val gp = createMockGamePanel()
    val pos = (100.0, 100.0)
    
    val smallImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
    val largeImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)
    
    val smallBuild = TowerBuild(gp, pos, smallImage)
    val largeBuild = TowerBuild(gp, pos, largeImage)
    
    smallBuild.drawCoords should not be largeBuild.drawCoords
    smallBuild.towerBuildImage should be(smallImage)
    largeBuild.towerBuildImage should be(largeImage)

  test("TowerBuild isInBuildRange should handle edge coordinates"):
    val gp = createMockGamePanel()
    val pos = (100.0, 100.0)
    val image = createMockImage()
    
    val towerBuild = TowerBuild(gp, pos, image)
    
    // Should handle coordinates at various positions without throwing exceptions
    noException should be thrownBy towerBuild.isInBuildRange(0, 0)
    noException should be thrownBy towerBuild.isInBuildRange(-1, -1)
    noException should be thrownBy towerBuild.isInBuildRange(Int.MaxValue, Int.MaxValue)
    noException should be thrownBy towerBuild.isInBuildRange(Int.MinValue, Int.MinValue)