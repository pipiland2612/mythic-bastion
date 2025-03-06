package system.stage

import entity.Entity
import game.GamePanel

import java.awt.Graphics2D
import java.awt.geom.AffineTransform

class StageManager (gp: GamePanel):

  private val transform = new AffineTransform()
  private var currentStage: Option[Stage] = None
  private var currentPlayer: Option[PlayerStage] = None

  def getCurrentStage: Option[Stage] = currentStage
  def getCurrentPlayer: Option[PlayerStage] = currentPlayer
  def getCurrentWave: Option[Int] =
    currentStage match
      case Some(stage) => Some(stage.getWaveSpawner.getCurrentWave)
      case _ => None

  def updateCoin(dx: Int): Unit = currentPlayer.foreach(_.updateCoin(dx))
  def updateHealth(dx: Int): Unit = currentPlayer.foreach(_.updateHealth(dx))

  def setStage(stage: Stage): Unit =
    currentStage = Some(stage)
    currentPlayer = Some(PlayerStage(stage.getCoins))

  def startWave(): Unit =
    currentStage.foreach(stage => stage.getWaveSpawner.scheduleWaveSpawn(stage.getWaves))

  def update(): Unit =
    currentStage.foreach (stage =>
      stage.getEnemyList.toList.foreach(_.update())
      stage.getAllianceList.toList.foreach(_.update())
      stage.getTowerList.foreach(_.update())
      stage.filterEnemyList(enemy => !enemy.haveReach && !enemy.hasDie)
    )

  def draw(g2d: Graphics2D): Unit =
    currentStage.foreach(stage =>
      // add enemylist, alliance list, and tower list to one entity list
      val sortedEntities: List[Entity] = (
        stage.getEnemyList ++
        stage.getAllianceList ++
        stage.getTowerList
      ).sortBy(_.getPosition._2) // then sort by y coords to draw

      stage.getMap.getTowerPos.foreach(_.draw(g2d))
      sortedEntities.foreach(_.draw(g2d))
    )

  def restart(): Unit =
    currentStage match
      case Some(stage) =>
        this.currentStage = Some(Stage.clone(stage))
        this.currentPlayer match
          case Some(player) => this.currentPlayer = Some(PlayerStage(stage.getCoins))
          case _ =>
      case _ =>