package stage

import entity.creature.Alliance
import entity.creature.enemy.Enemy

import java.awt.image.BufferedImage
import scala.collection.mutable.ListBuffer

case class Stage(
  stageName: String,
  stageID: Int,
  difficulty: Int,
  coins: Int,
  spawnPosition: Vector[(Double, Double)],
  waves: Vector[Wave],
  map: GameMap,
):
  var enemyList: ListBuffer[Enemy] = ListBuffer()
  var allianceList: ListBuffer[Alliance] = ListBuffer()
  var currentWave: Int = 0

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
  towerImage: BufferedImage,
  towerPos: Vector[(Double,Double)],
)

case class EnemyData(
  enemyType: Enemy,
  count: Int,
  spawnInterval: Double,
  spawnIndex: Int
):
  override def toString: String =
    s"EnemyData(Type: ${enemyType.toString}, Count: $count, Interval: $spawnInterval, Index: $spawnIndex)"

