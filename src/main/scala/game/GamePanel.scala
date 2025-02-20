package game

import entity.creature.enemy.Enemy
import stage.{Stage, StageManager}
import system.SystemHandler
import utils.{Cache, Tools}

import java.awt.{Color, Dimension, Font, Graphics, Graphics2D}
import javax.swing.JPanel


class GamePanel extends JPanel with Runnable:

  // Configuration
  val scale: Int = 3
  val tileSize: Int = 16 * scale
  val maxScreenColumn: Int = 20
  val maxScreenRow: Int = 12
  val screenWidth: Int = tileSize * maxScreenColumn
  val screenHeight: Int = tileSize * maxScreenRow
  val FPS: Int = 90

  // System initialize
  val systemHandler: SystemHandler = SystemHandler(this)
  val stageManager: StageManager = StageManager(this)
  var currentGameState: GameState = GameState.PlayState
  var gameThread: Thread = _

  this.setPreferredSize(new Dimension(screenWidth, screenHeight))
  this.setBackground(Color.BLACK)

  def setUpGame(): Unit =
    val stage: Stage = Tools.loadStage("stages/Stage01.json")
    Enemy.gp = this
    stageManager.setStage(stage)

  def startGameThread(): Unit =
    gameThread = Thread(this)
    gameThread.start()

  def update(): Unit =
    stageManager.update()

  override def paintComponent(g: Graphics): Unit =
    super.paintComponents(g)
    val g2d = g.asInstanceOf[Graphics2D]

    val startTime: Long = System.nanoTime()

    if currentGameState == GameState.PlayState then
      stageManager.draw(g2d)
    else if currentGameState == GameState.TitleState then
      {}

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

    while (gameThread != null) do
      currTime = System.nanoTime()
      delta += (currTime - lastTime) / drawInterval
      lastTime = currTime

      if delta >= 1 then
        update()
        repaint()
        delta -= 1
