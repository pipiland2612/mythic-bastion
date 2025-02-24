
import org.scalatest.funsuite.AnyFunSuite
import system.stage.Stage
import utils.Tools

class ParserTest extends AnyFunSuite :

  test("add should handle correctly when parsing")
    Tools.parser("enemies/Boss01.json", "enemies/Boss01.png", 2)

  test("add should parse corrent stage")
    val testCases: Map[String, (String, Int, Int, Int, Int)] = Map(
      "stages/Stage01.json" -> ("Forest of Doom", 1, 1, 2, 2),
      "stages/Stage02.json" -> ("Cavern of Shadows", 2, 2, 2, 3),
    )

    testCases.foreach {
      case (filePath, (expectedName, expectedID, expectedDifficulty, expectedSpawnPosLength, expectedWavesLength)) =>
        val stage: Stage = Tools.loadStage(filePath)
        assert(stage.stageName == expectedName)
        assert(stage.stageID == expectedID)
        assert(stage.difficulty == expectedDifficulty)
        assert(stage.spawnPosition.length == expectedSpawnPosLength)
        assert(stage.waves.length == expectedWavesLength)
    }


