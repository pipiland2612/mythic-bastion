package gui

import entity.tower.Frame
import game.{GamePanel, GameState}
import utils.{Constant, Tools}
import utils.Constant.{downTopLeftCoords, nextTopLeftCoords, startCoords, topLeftCoords, topRightCoords}

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics2D}

class GUI(gp: GamePanel):
  private var currentPreStagebg: BufferedImage = Tools.scaleImage(Tools.loadImage("maps/prestage_map1.png"), 0.6, 0.6)
  var currentFrame: Option[Frame] = None
  var currentPreStageId: Option[Int] = None

  private def centeredPosition(image: BufferedImage, offsetX: Int = 0, offsetY: Int = 0): (Int, Int) =
    val (x, y) = Tools.getCenterCoords((gp.screenWidth/2, gp.screenHeight/2), image, offsetX, offsetY)
    (x.toInt, y.toInt)

  def reset(): Unit = currentFrame = None

  def reloadPreStagebg(): Unit =
    currentPreStageId match
      case Some(id) => currentPreStagebg = Tools.scaleImage(Tools.loadImage(s"maps/prestage_map$id.png"), 0.6, 0.6)
      case None =>

  def drawUI(g2d: Graphics2D): Unit =
    gp.getCurrentGameState match
      case GameState.PlayState       => drawPlayState(g2d)
      case GameState.PauseState      => drawPauseState(g2d)
      case GameState.TitleState      => drawTitleState(g2d)
      case GameState.GameMenuState   => drawMenuState(g2d)
      case GameState.UpgradeState    => drawUpgradeState(g2d)
      case GameState.EndStageState   => drawEndStageState(g2d)
      case GameState.WinStageState   => drawWinStageState(g2d)
      case GameState.PreStageState   =>
        currentPreStageId match
          case Some(id) => drawPreStageState(g2d, id)
          case None =>

  private def drawPlayerStats(g2d: Graphics2D): Unit =
    val darkColor = new Color(49, 47, 46, 150)
    g2d.setColor(darkColor)
    g2d.fillRoundRect(topLeftCoords._1 + 15, topLeftCoords._2 + 5, 40, 20, 10, 10)
    g2d.fillRoundRect(nextTopLeftCoords._1 + 20, nextTopLeftCoords._2 + 5, 50, 20, 10, 10)
    g2d.fillRoundRect(downTopLeftCoords._1 + 15, downTopLeftCoords._2 + 5, 100, 20, 10, 10)

    g2d.setColor(Color.WHITE)
    gp.getSystemHandler.getStageManager.getCurrentStage.foreach(stage =>
      val player = stage.getCurrentPlayer
      g2d.drawString(player.getCoins.toString, nextTopLeftCoords._1 + 30, nextTopLeftCoords._2 + 20)
      g2d.drawString(player.getHealth.toString, topLeftCoords._1 + 25, topLeftCoords._2 + 20)
    )

    gp.getSystemHandler.getStageManager.getCurrentStage.foreach(stage =>
      val currentWave = stage.getWaveSpawner.getCurrentWave
      val totalWave = stage.totalWave
      g2d.drawString(s"Wave $currentWave/$totalWave", downTopLeftCoords._1 + 35, downTopLeftCoords._2 + 20)
    )

    g2d.drawImage(Image.heart_img, topLeftCoords._1, topLeftCoords._2, None.orNull)
    g2d.drawImage(Image.coins_img, nextTopLeftCoords._1, nextTopLeftCoords._2, None.orNull)
    g2d.drawImage(Image.skull_img, downTopLeftCoords._1, downTopLeftCoords._2, None.orNull)
    g2d.drawImage(Image.pause_img, topRightCoords._1, topRightCoords._2, None.orNull)

    currentFrame.foreach(_.draw(g2d))

  private def drawUpgradeState(g2d: Graphics2D): Unit = {}

  private def drawPlayState(g2d: Graphics2D): Unit =
    drawPlayerStats(g2d)

  private def drawPauseBanner(g2d: Graphics2D): Unit =
    drawDarkScreen(g2d)
    val (drawX, drawY) = centeredPosition(Image.pause)
    g2d.drawImage(Image.pause, drawX, drawY, None.orNull)

  private def drawDarkScreen(g2d: Graphics2D): Unit =
    val darkTransparentColor = new Color(0, 0, 0, 150)
    g2d.setColor(darkTransparentColor)
    g2d.fillRect(0, 0, gp.getWidth, gp.getHeight)

  private def drawPauseState(g2d: Graphics2D): Unit =
    drawPlayerStats(g2d)
    drawPauseBanner(g2d)

  private val map: Vector[(Int, Int)] = Vector(
    Constant.stage01Coords,
    Constant.stage02Coords,
    Constant.stage03Coords,
    Constant.stage04Coords,
    Constant.stage05Coords
  )

  private def drawTitleState(g2d: Graphics2D): Unit =
    for coords <- map do
      g2d.drawImage(Image.red_stage, coords._1, coords._2, None.orNull)
    g2d.drawImage(Image.menu_upgrade, 700, 500, None.orNull)

  private def drawEndStageState(g2d: Graphics2D): Unit =
    drawDarkScreen(g2d)
    val (loseX, loseY) = centeredPosition(Image.lose, offsetY = -40)
    g2d.drawImage(Image.lose, loseX, loseY, None.orNull)
    val (quitX, quitY) = centeredPosition(Image.quit, offsetY = Image.quit.getHeight / 2 + 10)
    g2d.drawImage(Image.quit, quitX, quitY, None.orNull)
    g2d.drawImage(Image.restart, Constant.restartEndStageCoords._1, Constant.restartEndStageCoords._2, None.orNull)

  private def drawWinStageState(g2d: Graphics2D): Unit =
    drawDarkScreen(g2d)
    val (winX, winY) = centeredPosition(Image.win, offsetY = -40)
    g2d.drawImage(Image.win, winX, winY, None.orNull)
    val (continueX, continueY) = centeredPosition(Image.continue, offsetY = Image.continue.getHeight / 2)
    g2d.drawImage(Image.continue, continueX, continueY, None.orNull)
    g2d.drawImage(Image.restart, Constant.restartEndStageCoords._1, Constant.restartEndStageCoords._2, None.orNull)

  private val font = new Font("Arial", Font.BOLD, 40)
  private def drawMenuState(g2d: Graphics2D): Unit =
    g2d.drawImage(Image.start, startCoords._1, startCoords._2, None.orNull)
    g2d.drawImage(Image.mythic_bastion, startCoords._1 - 45, startCoords._2 - 190, None.orNull)

  private def drawPreStageState(g2d: Graphics2D, stageId: Int): Unit =
    g2d.drawImage(Image.prestage_bg, 40, 20, None.orNull)
    g2d.drawImage(currentPreStagebg, 200, 110, None.orNull)
    g2d.drawImage(Image.glass, 70, 50, None.orNull)
    g2d.drawImage(Image.exit, Constant.exitPreStageCoords._1, Constant.exitPreStageCoords._2, None.orNull)
    g2d.drawImage(Image.play, Constant.playPreStageCoords._1, Constant.playPreStageCoords._2, None.orNull)