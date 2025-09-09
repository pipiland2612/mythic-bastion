package system

import game.{GamePanel, Player}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{any => anyArg}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import system.stage.StageManager
import system.upgrade.UpgradeManager

import java.awt.Graphics2D

class SystemHandlerTest extends AnyFunSuite with Matchers with MockitoSugar:

  test("SystemHandler should initialize with all subsystems"):
    // Use real GamePanel since SystemHandler has complex dependencies
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    systemHandler.getKeyHandler should not be null
    systemHandler.getStageManager should not be null
    systemHandler.getSound should not be null
    systemHandler.getUpgradeManager should not be null
    systemHandler.getPlayerDataManager should not be null

  test("SystemHandler should validate stage setup parameters"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Valid stage IDs should not throw
    noException should be thrownBy systemHandler.setUp(1)
    noException should be thrownBy systemHandler.setUp(2)
    noException should be thrownBy systemHandler.setUp(3)
    noException should be thrownBy systemHandler.setUp(4)
    noException should be thrownBy systemHandler.setUp(5)
    
    // Invalid stage IDs should throw IllegalArgumentException
    an[IllegalArgumentException] should be thrownBy systemHandler.setUp(0)
    an[IllegalArgumentException] should be thrownBy systemHandler.setUp(6)
    an[IllegalArgumentException] should be thrownBy systemHandler.setUp(-1)

  test("SystemHandler should delegate restart to stage manager"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Should not throw exceptions
    noException should be thrownBy systemHandler.restart()

  test("SystemHandler should delegate continue to stage manager"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Should not throw exceptions
    noException should be thrownBy systemHandler.continue()

  test("SystemHandler should delegate update to stage manager"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Should not throw exceptions during update
    noException should be thrownBy systemHandler.update()

  test("SystemHandler should delegate draw to stage manager"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    val g2d = mock[Graphics2D]
    
    // Should not throw exceptions during drawing
    noException should be thrownBy systemHandler.draw(g2d)

  test("SystemHandler should handle music operations"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Should not throw exceptions for music operations
    noException should be thrownBy systemHandler.playMusic("test/path/music.wav")
    noException should be thrownBy systemHandler.stopMusic()

  test("SystemHandler should handle sound effect operations"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Should not throw exceptions for sound effect operations
    noException should be thrownBy systemHandler.playSE("test/path/sound.wav")

  test("SystemHandler should provide access to subsystem components"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // All getters should return non-null objects
    val keyHandler = systemHandler.getKeyHandler
    val stageManager = systemHandler.getStageManager
    val sound = systemHandler.getSound
    val upgradeManager = systemHandler.getUpgradeManager
    val playerDataManager = systemHandler.getPlayerDataManager
    
    keyHandler should not be null
    stageManager should not be null
    sound should not be null
    upgradeManager should not be null
    playerDataManager should not be null

  test("SystemHandler should maintain component consistency"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Same instance should be returned on multiple calls
    systemHandler.getKeyHandler should be theSameInstanceAs systemHandler.getKeyHandler
    systemHandler.getStageManager should be theSameInstanceAs systemHandler.getStageManager
    systemHandler.getSound should be theSameInstanceAs systemHandler.getSound
    systemHandler.getUpgradeManager should be theSameInstanceAs systemHandler.getUpgradeManager
    systemHandler.getPlayerDataManager should be theSameInstanceAs systemHandler.getPlayerDataManager

  test("SystemHandler should handle multiple sequential operations"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    val g2d = mock[Graphics2D]
    
    // Should handle multiple operations in sequence
    noException should be thrownBy {
      systemHandler.playMusic("bg.wav")
      systemHandler.playSE("effect.wav")
      systemHandler.update()
      systemHandler.draw(g2d)
      systemHandler.stopMusic()
    }

  test("SystemHandler should handle concurrent sound operations"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Should handle overlapping music and sound effects
    noException should be thrownBy {
      systemHandler.playMusic("music.wav")
      systemHandler.playSE("sound1.wav")
      systemHandler.playSE("sound2.wav")
      systemHandler.stopMusic()
    }

  test("SystemHandler components should be properly initialized"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Each component should be in a valid initial state
    val upgradeManager = systemHandler.getUpgradeManager
    upgradeManager should be(a[UpgradeManager])
    
    val stageManager = systemHandler.getStageManager
    stageManager should be(a[StageManager])

  test("SystemHandler should handle edge cases in stage setup"):
    val gamePanel = GamePanel()
    val systemHandler = SystemHandler(gamePanel)
    
    // Test boundary conditions
    an[IllegalArgumentException] should be thrownBy systemHandler.setUp(Int.MinValue)
    an[IllegalArgumentException] should be thrownBy systemHandler.setUp(Int.MaxValue)