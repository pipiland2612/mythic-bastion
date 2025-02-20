package stage

import entity.Entity
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.Tools

import java.awt.Graphics2D
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class StageManager (gp: GamePanel) :

  private var currentStage: Option[Stage] = None
  private var isSpawning: Boolean = false
  var currentPlayer: Option[PlayerStage] = None

  def startWave(): Unit = isSpawning = true

  def setStage(stage: Stage): Unit =
    currentStage = Some(stage)
    currentPlayer = Some(PlayerStage(stage.coins))

  def backgroundImagePath: String =
    currentStage match
      case Some(stage) => s"maps/map${stage.stageID}.jpg"
      case _ => throw new Exception("Can not find background image path")

  def update(): Unit =
    currentStage.foreach ( stage =>
      scheduleWaveSpawn(stage.waves)

      stage.enemyList.toList.foreach(enemy => enemy.update())
      stage.allianceList.toList.foreach(alliance => alliance.update())

      stage.enemyList = stage.enemyList.filter(!_.haveReachBase)
    )

  def draw(g2d: Graphics2D): Unit =
    currentStage.foreach(stage =>
      g2d.drawImage(Tools.loadImage(backgroundImagePath), 0, 0, gp.screenWidth, gp.screenHeight, null)

      var entityList: ListBuffer[Entity] = ListBuffer()
      entityList.addAll(stage.enemyList)
      entityList.addAll(stage.allianceList)

      entityList = entityList.sortBy(entity => entity.pos._2) //sort by y coords
      entityList.foreach(entity => entity.draw(g2d))
    )

  private def scheduleWaveSpawn(waves: Vector[Wave]): Unit =
    if isSpawning then
      val waveScheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
      waves.foreach(wave =>
        val waveTask: Runnable = () => (
          scheduleEnemySpawn(wave.enemyData)
        )
        waveScheduler.schedule(waveTask, wave.delay.toLong, TimeUnit.SECONDS)
        isSpawning = false
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
      isSpawning = false
    )

  private def spawnEnemy(enemyData: EnemyData): Unit =
    currentStage.foreach ( stage =>
      val enemy: Enemy = Enemy.clone(enemyData.enemyType)
      enemy.pos = stage.spawnPosition(enemyData.spawnIndex)
      enemy.setPath(stage.map.path(enemyData.spawnIndex))
      stage.enemyList += enemy
    )