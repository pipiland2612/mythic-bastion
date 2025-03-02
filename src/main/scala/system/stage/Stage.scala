package system.stage

import entity.creature.alliance.Alliance
import entity.creature.enemy.Enemy
import entity.tower.TowerBuild

case class Stage(
  stageName: String,
  stageID: Int,
  difficulty: Int,
  coins: Int,
  spawnPosition: Vector[(Double, Double)],
  waves: Vector[Wave],
  map: GameMap,
):
  private var enemyList: List[Enemy] = List()
  private var allianceList: List[Alliance] = List()

  def getEnemyList: List[Enemy] = enemyList.toList
  def getAllianceList: List[Alliance] = allianceList.toList
  def totalWave: Int = waves.length

  def filterEnemyList(condition: Enemy => Boolean): Unit =
    enemyList = enemyList.filter(condition)
  
  def addEnemy(enemy: Enemy): Unit =
    enemyList :+= enemy

  def addAllianceList(list: Vector[Alliance]): Unit =
    allianceList ++= list

  override def toString: String =
    val alliances = allianceList.map(_.toString).mkString(", ")
    val positions = spawnPosition.map ((x, y) => s"($x, $y)" ).mkString(", ")
    val waveInfo = waves.map(_.toString).mkString("; ")

    s"Stage($stageName, ID: $stageID, Difficulty: $difficulty, " +
      s"Spawn Positions: [$positions], Waves: [$waveInfo], Alliances: [$alliances])"

case class Wave(
  delay: Int,
  enemyData: Vector[EnemyData],
):
  override def toString: String =
    if (enemyData.isEmpty) "Wave(Empty)"
    else s"Wave(${enemyData.map(_.toString).mkString("; ")})"

case class GameMap(
  path: Vector[Vector[(Double,Double)]],
  towerPos: Vector[TowerBuild],
)

case class EnemyData(
  enemyType: Enemy,
  count: Int,
  spawnInterval: Double,
  spawnIndex: Int
):
  override def toString: String =
    s"EnemyData(Type: ${enemyType.toString}, Count: $count, Interval: $spawnInterval, Index: $spawnIndex)"

