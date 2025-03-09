package gui

import entity.tower.Frame
import game.{GamePanel, GameState}
import utils.{Constant, Tools}
import utils.Constant.{downTopLeftCoords, nextTopLeftCoords, startCoords, topLeftCoords, topRightCoords}

import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Color, Font, Graphics2D}

class GUI(gp: GamePanel):
  private var g2d: Graphics2D = _
  private var isBuilding: Boolean = false
  private var currentPreStagebg: BufferedImage = Tools.scaleImage(Tools.loadImage(s"maps/prestage_map1.png"), 0.5, 0.5)
  var currentFrame: Option[Frame] = None
  var currentPreStageId: Option[Int] = None

  private def setUpGraphics(g: Graphics2D): Unit =
    this.g2d = g

  def reset(): Unit = currentFrame = None
  def reloadPreStagebg(): Unit =
    currentPreStageId match
      case Some(id) => currentPreStagebg = Tools.scaleImage(Tools.loadImage(s"maps/prestage_map$id.png"), 0.5, 0.5)
      case None =>

  def drawUI(g2d: Graphics2D): Unit =
    setUpGraphics(g2d)
    gp.getCurrentGameState match
      case GameState.PlayState       => drawPlayState()
      case GameState.PauseState      => drawPauseState()
      case GameState.TitleState      => drawTitleState()
      case GameState.GameMenuState   => drawMenuState()
      case GameState.UpgradeState    => drawUpgradeState()
      case GameState.EndStageState   => drawEndStageState()
      case GameState.WinStageState   => drawWinStageState()
      case GameState.PreStageState   =>
        currentPreStageId match
          case Some(id) => drawPreStageState(id)
          case None =>

  private def drawPlayerStats(): Unit =
    val darkColor = new Color(49, 47, 46, 150)
    g2d.setColor(darkColor)
    g2d.fillRoundRect(topLeftCoords._1 + 15, topLeftCoords._2 + 5, 40, 20, 10, 10)
    g2d.fillRoundRect(nextTopLeftCoords._1 + 20, nextTopLeftCoords._2 + 5, 50, 20, 10, 10)
    g2d.fillRoundRect(downTopLeftCoords._1 + 15, downTopLeftCoords._2 + 5, 100, 20, 10, 10)

    g2d.setColor(Color.WHITE)
    g2d.setStroke(BasicStroke(5))
    gp.getSystemHandler.getStageManager.getCurrentStage.foreach(stage =>
      val player = stage.getCurrentPlayer
      g2d.drawString(player.getCoins.toString, nextTopLeftCoords._1 + 30, nextTopLeftCoords._2 + 20)
      g2d.drawString(player.getHealth.toString, topLeftCoords._1 + 25, topLeftCoords._2 + 20)
    )

    gp.getSystemHandler.getStageManager.getCurrentStage.foreach(stage =>
      val currentWave = stage.getWaveSpawner.getCurrentWave
      val totalWave = stage.totalWave
      g2d.drawString(s"Wave ${currentWave}/${totalWave}", downTopLeftCoords._1 + 35, downTopLeftCoords._2 + 20)
    )

    g2d.drawImage(Image.heart_img, topLeftCoords._1, topLeftCoords._2, None.orNull)
    g2d.drawImage(Image.coins_img, nextTopLeftCoords._1, nextTopLeftCoords._2, None.orNull)
    g2d.drawImage(Image.skull_img, downTopLeftCoords._1, downTopLeftCoords._2, None.orNull)
    g2d.drawImage(Image.pause_img, topRightCoords._1, topRightCoords._2, None.orNull)

    currentFrame.foreach(_.draw(g2d))

  private def drawUpgradeState(): Unit = {}

  private def drawPlayState(): Unit =
    drawPlayerStats()

  private def drawPauseBanner(): Unit =
    drawDarkScreen()
    val (drawX, drawY) = (gp.getWidth / 2 - Image.pause.getWidth / 2, gp.getHeight / 2 - Image.pause.getHeight / 2)
    g2d.drawImage(Image.pause, drawX, drawY, None.orNull)

  private def drawDarkScreen(): Unit =
    val darkTransparentColor = new Color(0, 0, 0, 150)
    g2d.setColor(darkTransparentColor)
    g2d.fillRect(0, 0, gp.getWidth, gp.getHeight)

  private def drawPauseState(): Unit =
    drawPlayerStats()
    drawPauseBanner()

  private val map: Vector[(Int, Int)] = Vector(
    Constant.stage01Coords,
    Constant.stage02Coords,
    Constant.stage03Coords,
    Constant.stage04Coords,
    Constant.stage05Coords
  )

  private def drawTitleState(): Unit =
    for coords <- map do
      g2d.drawImage(Image.red_stage, coords._1, coords._2, None.orNull)

    g2d.drawImage(Image.menu_upgrade, 700, 500, None.orNull)

  private def drawEndStageState(): Unit =
    drawDarkScreen()
    g2d.drawImage(Image.lose, gp.screenWidth/2 - Image.lose.getWidth/2, gp.screenHeight/2 - Image.lose.getHeight/2 - 40, None.orNull)
    g2d.drawImage(Image.quit, gp.screenWidth/2 - Image.quit.getWidth/2, gp.screenHeight/2 + Image.quit.getHeight/2 + 10, None.orNull)
    g2d.drawImage(Image.restart, Constant.restartEndStageCoords._1, Constant.restartEndStageCoords._2, None.orNull)

  private def drawWinStageState(): Unit =
    drawDarkScreen()
    g2d.drawImage(Image.win, gp.screenWidth/2 - Image.win.getWidth/2, gp.screenHeight/2 - Image.win.getHeight/2 - 40, None.orNull)
    g2d.drawImage(Image.continue, gp.screenWidth/2 - Image.continue.getWidth/2, gp.screenHeight/2 + Image.continue.getHeight/2, None.orNull)
    g2d.drawImage(Image.restart, Constant.restartEndStageCoords._1, Constant.restartEndStageCoords._2, None.orNull)

  private val font = Font("Arial", Font.BOLD, 40)
  private def drawMenuState(): Unit =
    g2d.drawImage(Image.start, startCoords._1, startCoords._2, None.orNull)
    g2d.drawImage(Image.mythic_bastion, startCoords._1 - 45, startCoords._2 - 190, None.orNull)

  def drawPreStageState(stageId: Int): Unit =
    g2d.drawImage(Image.prestage_bg, 40, 20, None.orNull)
    g2d.drawImage(currentPreStagebg, 180, 80, None.orNull)
    g2d.drawImage(Image.glass, 100, 50, None.orNull)
    g2d.drawImage(Image.exit, 900, 50, None.orNull)