package stage

import entity.creature.Alliance
import entity.creature.enemy.{Enemy, Monster01}

import scala.collection.mutable.ListBuffer

case class Stage(
  stageName: String,
  stageID: Int,
  difficulty: Int,
  spawnPosition: Vector[(Int, Int)],
  waves: Vector[Wave]
) :
  var allianceList: ListBuffer[Option[Alliance]] = ListBuffer()

  override def toString: String =
    val alliances = allianceList.map(_.map(_.toString).getOrElse("None")).mkString(", ")
    val positions = spawnPosition.map { case (x, y) => s"($x, $y)" }.mkString(", ")
    val waveInfo = waves.map(_.toString).mkString("; ")

    s"Stage($stageName, ID: $stageID, Difficulty: $difficulty, " +
      s"Spawn Positions: [$positions], Waves: [$waveInfo], Alliances: [$alliances])"

case class Wave(enemyData: Vector[EnemyData]) :
  override def toString: String =
    if (enemyData.isEmpty) "Wave(Empty)"
    else s"Wave(${enemyData.map(_.toString).mkString("; ")})"


case class EnemyData(
  enemyType: Enemy,
  count: Int,
  spawnInterval: Double,
  spawnIndex: Int
) :
  override def toString: String =
    s"EnemyData(Type: ${enemyType.toString}, Count: $count, Interval: $spawnInterval, Index: $spawnIndex)"

