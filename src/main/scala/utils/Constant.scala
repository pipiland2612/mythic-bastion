package utils

import game.GamePanel
import gui.Image


object Constant:
  private var gp: GamePanel = _

  def setUp(gp: GamePanel): Unit = this.gp = gp

  val topLeftCoords: (Int, Int) = (10, 10)
  val nextTopLeftCoords: (Int, Int) = (70, 10)
  val downTopLeftCoords: (Int, Int) = (10, 40)
  val topRightCoords: (Int, Int) = (900, 10)
  val startCoords = (48 * 20/2 - Image.start.getWidth/2, 48 * 12/2 - Image.start.getHeight/2 + 50 + 40)


  val stage01Coords: (Int, Int) = (280, 360)
  val stage02Coords: (Int, Int) = (350, 270)
  val stage03Coords: (Int, Int) = (450, 220)
  val stage04Coords: (Int, Int) = (570, 250)
  val stage05Coords: (Int, Int) = (680, 360)