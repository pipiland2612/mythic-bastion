package game

import stage.StageManager
import system.SystemHandler

import java.awt.{Color, Dimension, Graphics, Graphics2D}
import javax.swing.JPanel


class GamePanel extends JPanel with Runnable:

  // Configuration
  val screenWidth = 1280
  val screenHeight = 800
  val FPS = 60

  // System initialize
  val systemHandler: SystemHandler = SystemHandler(this)
  val stageManager: StageManager = StageManager(this)
  var currentGameState: GameState = GameState.PlayState
  var gameThread: Thread = _

  this.setPreferredSize(new Dimension(screenWidth, screenHeight))
  this.setBackground(Color.BLACK)

  def setUpGame(): Unit = {}

  def startGameThread(): Unit =
    gameThread = Thread(this)
    gameThread.start()

  def update(): Unit = {}

  override def paintComponent(g: Graphics): Unit =
    // TODO: Implement this method first to draw stage state
    super.paintComponents(g)
    val g2d = g.asInstanceOf[Graphics2D]

    if currentGameState == GameState.PlayState then
      stageManager.drawCurrentStage(g2d)
    else if currentGameState == GameState.TitleState then
      {}

    g2d.dispose()

  override def run(): Unit =
    val drawInterval: Double = 1e9 / FPS
    var delta: Double = 0
    var lastTime: Long = System.nanoTime()
    var currTime: Long = 0

    while (gameThread != null) do
      currTime = System.nanoTime()
      delta += (currTime - lastTime) / drawInterval
      lastTime = currTime

      if delta >= 1 then
        update()
        repaint()
        delta -= 1
