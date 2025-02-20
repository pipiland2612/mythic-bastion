package stage

import entity.Entity
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.Tools

import java.awt.Graphics2D
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, PriorityQueue}

class StageManager (gp: GamePanel) :

  private var currentStage: Option[Stage] = None
  private var isSpawning: Boolean = false

  def startWave(): Unit = isSpawning = true

  def setStage(stage: Stage): Unit = currentStage = Some(stage)

  def backgroundImagePath: String =
    currentStage match
      case Some(stage) => s"maps/map${stage.stageID}.jpg"
      case _ => throw new Exception("Can not find background image path")

  def update(): Unit =
    currentStage.foreach ( stage =>
      val currentWaveData = stage.waves(stage.currentWave).enemyData

      scheduleEnemySpawns(currentWaveData)

      for (enemy <- stage.enemyList.toList) do
        enemy.update()

      for (alliance <- stage.allianceList.toList) do
        alliance.update()

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

  private def scheduleEnemySpawns(enemyDataList: Vector[EnemyData]): Unit =
    if isSpawning then
      enemyDataList.foreach ( enemyData =>
        val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        var spawnedCount = 0

        val task: Runnable = () => (
          if (spawnedCount <= enemyData.count) then
            spawnEnemy(enemyData)
            spawnedCount += 1
          else
            scheduler.shutdown()
        )
        scheduler.scheduleAtFixedRate(task, 0, enemyData.spawnInterval.toLong, TimeUnit.SECONDS)
      )
      isSpawning = false

  private def spawnEnemy(enemyData: EnemyData): Unit =
    currentStage.foreach ( stage =>
      val enemy: Enemy = Enemy.clone(enemyData.enemyType)
      enemy.pos = stage.spawnPosition(enemyData.spawnIndex)
      enemy.setPath(stage.map.path(enemyData.spawnIndex))
      stage.enemyList += enemy
    )