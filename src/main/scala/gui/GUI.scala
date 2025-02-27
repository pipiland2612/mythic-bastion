package gui

import game.{GamePanel, GameState}
import utils.Constant.{downTopLeftCoords, nextTopLeftCoords, topLeftCoords, topRightCoords}
import utils.Tools

import java.awt.{BasicStroke, Color, Graphics2D}
import java.awt.image.BufferedImage

class GUI(gp: GamePanel):

  private var backgroundImage: BufferedImage = _
  private var g2d: Graphics2D = _
  private var isBuilding: Boolean = false

  private def changeBackgroundImage(imgPath: String, scaleX: Int, scaleY: Int): Unit =
    this.backgroundImage = Tools.scaleImage(Tools.loadImage(imgPath), scaleX, scaleY)

  private def setUpGraphics(g: Graphics2D): Unit =
    this.g2d = g

  def reloadGameBackGround(): Unit =
    gp.getCurrentGameState match
      case GameState.PlayState       =>
        gp.getSystemHandler.getStageManager.getCurrentStage.foreach(stage =>
          changeBackgroundImage(s"maps/map${stage.stageID}.jpg", gp.screenWidth, gp.screenHeight)
        )
      case GameState.PauseState      =>
        changeBackgroundImage(s"maps/map.jpg", gp.screenWidth, gp.screenHeight)
      case GameState.TitleState      =>

  def drawUI(g2d: Graphics2D): Unit =
    setUpGraphics(g2d)
    g2d.drawImage(this.backgroundImage, 0, 0, None.orNull)
    gp.getCurrentGameState match
      case GameState.PlayState       => drawPlayState()
      case GameState.PauseState      => drawPauseState()
      case GameState.TitleState      => drawTitleScreen()

  def drawPlayState(): Unit =
    val darkColor = new Color(49, 47, 46, 150)
    g2d.setColor(darkColor)
    g2d.fillRoundRect(topLeftCoords._1 + 15, topLeftCoords._2 + 5, 40, 20, 10, 10)
    g2d.fillRoundRect(nextTopLeftCoords._1 + 20, nextTopLeftCoords._2 + 5, 40, 20, 10, 10)
    g2d.fillRoundRect(downTopLeftCoords._1 + 15, downTopLeftCoords._2 + 5, 100, 20, 10, 10)

    g2d.setColor(Color.WHITE)
    g2d.setStroke(BasicStroke(5))
    gp.getSystemHandler.getStageManager.getCurrentPlayer.foreach(player =>
      g2d.drawString(player.getCoins.toString, nextTopLeftCoords._1 + 30, nextTopLeftCoords._2 + 20)
      g2d.drawString(player.getHealth.toString, topLeftCoords._1 + 25, topLeftCoords._2 + 20)
    )

    gp.getSystemHandler.getStageManager.getCurrentStage.foreach(stage =>
      val currentWave = gp.getSystemHandler.getStageManager.getCurrentWave
      val totalWave = stage.totalWave
      g2d.drawString(s"Wave ${currentWave}/${totalWave}", downTopLeftCoords._1 + 35, downTopLeftCoords._2 + 20)
    )

    g2d.drawImage(Image.heart_img, topLeftCoords._1, topLeftCoords._2, None.orNull)
    g2d.drawImage(Image.coins_img, nextTopLeftCoords._1, nextTopLeftCoords._2, None.orNull)
    g2d.drawImage(Image.skull_img, downTopLeftCoords._1, downTopLeftCoords._2, None.orNull)
    g2d.drawImage(Image.pause_img, topRightCoords._1, topRightCoords._2, None.orNull)

  def drawPauseState(): Unit = {}

  def drawTitleScreen(): Unit = {}