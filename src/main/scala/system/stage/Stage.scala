package system.stage

import entity.creature.alliance.Alliance
import entity.creature.enemy.Enemy
import entity.tower.{BarrackTower, Tower, TowerBuild}
import game.GamePanel
import utils.Tools

/** Represents a game stage, managing enemies, allies, towers, waves, and player resources.
 *
 * This class encapsulates the state and behavior of a single game stage, including its name, ID, difficulty,
 * spawn positions, waves, map, and player data. It provides methods to manage entities (enemies, allies, towers),
 * update resources, and start waves.
 *
 * @param gp The GamePanel instance for accessing game state and UI.
 * @param stageName The name of the stage.
 * @param stageID The unique ID of the stage.
 * @param difficulty The difficulty level of the stage.
 * @param coins The initial coin count for the player.
 * @param spawnPosition The spawn positions for enemies.
 * @param waves The waves of enemies to spawn.
 * @param map The GameMap containing paths and tower build positions.
 */
case class Stage(
  gp: GamePanel,
  private val stageName: String,
  private val stageID: Int,
  private val difficulty: Int,
  private val coins: Int,
  private val spawnPosition: Vector[(Double, Double)],
  private val waves: Vector[Wave],
  private val map: GameMap
):
  private var enemyList: List[Enemy] = List()
  private var allianceList: List[Alliance] = List()
  private var towerList: List[Tower] = List()
  private val waveSpawner: WaveSpawner = WaveSpawner(this)
  private val grid: Grid = Grid(this.gp)
  private val currentPlayer: PlayerStage = PlayerStage(coins)

  def getStageName: String = stageName
  def getStageID: Int = stageID
  def getDifficulty: Int = difficulty
  def getCoins: Int = coins
  def getSpawnPosition: Vector[(Double, Double)] = spawnPosition
  def getWaves: Vector[Wave] = waves
  def getMap: GameMap = map
  def getEnemyList: List[Enemy] = enemyList
  def getAllianceList: List[Alliance] = allianceList
  def getTowerList: List[Tower] = towerList
  def totalWave: Int = waves.length
  def getWaveSpawner: WaveSpawner = waveSpawner
  def getGrid: Grid = grid
  def getCurrentPlayer: PlayerStage = currentPlayer

  /** Updates the player's coin count.
   *
   * @param dx The amount to increment or decrement the coin count.
   */
  def updateCoin(dx: Int): Unit = currentPlayer.updateCoin(dx)

  /** Updates the player's health.
   *
   * @param dx The amount to increment or decrement the health.
   */
  def updateHealth(dx: Int): Unit = currentPlayer.updateHealth(dx)

  /** Filters the enemy list based on a condition.
   *
   * Removes enemies that do not satisfy the given condition, updating the enemy list.
   *
   * @param condition A function that returns true for enemies to keep.
   */
  def filterEnemyList(condition: Enemy => Boolean): Unit =
    enemyList = enemyList.filter(condition)

  /** Adds an enemy to the stage.
   *
   * @param enemy The Enemy to add to the enemy list.
   */
  def addEnemy(enemy: Enemy): Unit =
    enemyList :+= enemy

  /** Adds multiple allies to the stage.
   *
   * @param list A Vector of Alliance objects to add to the alliance list.
   */
  def addAllianceList(list: Vector[Alliance]): Unit =
    allianceList ++= list

  /** Adds a tower to the stage at a tower build position.
   *
   * If a tower already exists at the position, it is removed before adding the new tower. Updates the
   * tower build's state to indicate it has a tower.
   *
   * @param tower The Tower to add.
   * @param towerBuild The TowerBuild position where the tower is placed.
   */
  def addTower(tower: Tower, towerBuild: TowerBuild): Unit =
    towerList.find(tower => tower.getPosition == towerBuild.pos) match
      case Some(tower) => removeTower(tower)
      case _ =>
    tower.setPosition(towerBuild.pos)
    towerBuild.hasTower = true
    towerList :+= tower

  /** Removes a tower from the stage.
   *
   * If the tower is a BarrackTower, removes all associated allies. Updates the tower list by filtering
   * out the specified tower.
   *
   * @param tower The Tower to remove.
   */
  def removeTower(tower: Tower): Unit =
    if tower.getTowerType == BarrackTower.towerType then
      tower.asInstanceOf[BarrackTower].removeAllAlliance()
    towerList = towerList.filter(to => !to.equals(tower))

  /** Retrieves the tower at a tower build position, if present.
   *
   * @param towerBuild The TowerBuild position to check.
   * @return An Option containing the Tower if one exists at the position, or None if not.
   */
  def getTower(towerBuild: TowerBuild): Option[Tower] =
    if towerBuild.hasTower then
      getTowerList.find(_.getPosition == towerBuild.pos)
    else None

  private var hasSpawn: Boolean = false

  /** Starts spawning waves for the stage.
   *
   * Initiates the wave spawner to schedule enemy waves if they have not already been started.
   */
  def startWave(): Unit =
    if !hasSpawn then
      hasSpawn = true
      waveSpawner.scheduleWaveSpawn(waves)

  /** Generates a string representation of the stage.
   *
   * Includes stage name, ID, difficulty, spawn positions, wave information, and alliances.
   *
   * @return A string describing the stage.
   */
  override def toString: String =
    val alliances = allianceList.map(_.toString).mkString(", ")
    val positions = spawnPosition.map ((x, y) => s"($x, $y)" ).mkString(", ")
    val waveInfo = waves.map(_.toString).mkString("; ")

    s"Stage($stageName, ID: $stageID, Difficulty: $difficulty, " +
      s"Spawn Positions: [$positions], Waves: [$waveInfo], Alliances: [$alliances])"

