package stage

import entity.Entity
import entity.creature.enemy.Enemy
import game.GamePanel
import javafx.animation.AnimationTimer

import java.awt.Graphics2D
import scala.collection.mutable.ListBuffer

class StageManager (gp: GamePanel) :

  var currentStage: Option[Stage] = None

  def updateCurrentStage(stage: Stage): Unit = currentStage = Some(stage)

  def scheduleSpawning(enemy: Enemy, number: Int, spawnInterval: Double): Unit =
    var lastSpawnTime: Long = 0
    var count: Int = 0
    val timer: AnimationTimer = new AnimationTimer() {
      override def handle(now: Long): Unit =
        if now - lastSpawnTime >= spawnInterval then
          // spawn(enemy)
          lastSpawnTime = now
          count += 1
        if count >= number then stop()
    }
    timer.start()

  def update(): Unit = {}

  def draw(g2d: Graphics2D): Unit =
    currentStage match
      case Some(stage) =>
        var entityList: ListBuffer[Entity] = ListBuffer()
//        stage.enemyList.foreach {
//          case Some(enemy) => entityList += enemy
//          case None =>
//        }
//        stage.allianceList.foreach {
//          case Some(alliance) => entityList += alliance
//          case None =>
//        }

        //sort by y coords
        entityList = entityList.sortBy(entity => entity.pos._2)
        entityList.foreach(entity => entity.draw(g2d))
      case None =>