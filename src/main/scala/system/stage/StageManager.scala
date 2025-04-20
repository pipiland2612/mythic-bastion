package system.stage

import entity.Entity
import game.{GamePanel, GameState}
import utils.SoundConstant

import java.awt.Graphics2D
import java.awt.geom.AffineTransform

/** Manages the game stage, including stage transitions, entity updates, and rendering.
 *
 * @param gp The GamePanel instance that provides access to game state and system handlers.
 */
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

  /** Retrieves the grid of the current stage.
   *
   * @return An Option containing the stage's Grid, or None if no stage is active.
   */
  def getGrid: Option[Grid] =
    currentStage match
      case Some(stage) => Some(stage.getGrid)
      case _ => None

  /** Sets a new stage and plays the background music.
   *
   * @param stage The new Stage to set as active.
   */
  def setStage(stage: Stage): Unit =
    currentStage = Some(stage)
    gp.getSystemHandler.playMusic(SoundConstant.GAME_BG_SOUND)

  /** Starts the next wave in the current stage.
   */
  def startWave(): Unit =
    currentStage.foreach(_.startWave())

  private def stopWave(): Unit =
    currentStage.foreach(_.stopWave())

  private var endCounter: Int = 0

  /** Updates the game state by checking win/lose conditions and updating entities.
   *
   * This method checks if the player has lost (health <= 0), won (all waves cleared and no enemies remain),
   * or continues normal gameplay by updating entities.
   */
  def update(): Unit =
    currentStage.foreach (stage =>
      if stage.getCurrentPlayer.getHealth <= 0 then handleLoseCondition()
      else
        if stage.getWaveSpawner.getCurrentWave == stage.totalWave && stage.getEnemyList.isEmpty then
          handleWinCondition()
        else handleUpdateEntity(stage)
    )

  /** Handles the lose condition by transitioning to the end stage state after a delay.
   */
  private def handleLoseCondition(): Unit =
    endCounter += 1
    if endCounter >= 60 then
      gp.handleReloadGameState(GameState.EndStageState)
      endCounter = 0

  /** Handles the win condition by transitioning to the win stage state and playing victory sound.
   */
  private def handleWinCondition(): Unit =
    endCounter += 1
    if endCounter >= 60 then
      gp.handleReloadGameState(GameState.WinStageState)
      gp.getSystemHandler.stopMusic()
      gp.getSystemHandler.playSE(SoundConstant.VICTORY)
      endCounter = 0

  /** Updates all entities in the stage (enemies, allies, towers) and filters out invalid enemies.
   *
   * @param stage The current Stage containing the entities to update.
   */
  private def handleUpdateEntity(stage: Stage): Unit =
    stage.getEnemyList.foreach(_.update())
    stage.getAllianceList.foreach(_.update())
    stage.getTowerList.foreach(_.update())
    stage.filterEnemyList(enemy => !enemy.haveReach && !enemy.hasDie)

  /** Renders the stage, including the map and sorted entities, to the provided graphics context.
   *
   * Entities are sorted by their y-coordinate to ensure correct rendering order (e.g., entities further
   * down the screen are drawn later and thus appear in front).
   *
   * @param g2d The Graphics2D context used for rendering.
   */
  def draw(g2d: Graphics2D): Unit =
    currentStage.foreach(stage =>
      // Combine enemy, alliance, and tower lists into a single entity list
      val sortedEntities: List[Entity] = (
        stage.getEnemyList ++
          stage.getAllianceList ++
          stage.getTowerList
        ).sortBy(_.getPosition._2) // Sort by y-coordinate for rendering order

      stage.getMap.getTowerPos.foreach(_.draw(g2d))
      sortedEntities.foreach(_.draw(g2d))
    )

  /** Restarts the current stage by cloning it and replaying the background music.
   */
  def restart(): Unit =
    gp.getSystemHandler.stopMusic()
    currentStage match
      case Some(stage) =>
        this.currentStage = Some(Stage.clone(stage))
        gp.getSystemHandler.playMusic(SoundConstant.GAME_BG_SOUND)
      case _ =>

  def quit(): Unit =
    stopWave()
    currentStage = None

  /** Advances to the next level of the current stage and replays the background music.
   */
  def continue(): Unit =
    gp.getSystemHandler.stopMusic()
      currentStage match
        case Some(stage) =>
          this.currentStage = Some(Stage.nextLevel(stage))
          gp.getSystemHandler.playMusic(SoundConstant.GAME_BG_SOUND)
        case _ =>