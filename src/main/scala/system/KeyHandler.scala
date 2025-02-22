package system

import entity.tower.{ExploTower, TowerBuild}
import game.GamePanel

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class KeyHandler(gp: GamePanel) extends MouseListener:

  val offSetY = 30

  @Override
  def mouseClicked(e: MouseEvent): Unit =
    val (x,y) = (e.getX, e.getY - offSetY)
    System.out.println("Mouse clicked at: " + x + ", " + y)
    handleTowerBuildOnClick(x, y)
    gp.stageManager.startWave()


  @Override
  def mousePressed(e: MouseEvent): Unit = {}

  @Override
  def mouseReleased(e: MouseEvent): Unit = {}

  @Override
  def mouseEntered(e: MouseEvent): Unit = {}

  @Override
  def mouseExited(e: MouseEvent): Unit = {}


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
              val tower = ExploTower(gp, 1)
              tower.pos = value.pos
              value.currentTower = Some(tower)
          )
        case _ =>
    )