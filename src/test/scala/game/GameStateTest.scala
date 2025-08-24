package game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GameStateTest extends AnyFlatSpec with Matchers:

  "GameState" should "have all expected states" in {
    GameState.values should contain allOf(
      GameState.PlayState,
      GameState.PauseState,
      GameState.TitleState,
      GameState.GameMenuState,
      GameState.UpgradeState,
      GameState.EndStageState,
      GameState.WinStageState,
      GameState.PreStageState
    )
  }

  it should "have exactly 8 states" in {
    GameState.values.length shouldBe 8
  }

  it should "support pattern matching" in {
    def getStateDescription(state: GameState): String = state match
      case GameState.PlayState => "Playing the game"
      case GameState.PauseState => "Game is paused"
      case GameState.TitleState => "Title screen"
      case GameState.GameMenuState => "Main menu"
      case GameState.UpgradeState => "Upgrade screen"
      case GameState.EndStageState => "Stage ended"
      case GameState.WinStageState => "Stage won"
      case GameState.PreStageState => "Pre-stage screen"

    getStateDescription(GameState.PlayState) shouldBe "Playing the game"
    getStateDescription(GameState.PauseState) shouldBe "Game is paused"
    getStateDescription(GameState.UpgradeState) shouldBe "Upgrade screen"
  }

  it should "maintain enum ordinal values" in {
    GameState.PlayState.ordinal should be >= 0
    GameState.PauseState.ordinal should be >= 0
    GameState.TitleState.ordinal should be >= 0
    
    // Ensure all states have different ordinals
    val ordinals = GameState.values.map(_.ordinal).toSet
    ordinals.size shouldBe GameState.values.length
  }