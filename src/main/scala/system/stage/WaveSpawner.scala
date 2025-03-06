package system.stage

import entity.creature.enemy.Enemy
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

class WaveSpawner(stage: Stage):
  private var currentWave: Int = 0
  private var waveScheduler: ScheduledExecutorService = _
  private var spawnerSchedulers: Vector[ScheduledExecutorService] = Vector()

  def getCurrentWave: Int = currentWave

  def scheduleWaveSpawn(waves: Vector[Wave]): Unit =
    stopAllSchedules()
    waveScheduler = Executors.newScheduledThreadPool(1)
    var cumulativeDelay = 0L
    waves.foreach(wave =>
      val startDelay = cumulativeDelay + wave.getDelay.toLong
      val waveDuration = calculateWaveDuration(wave)
      val waveTask: Runnable = () => {
        scheduleEnemySpawn(wave.getEnemyData)
        currentWave += 1
      }
      waveScheduler.schedule(waveTask, startDelay, TimeUnit.SECONDS)
      cumulativeDelay = startDelay + waveDuration
    )

  private def calculateWaveDuration(wave: Wave): Long =
    var result = 0L
    val durations = wave.getEnemyData.map(enemyData =>
      result = (enemyData.getCount - 1) * enemyData.getSpawnInterval.toLong
    )
    if (durations.isEmpty) then return 0L else result.max

    result / 3

  private def scheduleEnemySpawn(enemyDataList: Vector[EnemyData]): Unit =
    enemyDataList.foreach(enemyData =>
      val spawnerScheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
      spawnerSchedulers = spawnerSchedulers :+ spawnerScheduler
      var spawnedCount = 0

      val task: Runnable = () =>
        if spawnedCount < enemyData.getCount then
          spawnEnemy(enemyData)
          spawnedCount += 1
        else
          spawnerScheduler.shutdown()

      spawnerScheduler.scheduleAtFixedRate(task, 0, enemyData.getSpawnInterval.toLong, TimeUnit.SECONDS)
    )

  private def spawnEnemy(enemyData: EnemyData): Unit =
    val enemy: Enemy = Enemy.clone(enemyData.getEnemyType)
    enemy.setPosition(stage.getSpawnPosition(enemyData.getSpawnIndex))
    enemy.setPath(stage.getMap.getPath(enemyData.getSpawnIndex))
    stage.addEnemy(enemy)

  def stopAllSchedules(): Unit =
    if Option(waveScheduler).isDefined then
      waveScheduler.shutdown()
      waveScheduler = null
    spawnerSchedulers.foreach(_.shutdown())
    spawnerSchedulers = Vector()
