package system

import entity.tower.{Frame, TowerBuild}
import game.{GamePanel, GameState}
import utils.Cache

import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}

class KeyHandler(gp: GamePanel) extends MouseListener with KeyListener:

  private val offSetY = 30
  var isUniting: Boolean = false

  override def mouseClicked(e: MouseEvent): Unit =
    val (x,y) = (e.getX, e.getY - offSetY)
    System.out.println("Mouse clicked at: " + x + ", " + y)
    handleTowerBuildOnClick(x, y)

    gp.getGUI.currentFrame.foreach(frame =>
      if isUniting then
        isUniting = false
        frame.handleUniting(x, y)
        frame.drawingFrame = true
        gp.getGUI.currentFrame = None
      else
        frame.handleFrameOnClick(x,y)
    )

    if x <= 210 && y <= 255 then
      gp.getSystemHandler.getStageManager.startWave()

  override def mousePressed(e: MouseEvent): Unit = {}

  override def mouseReleased(e: MouseEvent): Unit = {}

  override def mouseEntered(e: MouseEvent): Unit = {}

  override def mouseExited(e: MouseEvent): Unit = {}

  override def keyTyped(e: KeyEvent): Unit = {}

  override def keyPressed(e: KeyEvent): Unit =
    e.getKeyCode match
      case KeyEvent.VK_P =>
        val stage =
          if gp.getCurrentGameState == GameState.PlayState then GameState.PauseState
          else GameState.PlayState
        gp.setCurrentGameState(stage)
        gp.reloadGameBackGround()
      case _ =>

  override def keyReleased(e: KeyEvent): Unit = {}

  private def handleTowerBuildOnClick(x: Int, y: Int): Unit =
    val towerBuildList: Option[Vector[TowerBuild]] = gp.getSystemHandler.getStageManager.getCurrentStage.map(_.map.towerPos)
    towerBuildList.foreach(towerBuildList =>
      val pos: Option[TowerBuild] = towerBuildList.find(_.isInBuildRange(x, y))
      pos match
        case Some(towerBuild) =>
          Cache.frameCached.get(towerBuild.pos) match
            case Some(frame) =>
              gp.getGUI.currentFrame = Some(frame)
            case _ =>
              val frame: Frame = Frame(towerBuild.gp, towerBuild)
              Cache.frameCached += towerBuild.pos -> frame
              gp.getGUI.currentFrame = Some(frame)
        case _ =>
    )