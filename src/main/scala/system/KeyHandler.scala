package system

import entity.tower.{ExploTower, Tower, TowerBuild}
import game.{GamePanel, GameState}

import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}

class KeyHandler(gp: GamePanel) extends MouseListener with KeyListener:

  private val offSetY = 30

  override def mouseClicked(e: MouseEvent): Unit =
    val (x,y) = (e.getX, e.getY - offSetY)
    System.out.println("Mouse clicked at: " + x + ", " + y)
    handleTowerBuildOnClick(x, y)
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
        gp.getGUI.reloadGameBackGround()
      case _ =>

  override def keyReleased(e: KeyEvent): Unit = {}

  private def handleTowerBuildOnClick(x: Int, y: Int): Unit =
    val towerBuildList: Option[Vector[TowerBuild]] = gp.getSystemHandler.getStageManager.getCurrentStage.map(_.map.towerPos)

    towerBuildList.foreach(towerBuildList =>
      val pos: Option[TowerBuild] = towerBuildList.find(_.isInBuildRange(x, y))

      pos match
        case Some(value) =>
          value.isDrawingFrame = if value.isDrawingFrame then false else true
          if value.isDrawingFrame then
            gp.getSystemHandler.getStageManager.getCurrentStage.foreach(stage =>
              if value.getCurrentTower.isEmpty then
                val clickedButton = value.buttons.keys.find(_.contains(x, y)) match
                  case Some(button) =>
                    value.buttons.get(button) match
                      case Some(tower) => value.setCurrentTower(tower)
                      case _ =>
                  case _ =>

//            if value.getCurrentTower.isEmpty then {}
////              val tower = ExploTower(gp, 1, value.pos)
////              value.setCurrentTower(tower)
//            else
//              value.getCurrentTower.foreach(stage =>
//                stage.isShowingRange = if stage.isShowingRange then false
//                else true
//              )
          )
        case _ =>
    )