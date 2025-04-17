package utils

import gui.Image

import java.awt.Font


object Constant:
  val screenWidth: Int = 48 * 20
  val screenHeight: Int = 48 * 12
  val topLeftCoords: (Int, Int) = (10, 10)
  val nextTopLeftCoords: (Int, Int) = (70, 10)
  val downTopLeftCoords: (Int, Int) = (10, 40)
  val topRightCoords: (Int, Int) = (900, 10)
  val startCoords: (Int, Int) = (screenWidth/2 - Image.start.getWidth/2, screenHeight/2 - Image.start.getHeight/2 + 50 + 40)

  val restartEndStageCoords: (Int, Int) = (screenWidth/2 - Image.restart.getWidth/2, screenHeight/2 + Image.restart.getHeight + 60)
  val exitPreStageCoords: (Int, Int) = (850, 50)
  val playPreStageCoords: (Int, Int) = (800, 470)
  val xUpgradeStageCoords: (Int, Int) = (20, 20)
  val buyUpgradeStageCoords: (Int, Int) = (730, 400)
  val quitEndStageCoords: (Int, Int) = (screenWidth / 2 - Image.quit.getWidth / 2, screenHeight / 2 + Image.quit.getHeight / 2)
  val continueEndStageCoords: (Int, Int) = (screenWidth / 2 - Image.continue.getWidth / 2, screenHeight / 2)

  val stage01Coords: (Int, Int) = (280, 360)
  val stage02Coords: (Int, Int) = (350, 270)
  val stage03Coords: (Int, Int) = (450, 220)
  val stage04Coords: (Int, Int) = (570, 250)
  val stage05Coords: (Int, Int) = (680, 360)

  val TEXT_FONT: Font = Tools.loadCustomFont("header.ttf", 15).deriveFont(Font.BOLD)
  val HEADER_FONT: Font = Tools.loadCustomFont("text.ttf", 18)
  val ARIAL_FONT_SMALL: Font = Tools.loadCustomFont("arial.ttf", 15)