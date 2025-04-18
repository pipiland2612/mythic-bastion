package game

import entity.creature.alliance.Alliance
import entity.creature.enemy.Enemy
import entity.weapon.Weapon
import game.GameState.{PreStageState, UpgradeState}
import gui.GUI
import system.SystemHandler
import utils.{SoundConstant, Tools}

import java.awt.image.BufferedImage
import java.awt.*
import javax.swing.JPanel

/** Represents the main game panel responsible for rendering and updating game content.
 * Extends JPanel for GUI rendering and Runnable for game loop execution.
 */
class GamePanel extends JPanel with Runnable:

  // Configuration
  private val scale: Int = 3
  private val maxScreenColumn: Int = 20
  private val maxScreenRow: Int = 12
  private val FPS: Int = 90
  private val tileSize: Int = 16 * scale
  val screenWidth: Int = tileSize * maxScreenColumn
  val screenHeight: Int = tileSize * maxScreenRow

  // System initialize
  private val player: Player = Player()
  private val gui: GUI = GUI(this)
  private val systemHandler: SystemHandler = SystemHandler(this)
  private var currentGameState: GameState = GameState.GameMenuState
  private var gameThread: Thread = _
  private var backgroundImage: BufferedImage = _

  this.setPreferredSize(new Dimension(screenWidth, screenHeight))
  this.setBackground(Color.BLACK)

  def getSystemHandler: SystemHandler = systemHandler
  def getGUI: GUI = gui
  def getCurrentGameState: GameState = currentGameState
  def setCurrentGameState(gst: GameState): Unit = currentGameState = gst
  def getPlayer: Player = player

  /** Changes the background image with specified scaling.
   * @param imgPath Path to the image file
   * @param scaleX Desired width of the scaled image
   * @param scaleY Desired height of the scaled image
   */
  private def changeBackgroundImage(imgPath: String, scaleX: Int, scaleY: Int): Unit =
    this.backgroundImage = Tools.scaleImage(Tools.loadImage(imgPath), scaleX, scaleY)

  /** Handles game state transitions and reloads necessary resources.
   * @param gameState The new game state to transition to
   */
  def handleReloadGameState(gameState: GameState): Unit =
    if gameState == GameState.TitleState &&
      !(currentGameState == PreStageState || currentGameState == UpgradeState)
    then
      getSystemHandler.stopMusic()
      getSystemHandler.playMusic(SoundConstant.MAP_BG_SOUND)
    if gameState == GameState.UpgradeState then
      gui.getupgradeGUI.reload()
    currentGameState = gameState
    reloadGameBackGround()
    gui.reset()

  /** Reloads the background image based on the current game state. */
  private def reloadGameBackGround(): Unit =
    getCurrentGameState match
      case GameState.PlayState       =>
        getSystemHandler.getStageManager.getCurrentStage.foreach(stage =>
          changeBackgroundImage(s"maps/map${stage.getStageID}.jpg", screenWidth, screenHeight)
        )
      case GameState.TitleState      => changeBackgroundImage(s"maps/map.jpg", screenWidth, screenHeight)
      case GameState.GameMenuState   => changeBackgroundImage(s"maps/mainmenu.png", screenWidth, screenHeight)
      case GameState.PreStageState   => changeBackgroundImage(s"maps/prestage.png", screenWidth, screenHeight)
      case GameState.UpgradeState    => changeBackgroundImage(s"maps/upgrade_bg.png", screenWidth, screenHeight)
      case _ =>

  /** Initializes game components and resources. */
  def setUpGame(): Unit =
    Enemy.setUp(this)
    Alliance.setUp(this)
    Weapon.setUp(this)
    Tools.setUp(this)
    reloadGameBackGround()
    systemHandler.getUpgradeManager.loadUpgrades()
    systemHandler.getPlayerDataManager.loadPlayerData()
    systemHandler.playMusic(SoundConstant.MAP_BG_SOUND)

  /** Sets up a specific game stage.
   * @param int Stage identifier
   */
  def setUpStage(int: Int): Unit =
    systemHandler.stopMusic()
    systemHandler.setUp(int)
    handleReloadGameState(GameState.PlayState)

  def startGameThread(): Unit =
    gameThread = Thread(this)
    gameThread.start()

  def restart(): Unit =
    systemHandler.restart()
    handleReloadGameState(GameState.PlayState)

  def continue(): Unit =
    systemHandler.continue()
    handleReloadGameState(GameState.PlayState)

  /** Updates game logic based on the current game state. */
  def update(): Unit =
    currentGameState match
      case GameState.PlayState =>
        systemHandler.update()
      case _ =>

  /** Renders the game graphics, including background, game objects, and UI.
   * @param g Graphics context for rendering
   */
  override def paintComponent(g: Graphics): Unit =
    super.paintComponents(g)
    val g2d = g.asInstanceOf[Graphics2D]

    val startTime: Long = System.nanoTime()
    g2d.drawImage(this.backgroundImage, 0, 0, None.orNull)

    if currentGameState == GameState.PlayState then
      systemHandler.draw(g2d)

    gui.drawUI(g2d)
    val x = 10
    val y = 400
    val endTime = System.nanoTime()
    val passTime = endTime - startTime
    g2d.setFont(new Font("Arial", Font.PLAIN, 20))
    g2d.setColor(Color.WHITE)

    // g2d.drawString("Draw time: " + passTime, x, y)

    g2d.dispose()

  /** Main game loop controlling updates and rendering at a fixed frame rate (60 FPS). */
  override def run(): Unit =
    val drawInterval: Double = 1e9 / FPS
    var delta: Double = 0
    var lastTime: Long = System.nanoTime()
    var currTime: Long = 0

    while Option(gameThread).isDefined do
      currTime = System.nanoTime()
      delta += (currTime - lastTime) / drawInterval
      lastTime = currTime

      if delta >= 1 then
        update()
        repaint()
        delta -= 1