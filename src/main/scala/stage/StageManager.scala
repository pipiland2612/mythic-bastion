package stage

import entity.Entity
import game.GamePanel

import java.awt.Graphics2D
import scala.collection.mutable.ListBuffer

class StageManager (gp: GamePanel) :

  var currentStage: Stage = Stage1()

  def drawCurrentStage(g2d: Graphics2D): Unit =
    if currentStage == null then return

    var entityList: ListBuffer[Entity] = ListBuffer()
    currentStage.enemyList.foreach(enemy => entityList += enemy)
    currentStage.allianceList.foreach(alliance => entityList += alliance)

    entityList = entityList.sortBy(entity => entity.pos._2)

    entityList.foreach(entity => entity.draw(g2d))