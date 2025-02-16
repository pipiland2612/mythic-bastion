package stage

import entity.Entity
import game.GamePanel

import java.awt.Graphics2D
import scala.collection.mutable.ListBuffer

class StageManager (gp: GamePanel) :

  var currentStage: Option[Stage] = Some(Stage1())

  def updateCurrentStage(stage: Stage): Unit = currentStage = Some(stage)

  def drawCurrentStage(g2d: Graphics2D): Unit =
    currentStage match
      case Some(stage) =>
        var entityList: ListBuffer[Entity] = ListBuffer()
        stage.enemyList.foreach {
          case Some(enemy) => entityList += enemy
          case None =>
        }
        stage.allianceList.foreach {
          case Some(alliance) => entityList += alliance
          case None =>
        }

        //sort by y coords
        entityList = entityList.sortBy(entity => entity.pos._2)
        entityList.foreach(entity => entity.draw(g2d))
      case None =>