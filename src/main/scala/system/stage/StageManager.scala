package system.stage

import entity.Entity
import game.GamePanel

import java.awt.Graphics2D
import java.awt.geom.AffineTransform

class StageManager (gp: GamePanel):

  private val transform = new AffineTransform()
  private var waveSpawner: WaveSpawner = WaveSpawner(this)
  private var currentStage: Option[Stage] = None
  private var currentPlayer: Option[PlayerStage] = None

  def getCurrentStage: Option[Stage] = currentStage
  def getCurrentPlayer: Option[PlayerStage] = currentPlayer
  def getCurrentWave: Int = waveSpawner.getCurrentWave

  def updateCoin(dx: Int): Unit = currentPlayer.foreach(_.updateCoin(dx))
  def updateHealth(dx: Int): Unit = currentPlayer.foreach(_.updateHealth(dx))

  def setStage(stage: Stage): Unit =
    currentStage = Some(stage)
    currentPlayer = Some(PlayerStage(stage.coins))

  def startWave(): Unit =
    currentStage.foreach(stage => waveSpawner.scheduleWaveSpawn(stage.waves))

  def update(): Unit =
    currentStage.foreach (stage =>
      stage.getEnemyList.toList.foreach(_.update())
      stage.getAllianceList.toList.foreach(_.update())
      stage.map.towerPos.foreach(towerBuild =>
        towerBuild.getCurrentTower.foreach(_.update())
      )
      stage.filterEnemyList(enemy => !enemy.haveReach && !enemy.hasDie)
    )

  def draw(g2d: Graphics2D): Unit =
    currentStage.foreach(stage =>
      // add enemylist, alliance list, and tower list to one entity list
      val sortedEntities: List[Entity] = (
        stage.getEnemyList ++
        stage.getAllianceList ++
        stage.map.towerPos.flatMap(_.getCurrentTower).toList
      ).sortBy(_.getPosition._2) // then sort by y coords to draw

      stage.map.towerPos.foreach(_.draw(g2d))
      sortedEntities.foreach(_.draw(g2d))
    )

  def restart(): Unit =
    this.waveSpawner = WaveSpawner(this)
    currentStage match
      case Some(stage) => this.currentStage = Some(Stage.clone(stage))
      case _ =>