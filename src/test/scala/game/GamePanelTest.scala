package game

import gui.GUI
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import system.SystemHandler

import java.awt.Dimension

class GamePanelTest extends AnyFunSuite with Matchers with MockitoSugar:

  test("GamePanel should initialize with correct screen dimensions"):
    val gamePanel = GamePanel()
    
    gamePanel.screenWidth should be(16 * 3 * 20) // tileSize * maxScreenColumn
    gamePanel.screenHeight should be(16 * 3 * 12) // tileSize * maxScreenRow
    
    val expectedDimension = new Dimension(gamePanel.screenWidth, gamePanel.screenHeight)
    gamePanel.getPreferredSize should be(expectedDimension)

  test("GamePanel should initialize with required components"):
    val gamePanel = GamePanel()
    
    gamePanel.getSystemHandler should not be null
    gamePanel.getGUI should not be null
    gamePanel.getPlayer should not be null
    gamePanel.getCurrentGameState should not be null

  test("GamePanel should start with GameMenuState"):
    val gamePanel = GamePanel()
    
    gamePanel.getCurrentGameState should be(GameState.GameMenuState)

  test("GamePanel should handle game state changes"):
    val gamePanel = GamePanel()
    
    gamePanel.setCurrentGameState(GameState.TitleState)
    gamePanel.getCurrentGameState should be(GameState.TitleState)
    
    gamePanel.setCurrentGameState(GameState.PlayState)
    gamePanel.getCurrentGameState should be(GameState.PlayState)
    
    gamePanel.setCurrentGameState(GameState.PauseState)
    gamePanel.getCurrentGameState should be(GameState.PauseState)

  test("GamePanel should provide access to all major components"):
    val gamePanel = GamePanel()
    
    val systemHandler = gamePanel.getSystemHandler
    val gui = gamePanel.getGUI
    val player = gamePanel.getPlayer
    
    systemHandler should be(a[SystemHandler])
    gui should be(a[GUI])
    player should be(a[Player])

  test("GamePanel should maintain component consistency"):
    val gamePanel = GamePanel()
    
    // Same instance should be returned on multiple calls
    gamePanel.getSystemHandler should be theSameInstanceAs gamePanel.getSystemHandler
    gamePanel.getGUI should be theSameInstanceAs gamePanel.getGUI
    gamePanel.getPlayer should be theSameInstanceAs gamePanel.getPlayer

  test("GamePanel should handle state reload without errors"):
    val gamePanel = GamePanel()
    
    noException should be thrownBy gamePanel.handleReloadGameState(GameState.TitleState)
    noException should be thrownBy gamePanel.handleReloadGameState(GameState.GameMenuState)
    noException should be thrownBy gamePanel.handleReloadGameState(GameState.PreStageState)

  test("GamePanel should handle game setup without errors"):
    val gamePanel = GamePanel()
    
    noException should be thrownBy gamePanel.setUpGame()

  test("GamePanel should handle stage setup with valid stage numbers"):
    val gamePanel = GamePanel()
    
    // Valid stages should not throw exceptions
    noException should be thrownBy gamePanel.setUpStage(1)
    noException should be thrownBy gamePanel.setUpStage(2)
    noException should be thrownBy gamePanel.setUpStage(3)
    noException should be thrownBy gamePanel.setUpStage(4)
    noException should be thrownBy gamePanel.setUpStage(5)

  test("GamePanel should transition to PlayState after stage setup"):
    val gamePanel = GamePanel()
    
    gamePanel.setUpStage(1)
    gamePanel.getCurrentGameState should be(GameState.PlayState)

  test("GamePanel should handle all game states in reload"):
    val gamePanel = GamePanel()
    
    val allStates = List(
      GameState.GameMenuState,
      GameState.TitleState,
      GameState.PlayState,
      GameState.PauseState,
      GameState.PreStageState,
      GameState.UpgradeState,
      GameState.EndStageState,
      GameState.WinStageState
    )
    
    allStates.foreach { state =>
      noException should be thrownBy gamePanel.handleReloadGameState(state)
      gamePanel.getCurrentGameState should be(state)
    }

  test("GamePanel should be Runnable"):
    val gamePanel = GamePanel()
    
    gamePanel should be(a[Runnable])

  test("GamePanel should extend JPanel"):
    val gamePanel = GamePanel()
    
    gamePanel should be(a[javax.swing.JPanel])

  test("GamePanel should have correct configuration values"):
    val gamePanel = GamePanel()
    
    // Test that screen dimensions are calculated correctly
    gamePanel.screenWidth should be > 0
    gamePanel.screenHeight should be > 0
    
    // Screen should be a reasonable size
    gamePanel.screenWidth should be >= 800
    gamePanel.screenHeight should be >= 400

  test("GamePanel should handle multiple state transitions"):
    val gamePanel = GamePanel()
    
    val stateSequence = List(
      GameState.TitleState,
      GameState.PreStageState,
      GameState.PlayState,
      GameState.PauseState,
      GameState.PlayState,
      GameState.EndStageState,
      GameState.GameMenuState
    )
    
    stateSequence.foreach { state =>
      noException should be thrownBy gamePanel.handleReloadGameState(state)
      gamePanel.getCurrentGameState should be(state)
    }

  test("GamePanel should initialize player correctly"):
    val gamePanel = GamePanel()
    
    val player = gamePanel.getPlayer
    player should not be null
    player should be(a[Player])

  test("GamePanel should handle stage setup edge cases"):
    val gamePanel = GamePanel()
    
    // Should handle boundary values
    noException should be thrownBy gamePanel.setUpStage(1)
    noException should be thrownBy gamePanel.setUpStage(5)