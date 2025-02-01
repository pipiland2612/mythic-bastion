package system

import game.GamePanel

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class KeyHandler(gp: GamePanel) extends MouseListener:

  @Override
  def mouseClicked(e: MouseEvent): Unit =
    System.out.println("Mouse clicked at: " + e.getX + ", " + e.getY)

  @Override
  def mousePressed(e: MouseEvent): Unit = {}

  @Override
  def mouseReleased(e: MouseEvent): Unit = {}

  @Override
  def mouseEntered(e: MouseEvent): Unit = {}

  @Override
  def mouseExited(e: MouseEvent): Unit = {}
