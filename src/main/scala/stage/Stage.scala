package stage

import entity.creature.Alliance
import entity.creature.enemy.{Enemy, Monster01}

import scala.collection.mutable.ListBuffer

abstract class Stage:
  var enemyList: ListBuffer[Enemy]
  var allianceList: ListBuffer[Alliance]

class Stage1 extends Stage:
  var enemyList: ListBuffer[Enemy] = ListBuffer(Enemy.enemyOfName(Monster01.name, 1))
  var allianceList: ListBuffer[Alliance] = ListBuffer()