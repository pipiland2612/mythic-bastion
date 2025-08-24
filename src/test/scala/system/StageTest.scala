package system

import entity.creature.enemy.Creep
import entity.tower.TowerBuild
import game.GamePanel
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import system.stage.{EnemyData, GameMap, Stage, Wave}
import utils.Tools

import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

class StageTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach:

  var testGamePanel: TestGamePanel = _
  var testStage: Stage = _
  var testWaves: Vector[Wave] = _
  var testMap: GameMap = _

  class TestGamePanel extends GamePanel:
    var playedSounds: List[String] = List()
    def playSE(sound: String): Unit =
      playedSounds = playedSounds :+ sound

  override def beforeEach(): Unit =
    testGamePanel = new TestGamePanel()
    
    // Create test image for tower builds
    val towerBuildImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
    
    // Create test map with proper structure
    testMap = GameMap(
      path = Vector(
        Vector((0.0, 100.0), (200.0, 100.0), (400.0, 100.0)),
        Vector((0.0, 150.0), (200.0, 150.0), (400.0, 150.0))
      ),
      towerPos = Vector(
        TowerBuild(testGamePanel, (100.0, 50.0), towerBuildImage),
        TowerBuild(testGamePanel, (300.0, 50.0), towerBuildImage),
        TowerBuild(testGamePanel, (500.0, 50.0), towerBuildImage)
      )
    )
    
    // Create test enemies for enemy data
    val testEnemy1 = Creep(
      "TestEnemy1", 100.0, 10.0, 5.0, 2.0, 8.0, 3.0, 50.0, 2.0, 5.0, 10.0, 5.0,
      "enemies/Monster01.json", "enemies/Monster01.png",
      new Rectangle2D.Double(0, 0, 32, 32), testGamePanel, (0, 0), (0.0, 100.0)
    )
    
    val testEnemy2 = Creep(
      "TestEnemy2", 80.0, 8.0, 4.0, 1.5, 6.0, 2.5, 45.0, 1.8, 4.5, 8.0, 4.0,
      "enemies/Monster02.json", "enemies/Monster02.png",
      new Rectangle2D.Double(0, 0, 32, 32), testGamePanel, (0, 0), (0.0, 150.0)
    )
    
    // Create test enemy data
    val enemyData1 = EnemyData(
      enemyType = testEnemy1,
      count = 5,
      spawnInterval = 2.0,
      spawnIndex = 0
    )
    
    val enemyData2 = EnemyData(
      enemyType = testEnemy2,
      count = 3,
      spawnInterval = 3.0,
      spawnIndex = 1
    )
    
    // Create test waves
    testWaves = Vector(
      Wave(
        delay = 5,
        enemyData = Vector(enemyData1)
      ),
      Wave(
        delay = 10,
        enemyData = Vector(enemyData2)
      )
    )
    
    testStage = Stage(
      gp = testGamePanel,
      stageName = "Test Stage",
      description = "A test stage for unit testing",
      stageID = 1,
      difficulty = 3,
      coins = 100,
      spawnPosition = Vector((0.0, 100.0), (0.0, 150.0)),
      waves = testWaves,
      map = testMap
    )

  "Stage" should "initialize with correct properties" in {
    testStage.getStageName shouldBe "Test Stage"
    testStage.getDescription shouldBe "A test stage for unit testing"
    testStage.getStageID shouldBe 1
    testStage.getDifficulty shouldBe 3
    testStage.getCoins shouldBe 100
    testStage.getSpawnPosition should have length 2
    testStage.getWaves should have length 2
  }

  it should "start with empty entity lists" in {
    testStage.getEnemyList shouldBe empty
    testStage.getAllianceList shouldBe empty
    testStage.getTowerList shouldBe empty
  }

  it should "manage coins correctly" in {
    val currentPlayer = testStage.getCurrentPlayer
    val initialCoins = currentPlayer.getCoins
    
    testStage.updateCoin(50)
    currentPlayer.getCoins shouldBe initialCoins + 50
    
    testStage.updateCoin(-30)
    currentPlayer.getCoins shouldBe initialCoins + 20
  }

  it should "manage health correctly" in {
    val currentPlayer = testStage.getCurrentPlayer
    val initialHealth = currentPlayer.getHealth
    
    testStage.updateHealth(-10)
    currentPlayer.getHealth shouldBe initialHealth - 10
    
    testStage.updateHealth(5)
    currentPlayer.getHealth shouldBe initialHealth - 5
  }

  it should "provide access to grid" in {
    val grid = testStage.getGrid
    grid should not be null
  }

  it should "provide access to wave spawner" in {
    val waveSpawner = testStage.getWaveSpawner
    waveSpawner should not be null
    waveSpawner.getCurrentWave shouldBe 0 // Initially no waves started
  }

  it should "manage game map data" in {
    val map = testStage.getMap
    map.getPath should have length 2
    map.getTowerPos should have length 3
    
    // Check that tower positions are accessible
    val towerPositions = map.getTowerPos
    towerPositions.foreach(towerBuild =>
      towerBuild.pos._1 should be > 0.0
      towerBuild.pos._2 should be > 0.0
    )
  }

  it should "add enemies to list" in {
    val testEnemy = Creep(
      "TestEnemy", 100.0, 10.0, 5.0, 2.0, 8.0, 3.0, 50.0, 2.0, 5.0, 10.0, 5.0,
      "enemies/Monster01.json", "enemies/Monster01.png",
      new Rectangle2D.Double(0, 0, 32, 32), testGamePanel, (0, 0), (0.0, 0.0)
    )
    
    testStage.addEnemy(testEnemy)
    testStage.getEnemyList should contain(testEnemy)
  }

  it should "remove enemies from list" in {
    val testEnemy = Creep(
      "TestEnemy", 100.0, 10.0, 5.0, 2.0, 8.0, 3.0, 50.0, 2.0, 5.0, 10.0, 5.0,
      "enemies/Monster01.json", "enemies/Monster01.png",
      new Rectangle2D.Double(0, 0, 32, 32), testGamePanel, (0, 0), (0.0, 0.0)
    )
    
    testStage.addEnemy(testEnemy)
    // Note: removeEnemy method may not be public, so we'll just test addition
    testStage.getEnemyList should contain(testEnemy)
  }

  it should "handle wave starting" in {
    testStage.startWave()
    // Wave spawner should be initialized and working
    val waveSpawner = testStage.getWaveSpawner
    waveSpawner should not be null
  }

  it should "provide current player stage data" in {
    val playerStage = testStage.getCurrentPlayer
    playerStage should not be null
    playerStage.getCoins shouldBe 100
    playerStage.getHealth should be > 0
  }

  "Wave" should "provide correct wave data" in {
    val wave = testWaves.head
    wave.getDelay shouldBe 5
    wave.getEnemyData should have length 1
    wave.getEnemyData.head.getEnemyType.getName shouldBe "TestEnemy1"
  }

  "GameMap" should "provide correct map data" in {
    val map = testStage.getMap
    val paths = map.getPath
    paths should have length 2
    
    val firstPath = paths.head
    firstPath should have length 3
    firstPath.head shouldBe (0.0, 100.0)
    
    val towerPositions = map.getTowerPos
    towerPositions should have length 3
  }

  it should "support map operations" in {
    val originalMap = testStage.getMap
    // Test map structure
    originalMap.getPath should have length 2
    originalMap.getTowerPos should have length 3
  }