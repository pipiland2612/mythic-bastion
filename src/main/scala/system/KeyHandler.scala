package system

import entity.tower.{Frame, TowerBuild}
import game.{GamePanel, GameState}
import gui.Image
import utils.{Cache, Constant, Tools}

import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}
import java.awt.geom.Rectangle2D

class KeyHandler(gp: GamePanel) extends MouseListener with KeyListener:

  private val offSetY = 30
  private val pauseButton: Rectangle2D = Tools.getRectInRange(Constant.topRightCoords, Image.pause)
  private val cancelButton: Rectangle2D = Rectangle2D.Double(680, 165, 40, 40)
  private val startButton: Rectangle2D = Tools.getRectInRange(Constant.startCoords, Image.start)
  private val quitButton: Rectangle2D = Rectangle2D.Double(500, 335, 170, 50)
  private val restartButton: Rectangle2D = Rectangle2D.Double(300, 335, 170, 50)
  var isUniting: Boolean = false

  override def mouseClicked(e: MouseEvent): Unit =
    val (x,y) = (e.getX, e.getY - offSetY)
    System.out.println("Mouse clicked at: " + x + ", " + y)

    gp.getCurrentGameState match
      case GameState.GameMenuState => handleGameMenuState(x, y)
      case GameState.TitleState => handleTitleState(x, y)
      case GameState.PlayState => handlePlayState(x, y)
      case GameState.PauseState => handlePauseState(x, y)

  override def mousePressed(e: MouseEvent): Unit = {}

  override def mouseReleased(e: MouseEvent): Unit = {}

  override def mouseEntered(e: MouseEvent): Unit = {}

  override def mouseExited(e: MouseEvent): Unit = {}

  override def keyTyped(e: KeyEvent): Unit = {}

  override def keyPressed(e: KeyEvent): Unit =
    gp.getCurrentGameState match
    case GameState.PlayState => handlePlayState(e)
    case GameState.PauseState => handlePauseState(e)
    case _ =>
  override def keyReleased(e: KeyEvent): Unit = {}

  private def handleGameMenuState(x: Int, y: Int): Unit =
    if startButton.contains(x, y) then
      gp.handleReloadGameState(GameState.TitleState)


  private val stage01Rec: Rectangle2D = Tools.getRectInRange(Constant.stage01Coords, Image.red_stage)
  private val stage02Rec: Rectangle2D = Tools.getRectInRange(Constant.stage02Coords, Image.red_stage)
  private val stage03Rec: Rectangle2D = Tools.getRectInRange(Constant.stage03Coords, Image.red_stage)
  private val stage04Rec: Rectangle2D = Tools.getRectInRange(Constant.stage04Coords, Image.red_stage)
  private val stage05Rec: Rectangle2D = Tools.getRectInRange(Constant.stage05Coords, Image.red_stage)

  private val map: Map[Rectangle2D, Int] = Map(
    stage01Rec -> 1,
    stage02Rec -> 2,
    stage03Rec -> 3,
    stage04Rec -> 4,
    stage05Rec -> 5
  )

  private def handleTitleState(x: Int, y: Int): Unit =
    map.keys.find(_.contains(x, y)) match
      case Some(button) =>
        map.get(button).foreach(gp.setUpStage(_))
      case None =>

  private def handlePlayState(x: Int, y: Int): Unit =
    if pauseButton.contains(x, y) then
      gp.handleReloadGameState(GameState.PauseState)
      return

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

    if x <= 150 && y <= 150 then
      gp.getSystemHandler.getStageManager.startWave()
  private def handlePauseState(x: Int, y: Int): Unit =
    if cancelButton.contains(x, y) then
      gp.handleReloadGameState(GameState.PlayState)

    if quitButton.contains(x,y) then
      gp.handleReloadGameState(GameState.TitleState)

    if restartButton.contains(x,y) then
      gp.restart()

  private def handleTowerBuildOnClick(x: Int, y: Int): Unit =
    val towerBuildList: Option[Vector[TowerBuild]] = gp.getSystemHandler.getStageManager.getCurrentStage.map(_.getMap.getTowerPos)
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

  private def handlePlayState(e: KeyEvent): Unit =
    e.getKeyCode match
      case KeyEvent.VK_P =>
        gp.handleReloadGameState(GameState.PauseState)
      case _ =>

  private def handlePauseState(e: KeyEvent): Unit =
    e.getKeyCode match
      case KeyEvent.VK_P =>
        gp.handleReloadGameState(GameState.PlayState)
      case _ =>