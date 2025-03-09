package utils

import game.GamePanel
import gui.Image


object Constant:
  val screenWidth = 48 * 20
  val screenHeight = 48 * 12
  val topLeftCoords: (Int, Int) = (10, 10)
  val nextTopLeftCoords: (Int, Int) = (70, 10)
  val downTopLeftCoords: (Int, Int) = (10, 40)
  val topRightCoords: (Int, Int) = (900, 10)
  val startCoords = (screenWidth/2 - Image.start.getWidth/2, screenHeight/2 - Image.start.getHeight/2 + 50 + 40)

  val restartEndStageCoords = (screenWidth/2 - Image.restart.getWidth/2, screenHeight/2 + Image.restart.getHeight + 60)
  val exitPreStageCoords = (850, 50)
  val playPreStageCoords = (800, 470)

  val stage01Coords: (Int, Int) = (280, 360)
  val stage02Coords: (Int, Int) = (350, 270)
  val stage03Coords: (Int, Int) = (450, 220)
  val stage04Coords: (Int, Int) = (570, 250)
  val stage05Coords: (Int, Int) = (680, 360)