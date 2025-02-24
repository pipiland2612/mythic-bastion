
import game.GamePanel
import org.scalatest.funsuite.AnyFunSuite
import system.stage.Stage
import utils.Tools

class StageManagerTest extends AnyFunSuite :

  val gp: GamePanel = GamePanel()
  gp.setUpGame()

  test("should spawn correctly every wave")
    val testCases: Map[String, String] = Map(
    )

    testCases.foreach(test =>
      gp.systemHandler.stageManager.startWave()
    )


