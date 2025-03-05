package system

import entity.tower.{Frame, TowerBuild}
import game.{GamePanel, GameState}
import gui.Image
import utils.{Cache, Constant}

import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}
import java.awt.geom.Rectangle2D

class KeyHandler(gp: GamePanel) extends MouseListener with KeyListener:

  private val offSetY = 30
  private val pauseButton: Rectangle2D = Rectangle2D.Double(Constant.topRightCoords._1, Constant.topRightCoords._2, Image.pause.getWidth, Image.pause.getHeight)
  private val cancelButton: Rectangle2D = Rectangle2D.Double(680, 165, 40, 40)
  var isUniting: Boolean = false

  override def mouseClicked(e: MouseEvent): Unit =
    val (x,y) = (e.getX, e.getY - offSetY)
    System.out.println("Mouse clicked at: " + x + ", " + y)

    gp.getCurrentGameState match
      case GameState.GameMenuState => handleGameMenuState(x, y)
      case GameState.TitleState => handleTitleState(x, y)
      case GameState.PlayState => handlePlayState(x, y)
      case GameState.PauseState => handlePauseState(x, y)
    handlePauseClick(x, y)

  override def mousePressed(e: MouseEvent): Unit = {}

  override def mouseReleased(e: MouseEvent): Unit = {}

  override def mouseEntered(e: MouseEvent): Unit = {}

  override def mouseExited(e: MouseEvent): Unit = {}

  override def keyTyped(e: KeyEvent): Unit = {}

  override def keyPressed(e: KeyEvent): Unit =
    e.getKeyCode match
      case KeyEvent.VK_P =>
        val state =
          if gp.getCurrentGameState == GameState.PlayState then GameState.PauseState
          else GameState.PlayState
        gp.handleReloadGameState(state)
      case _ =>

  override def keyReleased(e: KeyEvent): Unit = {}


  private def handleGameMenuState(x: Int, y: Int): Unit = {}
  private def handleTitleState(x: Int, y: Int): Unit = {}
  private def handlePlayState(x: Int, y: Int): Unit =
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
  private def handlePauseState(x: Int, y: Int): Unit = {}

  private def handlePauseClick(x: Int, y: Int): Unit =
    if gp.getCurrentGameState == GameState.PlayState && pauseButton.contains(x, y) then
      gp.handleReloadGameState(GameState.PauseState)
    else if gp.getCurrentGameState == GameState.PauseState && cancelButton.contains(x, y) then
      gp.handleReloadGameState(GameState.PlayState)

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