/** Companion object for the Stage class, providing utility methods for stage creation.
 */
object Stage:
  /** Loads the next stage level.
   *
   * Requires the current stage ID to be less than 5, then loads the next stage from a JSON file.
   *
   * @param stage The current Stage.
   * @return The next Stage.
   * @throws IllegalArgumentException If the stage ID is 5 or greater.
   */
  def nextLevel(stage: Stage): Stage =
    require(stage.stageID < 5)
    Tools.loadStage(s"stages/Stage0${stage.stageID + 1}.json")

  /** Creates a clone of the given stage with a reset map.
   *
   * @param stage The Stage to clone.
   * @return A new Stage with the same properties and a reset map.
   */
  def clone(stage: Stage): Stage =
    new Stage(stage.gp, stage.stageName, stage.stageID, stage.difficulty, stage.coins, stage.spawnPosition, stage.waves, stage.map.reset())

end Stage

/** Represents a wave of enemies to be spawned in a stage.
 *
 * @param delay The delay in seconds before the wave starts.
 * @param enemyData A Vector of EnemyData specifying the enemies to spawn.
 */
case class Wave(
  private val delay: Int,
  private val enemyData: Vector[EnemyData],
):
  def getDelay: Int = delay
  def getEnemyData: Vector[EnemyData] = enemyData

  /** Generates a string representation of the wave.
   *
   * @return A string describing the wave's enemy data or "Wave(Empty)" if no enemies are present.
   */
  override def toString: String =
    if (enemyData.isEmpty) "Wave(Empty)"
    else s"Wave(${enemyData.map(_.toString).mkString("; ")})"

/** Represents the map of a stage, including enemy paths and tower build positions.
 *
 * @param path A Vector of Vectors containing (x, y) coordinates for enemy paths.
 * @param towerPos A Vector of TowerBuild objects representing tower build positions.
 */
case class GameMap(
  private val path: Vector[Vector[(Double,Double)]],
  private val towerPos: Vector[TowerBuild]
):
  def getPath: Vector[Vector[(Double,Double)]] = path
  def getTowerPos: Vector[TowerBuild] = towerPos

  /** Creates a new map with reset tower build positions.
   *
   * Resets the tower build positions by creating new TowerBuild instances, preserving the paths.
   *
   * @return A new GameMap with reset tower build positions.
   */
  def reset(): GameMap =
    val newTowerPos: Vector[TowerBuild] = towerPos.map(towerBuild => TowerBuild(towerBuild.gp, towerBuild.pos, towerBuild.towerBuildImage))
    GameMap(path, newTowerPos)

end GameMap

/** Represents data for spawning a group of enemies in a wave.
 *
 * @param enemyType The type of Enemy to spawn.
 * @param count The number of enemies to spawn.
 * @param spawnInterval The time interval (in seconds) between spawns.
 * @param spawnIndex The index of the spawn position and path to use.
 */
case class EnemyData(
  private val enemyType: Enemy,
  private val count: Int,
  private val spawnInterval: Double,
  private val spawnIndex: Int
):
  def getEnemyType: Enemy = enemyType
  def getCount: Int = count
  def getSpawnInterval: Double = spawnInterval
  def getSpawnIndex: Int = spawnIndex

  /** Generates a string representation of the enemy data.
   *
   * @return A string describing the enemy type, count, spawn interval, and spawn index.
   */
  override def toString: String =
    s"EnemyData(Type: ${enemyType.toString}, Count: $count, Interval: $spawnInterval, Index: $spawnIndex)"

end EnemyData