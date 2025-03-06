package system.stage

import entity.creature.alliance.Alliance
import entity.creature.enemy.Enemy
import entity.tower.{Tower, TowerBuild}
import game.GamePanel

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

  def getStageName: String = stageName
  def getStageID: Int = stageID
  def getDifficulty: Int = difficulty
  def getCoins: Int = coins
  def getSpawnPosition: Vector[(Double, Double)] = spawnPosition
  def getWaves: Vector[Wave] = waves
  def getMap: GameMap = map
  def getEnemyList: List[Enemy] = enemyList.toList
  def getAllianceList: List[Alliance] = allianceList.toList
  def getTowerList: List[Tower] = towerList.toList
  def totalWave: Int = waves.length
  def getWaveSpawner: WaveSpawner = waveSpawner
  def getGrid: Grid = grid

  def filterEnemyList(condition: Enemy => Boolean): Unit =
    enemyList = enemyList.filter(condition)

  def addEnemy(enemy: Enemy): Unit =
    enemyList :+= enemy

  def addAllianceList(list: Vector[Alliance]): Unit =
    allianceList ++= list

  def addTower(tower: Tower, towerBuild: TowerBuild): Unit =
    tower.setPosition(towerBuild.pos)
    towerBuild.hasTower = true
    towerList :+= tower

  def removeTower(tower: Tower): Unit =
    towerList = towerList.filter(to => to.eq(tower))

  def getTower(towerBuild: TowerBuild): Option[Tower] =
    if towerBuild.hasTower then
      getTowerList.find(_.getPosition == towerBuild.pos)
    else None

  override def toString: String =
    val alliances = allianceList.map(_.toString).mkString(", ")
    val positions = spawnPosition.map ((x, y) => s"($x, $y)" ).mkString(", ")
    val waveInfo = waves.map(_.toString).mkString("; ")

    s"Stage($stageName, ID: $stageID, Difficulty: $difficulty, " +
      s"Spawn Positions: [$positions], Waves: [$waveInfo], Alliances: [$alliances])"

object Stage:
  def clone(stage: Stage): Stage =
    new Stage(stage.gp, stage.stageName, stage.stageID, stage.difficulty, stage.coins, stage.spawnPosition, stage.waves, stage.map.reset())

end Stage

case class Wave(
  private val delay: Int,
  private val enemyData: Vector[EnemyData],
):
  def getDelay: Int = delay
  def getEnemyData: Vector[EnemyData] = enemyData
  override def toString: String =
    if (enemyData.isEmpty) "Wave(Empty)"
    else s"Wave(${enemyData.map(_.toString).mkString("; ")})"

case class GameMap(
  private val path: Vector[Vector[(Double,Double)]],
  private val towerPos: Vector[TowerBuild]
):
  def getPath: Vector[Vector[(Double,Double)]] = path
  def getTowerPos: Vector[TowerBuild] = towerPos

  def reset(): GameMap =
    val newTowerPos: Vector[TowerBuild] = towerPos.map(towerBuild => new TowerBuild(towerBuild.gp, towerBuild.pos, towerBuild.towerBuildImage))
    GameMap(path, newTowerPos)

end GameMap

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

  override def toString: String =
    s"EnemyData(Type: ${enemyType.toString}, Count: $count, Interval: $spawnInterval, Index: $spawnIndex)"

