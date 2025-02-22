package system

import entity.tower.{ExploTower, TowerBuild}
import game.{GamePanel, GameState}

import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}

class KeyHandler(gp: GamePanel) extends MouseListener with KeyListener:

  val offSetY = 30

  override def mouseClicked(e: MouseEvent): Unit =
    val (x,y) = (e.getX, e.getY - offSetY)
    System.out.println("Mouse clicked at: " + x + ", " + y)
    handleTowerBuildOnClick(x, y)
    gp.stageManager.startWave()

  override def mousePressed(e: MouseEvent): Unit = {}

  override def mouseReleased(e: MouseEvent): Unit = {}

  override def mouseEntered(e: MouseEvent): Unit = {}

  override def mouseExited(e: MouseEvent): Unit = {}

  override def keyTyped(e: KeyEvent): Unit = {}

  override def keyPressed(e: KeyEvent): Unit =
    e.getKeyCode match
      case KeyEvent.VK_P =>
        println("pressed")
        gp.currentGameState =
          if gp.currentGameState == GameState.PlayState then GameState.PauseState
          else GameState.PlayState
      case _ =>

  override def keyReleased(e: KeyEvent): Unit = {}


  private def handleTowerBuildOnClick(x: Int, y: Int): Unit =
    val radius = 30
    val towerBuildList: Option[Vector[TowerBuild]] = gp.stageManager.currentStage match
      case Some(stage) => Some(stage.map.towerPos)
      case _ => None

    towerBuildList.foreach(towerBuildList =>
      val pos: Option[TowerBuild] = towerBuildList.find(_.isInBuildRange(x, y))

      pos match
        case Some(value) =>
          gp.stageManager.currentStage.foreach(stage =>
            if value.currentTower.isEmpty then
              val tower = ExploTower(gp, 1, value.pos)
              value.currentTower = Some(tower)
          )
        case _ =>
    )