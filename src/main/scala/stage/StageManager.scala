package stage

import entity.Entity
import entity.creature.enemy.Enemy
import game.GamePanel

import java.awt.Graphics2D
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, PriorityQueue}

class StageManager (gp: GamePanel) :

  var currentStage: Option[Stage] = None
  var isSpawning: Boolean = false

  private def scheduleEnemySpawns(enemyDataList: Vector[EnemyData]): Unit =
    if !isSpawning then
      enemyDataList.foreach ( enemyData =>
        val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        var spawnedCount = 0
        println(s"Scheduling enemy spawns for ${enemyData.enemyType} with interval ${enemyData.spawnInterval} seconds.")

        val task: Runnable = () => (
          if (spawnedCount < enemyData.count) then
            println(s"Spawning enemy ${enemyData.enemyType}, count: $spawnedCount / ${enemyData.count}")
            spawnEnemy(enemyData)
            spawnedCount += 1
          else
            println(s"Max spawn reached for ${enemyData.enemyType}, stopping spawning.")
            scheduler.shutdown()
          )

        // Start the task with the specified interval for each enemy data
        scheduler.scheduleAtFixedRate(task, 0, enemyData.spawnInterval.toLong, TimeUnit.SECONDS)
      )
      isSpawning = true

  private def spawnEnemy(enemyData: EnemyData): Unit =
    currentStage.foreach ( stage =>
      val enemy: Enemy = Enemy.clone(enemyData.enemyType)
      enemy.pos = stage.spawnPosition(enemyData.spawnIndex)
      enemy.path = stage.map.path
      stage.enemyList += enemy

      println(s"Enemy spawned: ${enemyData.enemyType} at position ${enemy.pos}")
    )

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
      var entityList: ListBuffer[Entity] = ListBuffer()
      entityList.addAll(stage.enemyList)
      entityList.addAll(stage.allianceList)

      entityList = entityList.sortBy(entity => entity.pos._2) //sort by y coords
      entityList.foreach(entity => entity.draw(g2d))
    )