package game

import entity.creature.enemy.Enemy
import entity.weapon.Weapon
import gui.GUI
import system.SystemHandler
import utils.Tools

import java.awt.{Color, Dimension, Font, Graphics, Graphics2D}
import javax.swing.JPanel


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
  private val systemHandler: SystemHandler = SystemHandler(this)
  private val gui: GUI = GUI(this)
  private var currentGameState: GameState = GameState.PlayState
  private var gameThread: Thread = _

  this.setPreferredSize(new Dimension(screenWidth, screenHeight))
  this.setBackground(Color.BLACK)

  def getSystemHandler: SystemHandler = systemHandler
  def getGUI: GUI = gui
  def getCurrentGameState: GameState = currentGameState
  def setCurrentGameState(gst: GameState) = currentGameState = gst

  def setUpGame(): Unit =
    Enemy.setUp(this)
    Weapon.setUp(this)
    Tools.setUp(this)
    systemHandler.setUp()
    gui.reloadGameBackGround()

  def startGameThread(): Unit =
    gameThread = Thread(this)
    gameThread.start()

  def update(): Unit =
    currentGameState match
      case GameState.PlayState =>
        systemHandler.update()
      case _ =>

  override def paintComponent(g: Graphics): Unit =
    super.paintComponents(g)
    var g2d = g.asInstanceOf[Graphics2D]
    val g2dSaved: Graphics2D = g2d.create().asInstanceOf[Graphics2D]

    val startTime: Long = System.nanoTime()

    gui.drawUI(g2d)
    g2d.dispose()
    g2d = g2dSaved

    systemHandler.draw(g2d)

    val x = 10
    val y = 400
    val endTime = System.nanoTime()
    val passTime = endTime - startTime
    g2d.setFont(new Font("Arial",Font.PLAIN, 20))
    g2d.setColor(Color.WHITE)

    g2d.drawString("Draw time: " + passTime, x,y)

    g2d.dispose()

  override def run(): Unit =
    val drawInterval: Double = 1e9 / FPS
    var delta: Double = 0
    var lastTime: Long = System.nanoTime()
    var currTime: Long = 0

    while (Option(gameThread).isDefined) do
      currTime = System.nanoTime()
      delta += (currTime - lastTime) / drawInterval
      lastTime = currTime

      if delta >= 1 then
        update()
        repaint()
        delta -= 1
