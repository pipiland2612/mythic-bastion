package entity

import entity.creature.enemy.{Creep, Enemy}
import entity.tower.{BarrackTower, Tower}
import game.GamePanel
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import system.SystemHandler
import system.stage.{Grid, StageManager}
import utils.Cache

import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import scala.collection.mutable

class TowerTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach:
  // Test data
  val testRect = new Rectangle2D.Double(0, 0, 32, 32)
  val testImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
  val healthOffset: (Int, Int) = (0, 0)

  var testGamePanel: TestGamePanel = _
  var testCreep: Enemy = _
  var barrackTower: Tower = _
  // Test implementations
  class TestGamePanel extends GamePanel:
    var playedSounds: List[String] = List()
    var enemies: mutable.ListBuffer[Enemy] = mutable.ListBuffer()

    def playSE(sound: String): Unit = {
      playedSounds = playedSounds :+ sound
    }

    def addEnemy(enemy: Enemy): Unit = {
      enemies += enemy
    }

    override def getSystemHandler: SystemHandler = new SystemHandler(testGamePanel) :
      override def getStageManager: StageManager = new StageManager(testGamePanel) :
        override def getGrid: Option[Grid] = Some(new Grid(testGamePanel) :
          def scanForEnemiesInRange(tower: Tower): mutable.ListBuffer[Enemy] =
            enemies.filter(e =>
              e.getPosition._1 >= tower.getPosition._1 - tower.getRange &&
                e.getPosition._1 <= tower.getPosition._1 + tower.getRange &&
                e.getPosition._2 >= tower.getPosition._2 - tower.getRange &&
                e.getPosition._2 <= tower.getPosition._2 + tower.getRange
            )
        )

  override def beforeEach(): Unit = {
    testGamePanel = new TestGamePanel()

    // Create test creep
    testCreep = new Creep(
      name = "TestCreep",
      maxHealth = 100.0,
      playerDamage = 10.0,
      apDmg = 5.0,
      apDefense = 2.0,
      adDmg = 8.0,
      adDefense = 3.0,
      range = 50.0,
      speed = 2.0,
      maxAttackCoolDown = 5.0,
      maxDeadCounter = 10.0,
      coin = 5.0,
      jsonPath = "enemies/Boss01.json",
      imagePath = "enemies/Boss01.png",
      rect = testRect,
      gp = testGamePanel,
      healthOffSet = healthOffset,
      pos = (100.0, 100.0)
    )

    // Create BarrackTower
    barrackTower = BarrackTower(testGamePanel, 1, (50.0, 50.0))

    // Setup test animation
    val mockAnimation = Vector(Vector(testImage))
    Cache.animationCached = Cache.animationCached + (barrackTower.getName -> mockAnimation)

    // Add enemy to game panel
    testGamePanel.addEnemy(testCreep)
  }

  "BarrackTower" should "initialize with correct properties" in {
    barrackTower.level shouldBe 1
    barrackTower.getPosition shouldBe (50.0, 50.0)
    barrackTower.getRange should be > 0.0
  }

  it should "show range when requested" in {
    barrackTower.isShowingRange = true
    // Would verify range circle is drawn in draw method
  }

  it should "level up correctly" in {
    val originalRange = barrackTower.getRange
    val leveledTower = Tower.levelUp(barrackTower, barrackTower.level)
    leveledTower.level shouldBe 2
    leveledTower.getRange should be > originalRange
  }

  it should "calculate correct level up cost" in {
    Tower.moneyToLevelUp(barrackTower, 1) shouldBe defined
  }
