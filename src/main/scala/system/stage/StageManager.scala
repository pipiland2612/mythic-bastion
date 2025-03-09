package system.stage

import entity.Entity
import game.{GamePanel, GameState}
import utils.SoundConstant

import java.awt.Graphics2D
import java.awt.geom.AffineTransform

class StageManager (gp: GamePanel):

  private val transform = new AffineTransform()
  private var currentStage: Option[Stage] = None

  def getCurrentStage: Option[Stage] = currentStage
  def getCurrentWave: Option[Int] =
    currentStage match
      case Some(stage) => Some(stage.getWaveSpawner.getCurrentWave)
      case _ => None

  def updateCoin(dx: Int): Unit = currentStage.foreach(_.updateCoin(dx))
  def updateHealth(dx: Int): Unit = currentStage.foreach(_.updateHealth(dx))
      
  def getGrid: Option[Grid] =
    currentStage match
      case Some(stage) => Some(stage.getGrid)
      case _ => None

  def setStage(stage: Stage): Unit =
    currentStage = Some(stage)
    gp.getSystemHandler.playMusic(SoundConstant.GAME_BG_SOUND)

  def startWave(): Unit =
    currentStage.foreach(stage => stage.getWaveSpawner.scheduleWaveSpawn(stage.getWaves))

  private var endCounter: Int = 0
  def update(): Unit =
    currentStage.foreach (stage =>
      if stage.getCurrentPlayer.getHealth <= 0 then handleLoseCondition()
      else
        if stage.getWaveSpawner.getCurrentWave == stage.totalWave && stage.getEnemyList.isEmpty then
          handleWinCondition()
        else handleUpdateEntity(stage)
    )

  private def handleLoseCondition(): Unit =
    endCounter += 1
    if endCounter >= 60 then
      gp.handleReloadGameState(GameState.EndStageState)
      endCounter = 0
  private def handleWinCondition(): Unit =
    endCounter += 1
    if endCounter >= 60 then
      gp.handleReloadGameState(GameState.WinStageState)
      gp.getSystemHandler.stopMusic()
      gp.getSystemHandler.playSE(SoundConstant.VICTORY)
      endCounter = 0

  private def handleUpdateEntity(stage: Stage): Unit =
    stage.getEnemyList.toList.foreach(_.update())
    stage.getAllianceList.toList.foreach(_.update())
    stage.getTowerList.foreach(_.update())
    stage.filterEnemyList(enemy => !enemy.haveReach && !enemy.hasDie)

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
    gp.getSystemHandler.stopMusic()
    currentStage match
      case Some(stage) =>
        this.currentStage = Some(Stage.clone(stage))
        gp.getSystemHandler.playMusic(SoundConstant.GAME_BG_SOUND)
      case _ =>

  def continue(): Unit =
    gp.getSystemHandler.stopMusic()
      currentStage match
        case Some(stage) =>
          this.currentStage = Some(Stage.nextLevel(stage))
          gp.getSystemHandler.playMusic(SoundConstant.GAME_BG_SOUND)
        case _ =>