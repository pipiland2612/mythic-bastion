package system.stage

import entity.creature.enemy.Enemy

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

class WaveSpawner(stageManager: StageManager):
  private var currentWave: Int = 0

  def getCurrentWave: Int = currentWave
  
  def scheduleWaveSpawn(waves: Vector[Wave]): Unit =
    val waveScheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    waves.foreach(wave =>
      val waveTask: Runnable = () => {
        scheduleEnemySpawn(wave.getEnemyData)
        currentWave += 1
      }
      waveScheduler.schedule(waveTask, wave.getDelay.toLong, TimeUnit.SECONDS)
    )

  private def scheduleEnemySpawn(enemyDataList: Vector[EnemyData]): Unit =
    enemyDataList.foreach ( enemyData =>
      val spawnerScheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
      var spawnedCount = 0

      val task: Runnable = () => (
        if (spawnedCount <= enemyData.getCount) then
          spawnEnemy(enemyData)
          spawnedCount += 1
        else
          spawnerScheduler.shutdown()
      )
      spawnerScheduler.scheduleAtFixedRate(task, 0, enemyData.getSpawnInterval.toLong, TimeUnit.SECONDS)
    )

  private def spawnEnemy(enemyData: EnemyData): Unit =
    stageManager.getCurrentStage.foreach ( stage =>
      val enemy: Enemy = Enemy.clone(enemyData.getEnemyType)
      enemy.setPosition(stage.getSpawnPosition(enemyData.getSpawnIndex))
      enemy.setPath(stage.getMap.getPath(enemyData.getSpawnIndex))
      stage.addEnemy(enemy)
    )
