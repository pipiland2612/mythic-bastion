package system

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
//    gp.stageManager.startWave()


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
    val positionList: Option[Vector[(Double, Double)]] = gp.stageManager.currentStage match
      case Some(stage) => Some(stage.map.towerPos)
      case _ => None

    positionList.foreach(positions =>
      val pos: Option[(Double, Double)] = positions.filter(position =>
        Math.pow((x - position._1) / (radius), 2) + Math.pow((y - position._2) / (radius/2), 2) <= 1
      ).sortBy(position => Math.pow(x - position._1, 2) + Math.pow(y - position._2, 2)).headOption

      pos match
        case Some(value) =>
          println(s"Build tower at $value")
        case _ =>
    )