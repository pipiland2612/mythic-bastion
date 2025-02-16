package stage

import entity.creature.Alliance
import entity.creature.enemy.{Enemy, Monster01}

import scala.collection.mutable.ListBuffer

abstract class Stage:
  var enemyList: ListBuffer[Option[Enemy]]
  var allianceList: ListBuffer[Option[Alliance]] = ListBuffer()

object Stage:
  def numberToStage(num: Int): Stage =
    num match
      case 1 => Stage1()

class Stage1 extends Stage:
  var enemyList: ListBuffer[Option[Enemy]] = ListBuffer(
    Enemy.enemyOfName(Monster01.name, 1),
    Enemy.enemyOfName(Monster01.name, 1),
  )

