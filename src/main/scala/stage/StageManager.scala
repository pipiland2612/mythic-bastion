package stage

import entity.Entity
import entity.creature.enemy.Enemy
import game.GamePanel
import javafx.animation.AnimationTimer
import javafx.application.Platform

import java.awt.Graphics2D
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.Timer
import scala.collection.mutable.ListBuffer

class StageManager (gp: GamePanel) :

  var currentStage: Option[Stage] = None
  var isEndOfWave: Boolean = false

  def spawnEnemy(enemyData: EnemyData): Unit =
    currentStage.foreach(stage =>
      val enemy: Enemy = Enemy.clone(enemyData.enemyType)
      enemy.pos = stage.spawnPosition(enemyData.spawnIndex)
      enemy.path = stage.map.path
      stage.enemyList += enemy
    )

  def scheduleSpawning(enemyData: EnemyData): Unit =
    var count = 0
    val timer = new Timer(enemyData.spawnInterval.toInt, new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit =
        spawnEnemy(enemyData)
        count += 1
        if (count >= enemyData.count) then
          isEndOfWave = true
          e.getSource.asInstanceOf[Timer].stop()
    })

    timer.setInitialDelay(0)
    timer.start()

  def update(): Unit =
    currentStage.foreach(stage =>
      if isEndOfWave then
        stage.currentWave += 1
        isEndOfWave = false
      scheduleSpawning(stage.waves(stage.currentWave).enemyData(0))
      stage.enemyList.foreach(enemy => enemy.update())
      stage.allianceList.foreach(enemy => enemy.update())
    )

  def draw(g2d: Graphics2D): Unit =
    currentStage.foreach(stage =>
      var entityList: ListBuffer[Entity] = ListBuffer()
      entityList.addAll(stage.enemyList)
      entityList.addAll(stage.allianceList)

      entityList = entityList.sortBy(entity => entity.pos._2) //sort by y coords
      entityList.foreach(entity => entity.draw(g2d))
    )