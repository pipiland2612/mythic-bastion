package stage

import entity.creature.Alliance
import entity.creature.enemy.{Enemy, Monster01}

import scala.collection.mutable.ListBuffer

case class Stage(
  stageName: String,
  stageID: Int,
  difficulty: Int,
  spawnPosition: Vector[(Int, Int)],
  waves: Vector[Wave],
):
  var allianceList: ListBuffer[Option[Alliance]] = ListBuffer()

case class Wave (val enemyData: Vector[EnemyData])

case class EnemyData (
  val enemyType: Enemy,
  val count: Int,
  val spawnInterval: Double,
  val spawnIndex: Int
)