package system

import game.GamePanel
import system.stage.{Grid, Stage, StageManager}
import utils.Tools

import java.awt.Graphics2D

class SystemHandler (gp: GamePanel):
  private val keyHandler: KeyHandler = KeyHandler(gp)
  private val stageManager: StageManager = StageManager(gp)

  def getKeyHandler: KeyHandler = keyHandler
  def getStageManager: StageManager = stageManager

  def setUp(int: Int): Unit =
    require(int >= 1 && int <= 5)
    val stage: Stage = Tools.loadStage(s"stages/Stage0${int}.json")
    stageManager.setStage(stage)

  def restart(): Unit = 
    stageManager.restart()

  def update(): Unit =
    stageManager.update()

  def draw(g2d: Graphics2D) =
    stageManager.draw(g2d)