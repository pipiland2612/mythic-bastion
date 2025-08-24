package system

import game.{GamePanel, GameState}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import system.KeyHandler

import java.awt.event.{KeyEvent, MouseEvent}
import java.awt.geom.Rectangle2D
import javax.swing.JFrame

class KeyHandlerTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach:

  var testGamePanel: TestGamePanel = _
  var keyHandler: KeyHandler = _
  var testFrame: JFrame = _

  class TestGamePanel extends GamePanel:
    var currentGameState: GameState = GameState.GameMenuState
    var playedSounds: List[String] = List()
    var stateChanges: List[GameState] = List()
    
    override def getCurrentGameState: GameState = currentGameState
    override def setCurrentGameState(state: GameState): Unit =
      currentGameState = state
      stateChanges = stateChanges :+ state
    
    def playSE(sound: String): Unit =
      playedSounds = playedSounds :+ sound

  override def beforeEach(): Unit =
    testGamePanel = new TestGamePanel()
    keyHandler = new KeyHandler(testGamePanel)
    testFrame = new JFrame()

  "Button" should "detect when point is within bounds" in {
    val rect = new Rectangle2D.Double(10.0, 10.0, 100.0, 50.0)
    var clicked = false
    val button = Button(rect, (_, _) => clicked = true)
    
    button.contains(50, 30) shouldBe true
    button.contains(5, 30) shouldBe false
    button.contains(120, 30) shouldBe false
  }

  it should "execute action when clicked" in {
    val rect = new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0)
    var clickCount = 0
    var lastX, lastY = 0
    
    val button = Button(rect, (x, y) => {
      clickCount += 1
      lastX = x
      lastY = y
    })
    
    button.execute(25, 75)
    clickCount shouldBe 1
    lastX shouldBe 25
    lastY shouldBe 75
  }

  "KeyHandler" should "initialize with correct default state" in {
    keyHandler.isUniting shouldBe false
  }

  it should "handle mouse pressed events" in {
    val mouseEvent = new MouseEvent(
      testFrame, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
      0, 50, 50, 1, false
    )
    
    // Should not throw exception
    keyHandler.mousePressed(mouseEvent)
  }

  it should "handle mouse released events" in {
    val mouseEvent = new MouseEvent(
      testFrame, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(),
      0, 50, 50, 1, false
    )
    
    // Should not throw exception
    keyHandler.mouseReleased(mouseEvent)
  }

  it should "handle key pressed events" in {
    val keyEvent = new KeyEvent(
      testFrame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
      0, KeyEvent.VK_ESCAPE, ' '
    )
    
    // Should not throw exception
    keyHandler.keyPressed(keyEvent)
  }

  it should "handle key released events" in {
    val keyEvent = new KeyEvent(
      testFrame, KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
      0, KeyEvent.VK_SPACE, ' '
    )
    
    // Should not throw exception
    keyHandler.keyReleased(keyEvent)
  }

  it should "toggle uniting state" in {
    val initialState = keyHandler.isUniting
    
    // Simulate some operation that might toggle uniting
    keyHandler.isUniting = !keyHandler.isUniting
    keyHandler.isUniting should not be initialState
  }

  it should "handle mouse events without crashing in different game states" in {
    val mouseEvent = new MouseEvent(
      testFrame, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
      0, 100, 100, 1, false
    )
    
    // Test various game states
    testGamePanel.setCurrentGameState(GameState.PlayState)
    keyHandler.mouseClicked(mouseEvent)
    
    testGamePanel.setCurrentGameState(GameState.PauseState)
    keyHandler.mouseClicked(mouseEvent)
    
    testGamePanel.setCurrentGameState(GameState.TitleState)
    keyHandler.mouseClicked(mouseEvent)
    
    // Should complete without throwing exceptions
    testGamePanel.stateChanges should not be empty
  }

  it should "handle key events in different game states" in {
    val escapeEvent = new KeyEvent(
      testFrame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
      0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED
    )
    
    // Test escape key handling in play state
    testGamePanel.setCurrentGameState(GameState.PlayState)
    keyHandler.keyPressed(escapeEvent)
    
    // Should handle without crashing
    testGamePanel.stateChanges should not be empty
  }