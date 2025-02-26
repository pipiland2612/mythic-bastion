package system

import game.GamePanel
import system.stage.{Stage, StageManager}
import utils.Tools

import java.awt.Graphics2D

class SystemHandler (gp: GamePanel):
  private val keyHandler: KeyHandler = KeyHandler(gp)
  private val grid: Grid = Grid(gp)
  private val stageManager: StageManager = StageManager(gp)

  def getKeyHandler: KeyHandler = keyHandler
  def getGrid: Grid = grid
  def getStageManager: StageManager = stageManager

  def setUp(): Unit = 
    val stage: Stage = Tools.loadStage("stages/Stage01.json")
    stageManager.setStage(stage)

  def update(): Unit =
    stageManager.update()

  def draw(g2d: Graphics2D) =
    stageManager.draw(g2d)