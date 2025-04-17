package system

import game.GamePanel
import system.stage.{Stage, StageManager}
import system.upgrade.UpgradeManager
import utils.Tools

import java.awt.Graphics2D

/** Central manager for game systems, coordinating input, stage management, sound, upgrades, and player data.
 *
 * This class acts as a facade, providing access to various subsystems (e.g., KeyHandler, StageManager, Sound)
 * and delegating tasks such as stage setup, updates, rendering, and audio playback. It is initialized with
 * a GamePanel instance to interact with the game's UI and state.
 *
 * @param gp The GamePanel instance that provides access to game state and UI components.
 */
class SystemHandler(gp: GamePanel):
  private val keyHandler: KeyHandler = KeyHandler(gp)
  private val stageManager: StageManager = StageManager(gp)
  private val sound: Sound = Sound()
  private val soundEffect: Sound = Sound()
  private val upgradeManager: UpgradeManager = UpgradeManager()
  private val playerDataManager: PlayerDataManager = PlayerDataManager(gp)

  def getKeyHandler: KeyHandler = keyHandler
  def getStageManager: StageManager = stageManager
  def getSound: Sound = sound
  def getUpgradeManager: UpgradeManager = upgradeManager
  def getPlayerDataManager: PlayerDataManager = playerDataManager

  /** Sets up a stage by loading it from a JSON configuration file.
   *
   * Loads the specified stage (1 to 5) and sets it as the active stage in the stage manager.
   *
   * @param int The stage ID (1 to 5).
   * @throws IllegalArgumentException If the stage ID is not between 1 and 5.
   */
  def setUp(int: Int): Unit =
    require(int >= 1 && int <= 5)
    val stage: Stage = Tools.loadStage(s"stages/Stage0$int.json")
    stageManager.setStage(stage)

  /** Restarts the current stage.
   *
   * Delegates to the stage manager to restart the active stage.
   */
  def restart(): Unit =
    stageManager.restart()

  /** Advances to the next level of the current stage.
   *
   * Delegates to the stage manager to continue to the next stage level.
   */
  def continue(): Unit =
    stageManager.continue()

  /** Updates the game state.
   *
   * Delegates to the stage manager to update the current stage's entities and state.
   */
  def update(): Unit =
    stageManager.update()

  /** Renders the game stage.
   *
   * Delegates to the stage manager to draw the current stage's entities and map.
   *
   * @param g2d The Graphics2D context for rendering.
   */
  def draw(g2d: Graphics2D): Unit =
    stageManager.draw(g2d)

  /** Plays background music from the specified file path.
   *
   * Sets the music file, starts playback, and loops it continuously.
   *
   * @param path The path to the music file.
   */
  def playMusic(path: String): Unit =
    this.sound.setFile(path)
    this.sound.play()
    this.sound.loop()

  /** Stops the currently playing background music.
   */
  def stopMusic(): Unit = this.sound.stop()

  /** Plays a sound effect from the specified file path.
   *
   * Sets the sound effect file and plays it once.
   *
   * @param path The path to the sound effect file.
   */
  def playSE(path: String): Unit =
    this.soundEffect.setFile(path)
    this.soundEffect.play()