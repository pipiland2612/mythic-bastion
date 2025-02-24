package stage

import entity.creature.enemy.Enemy

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

class WaveSpawner(stageManager: StageManager):
  
  def scheduleWaveSpawn(waves: Vector[Wave]): Unit =
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
    stageManager.currentStage.foreach ( stage =>
      val enemy: Enemy = Enemy.clone(enemyData.enemyType)
      enemy.pos = stage.spawnPosition(enemyData.spawnIndex)
      enemy.setPath(stage.map.path(enemyData.spawnIndex))
      stage.addEnemy(enemy)
    )
