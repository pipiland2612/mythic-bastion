package stage

import entity.creature.Creature
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.Tools

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

class StageManager (gp: GamePanel):

  private var currentStage: Option[Stage] = None
  private var isSpawning: Boolean = false
  private var backgroundImage: BufferedImage = _
  var currentPlayer: Option[PlayerStage] = None

  def startWave(): Unit =
    currentStage.foreach(stage => scheduleWaveSpawn(stage.waves))

  def setStage(stage: Stage): Unit =
    currentStage = Some(stage)
    currentPlayer = Some(PlayerStage(stage.coins))

  def setUpBackgroundImage(): Unit =
    this.backgroundImage = currentStage match
      case Some(stage) => Tools.scaleImage(Tools.loadImage(s"maps/map${stage.stageID}.jpg"), gp.screenWidth, gp.screenHeight)
      case _ => throw new Exception("Can not find background image path")

  def update(): Unit =
    currentStage.foreach ( stage =>
      stage.enemyList.toList.foreach(_.update())
      stage.allianceList.toList.foreach(_.update())

      stage.enemyList.filterInPlace(!_.haveReachBase)
    )

  def draw(g2d: Graphics2D): Unit =
    currentStage.foreach(stage =>
      g2d.drawImage(backgroundImage, 0, 0, null)

      val sortedEntities: List[Creature] = (stage.enemyList ++ stage.allianceList).toList.sortBy(_.pos._2)
      sortedEntities.foreach(_.draw(g2d))
    )

  private def scheduleWaveSpawn(waves: Vector[Wave]): Unit =
    val waveScheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    waves.foreach(wave =>
      val waveTask: Runnable = () => (
        scheduleEnemySpawn(wave.enemyData)
      )
      waveScheduler.schedule(waveTask, wave.delay.toLong, TimeUnit.SECONDS)
    )

  private def scheduleEnemySpawn(enemyDataList: Vector[EnemyData]): Unit =
    enemyDataList.foreach ( enemyData =>
      val spawnerScheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
      var spawnedCount = 0

      val task: Runnable = () => (
        if (spawnedCount <= enemyData.count) then
          spawnEnemy(enemyData)
          spawnedCount += 1
        else
          spawnerScheduler.shutdown()
      )
      spawnerScheduler.scheduleAtFixedRate(task, 0, enemyData.spawnInterval.toLong, TimeUnit.SECONDS)
    )

  private def spawnEnemy(enemyData: EnemyData): Unit =
    currentStage.foreach ( stage =>
      val enemy: Enemy = Enemy.clone(enemyData.enemyType)
      enemy.pos = stage.spawnPosition(enemyData.spawnIndex)
      enemy.setPath(stage.map.path(enemyData.spawnIndex))
      stage.enemyList += enemy
    )