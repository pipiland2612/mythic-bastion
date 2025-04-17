package system.stage

import entity.creature.enemy.Enemy
import utils.SoundConstant

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

/** Manages the spawning of enemy waves in a game stage.
 *
 * This class schedules and spawns waves of enemies according to the provided wave data, handling delays,
 * spawn intervals, and enemy placement. It uses thread pools to manage asynchronous spawning tasks and
 * ensures proper cleanup of schedules when necessary.
 *
 * @param stage The Stage instance that provides context for spawning enemies (e.g., spawn positions, map paths).
 */
class WaveSpawner(stage: Stage):
  private var currentWave: Int = 0
  private var waveScheduler: ScheduledExecutorService = _
  private var spawnerSchedulers: Vector[ScheduledExecutorService] = Vector()

  /** Retrieves the index of the current wave.
   *
   * @return The current wave number (0-based index).
   */
  def getCurrentWave: Int = currentWave

  /** Schedules the spawning of waves with their respective delays.
   *
   * Stops any existing schedules, then creates a new scheduler to spawn each wave after its cumulative
   * delay. Each wave triggers enemy spawning and plays a sound effect when it starts.
   *
   * @param waves A Vector of Wave objects containing enemy data and delays.
   */
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
        stage.gp.getSystemHandler.playSE(SoundConstant.BO_START)
      }
      waveScheduler.schedule(waveTask, startDelay, TimeUnit.SECONDS)
      cumulativeDelay = startDelay + waveDuration
    )

  /** Calculates the duration of a wave based on enemy spawn intervals and counts.
   *
   * Determines the longest duration among all enemy data in the wave, based on the number of enemies
   * and their spawn intervals, and scales it by a factor of 1/3.
   *
   * @param wave The Wave object containing enemy data.
   * @return The calculated duration of the wave in seconds.
   */
  private def calculateWaveDuration(wave: Wave): Long =
    var result = 0L
    val durations = wave.getEnemyData.map(enemyData =>
      result = (enemyData.getCount - 1) * enemyData.getSpawnInterval.toLong
    )
    if durations.isEmpty then return 0L else result.max
    result / 3

  /** Schedules the spawning of enemies for a wave.
   *
   * Creates a separate scheduler for each enemy type in the wave, spawning enemies at fixed intervals
   * until the specified count is reached. Each scheduler is tracked for later cleanup.
   *
   * @param enemyDataList A Vector of EnemyData objects specifying enemy types, counts, and spawn intervals.
   */
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

  /** Spawns a single enemy instance for the given enemy data.
   *
   * Clones the enemy type, sets its position and path based on the stage's configuration, and adds it
   * to the stage's enemy list.
   *
   * @param enemyData The EnemyData object specifying the enemy type, spawn index, and other details.
   */
  private def spawnEnemy(enemyData: EnemyData): Unit =
    val enemy: Enemy = Enemy.clone(enemyData.getEnemyType)
    enemy.setPosition(stage.getSpawnPosition(enemyData.getSpawnIndex))
    enemy.setPath(stage.getMap.getPath(enemyData.getSpawnIndex))
    stage.addEnemy(enemy)

  /** Stops all active wave and enemy spawn schedules.
   *
   * Shuts down the wave scheduler and all enemy spawner schedulers, clearing the list of spawner
   * schedulers to ensure no tasks continue running.
   */
  private def stopAllSchedules(): Unit =
    if Option(waveScheduler).isDefined then
      waveScheduler.shutdown()
      waveScheduler = null
    spawnerSchedulers.foreach(_.shutdown())
    spawnerSchedulers = Vector()