package utils

import entity.tower.TowerBuild
import game.GamePanel
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StageLoadTest extends AnyFlatSpec with Matchers:
  val mockGamePanel: GamePanel = new GamePanel()

  Tools.setUp(mockGamePanel)

  private def testStageLoading(stagePath: String, expectedName: String, expectedId: Int, expectedCoins: Int): Unit =
    s"loadStage for $expectedName" should "load the stage without errors" in {
      noException should be thrownBy Tools.loadStage(stagePath)
    }

    it should "create a Stage with correct basic properties" in {
      val stage = Tools.loadStage(stagePath)

      stage.getStageName shouldBe expectedName
      stage.getStageID shouldBe expectedId
      stage.getCoins shouldBe expectedCoins
      stage.getDifficulty should (be >= 1 and be <= 5)
    }

    it should "have valid spawn positions" in {
      val stage = Tools.loadStage(stagePath)

      stage.getSpawnPosition should not be empty
      stage.getSpawnPosition.foreach((x, y) =>
        x should be >= 0.0
        y should be >= 0.0
      )
    }

    it should "have valid waves configuration" in {
      val stage = Tools.loadStage(stagePath)

      stage.getWaves should not be empty
      stage.getWaves.foreach(wave =>
        wave.getDelay should be >= 0
        wave.getEnemyData should not be empty

        wave.getEnemyData.foreach(enemyData =>
          enemyData.getEnemyType should not be null
          enemyData.getCount should be > 0
          enemyData.getSpawnInterval should be > 0.0
          enemyData.getSpawnIndex should be >= 0
        )
      )
    }

    it should "have a valid game map with paths and tower spots" in {
      val stage = Tools.loadStage(stagePath)
      val map = stage.getMap

      map.getPath should not be empty
      map.getPath.foreach(path =>
        path should not be empty
        path.foreach ((x, y) =>
          x should be >= 0.0
          y should be >= 0.0
        )
      )

      map.getTowerPos should not be empty
      map.getTowerPos.foreach(towerBuild =>
        towerBuild shouldBe a[TowerBuild]
      )
    }

  // Test each stage file
  testStageLoading("stages/Stage01.json", "Sands of Doom", 1, 1000)
  testStageLoading("stages/Stage02.json", "The Scorched Path", 2, 1000)
  testStageLoading("stages/Stage03.json", "The Dune Reaper", 3, 1200)
  testStageLoading("stages/Stage04.json", "Into the forest", 4, 1400)
  testStageLoading("stages/Stage05.json", "New Adventure", 5, 1400)

  it should "validate enemy types exist" in {
    val stage = Tools.loadStage("stages/Stage01.json")

    stage.getWaves.foreach(wave =>
      wave.getEnemyData.foreach(enemyData =>
        enemyData.getEnemyType should not be null
      )
    )
  }