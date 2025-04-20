package gui

import entity.tower.Frame
import game.{GamePanel, GameState}
import utils.Constant.*
import utils.{Constant, Tools}

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics2D}

/** Manages the graphical user interface for the game, rendering UI elements based on game state.
 * @param gp The GamePanel instance managing the game.
 */
class GUI(gp: GamePanel):
  private var currentPreStagebg: BufferedImage = Tools.scaleImage(Tools.loadImage("maps/prestage_map1.png"), 0.6, 0.6)
  private val upgradeGUI: UpgradeGUI = UpgradeGUI(gp)
  var currentFrame: Option[Frame] = None
  var currentPreStageId: Option[Int] = None

  /** Calculates the centered position for an image with optional offsets.
   * @param image The image to position.
   * @param offsetX The x-offset from the center.
   * @param offsetY The y-offset from the center.
   * @return The calculated (x, y) coordinates.
   */
  private def centeredPosition(image: BufferedImage, offsetX: Int = 0, offsetY: Int = 0): (Int, Int) =
    val (x, y) = Tools.getCenterCoords((gp.screenWidth/2, gp.screenHeight/2), image, offsetX, offsetY)
    (x.toInt, y.toInt)

  /** Resets the current frame to None, clearing any active tower build/upgrade frame. */
  def reset(): Unit = currentFrame = None
  def getupgradeGUI: UpgradeGUI = upgradeGUI

  /** Reloads the pre-stage background image based on the current stage ID. */
  def reloadPreStagebg(): Unit =
    currentPreStageId match
      case Some(id) => currentPreStagebg = Tools.scaleImage(Tools.loadImage(s"maps/prestage_map$id.png"), 0.6, 0.6)
      case None =>

  /** Renders the UI elements based on the current game state.
   * @param g2d The Graphics2D context for rendering.
   */
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

  /** Renders player statistics (health, coins, wave info) and tower frame in play state.
   * @param g2d The Graphics2D context for rendering.
   */
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

  private val font2 = new Font("Arial", Font.BOLD, 25)
  /** Renders the upgrade state UI, including upgrade options and descriptions.
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawUpgradeState(g2d: Graphics2D): Unit =
    val g2dCopy = g2d.create().asInstanceOf[Graphics2D]
    g2dCopy.drawImage(Image.board, 60, 0, None.orNull)
    g2dCopy.drawImage(Image.desc_board, Image.board.getWidth + 50, 0, None.orNull)
    g2dCopy.drawImage(Image.x, Constant.xUpgradeStageCoords._1, Constant.xUpgradeStageCoords._2, None.orNull)
    g2dCopy.drawImage(Image.buy_upgrade, Constant.buyUpgradeStageCoords._1, Constant.buyUpgradeStageCoords._2, None.orNull)

    val currentFrame = upgradeGUI.getCurrentFrame
    val centerX = 820
    g2dCopy.drawImage(currentFrame.getCurrentImage, 790, 115, None.orNull)
    val fm = g2dCopy.getFontMetrics
    var y = 220

    g2dCopy.setColor(Color.RED)
    val nameW = fm.stringWidth(currentFrame.name)
    g2dCopy.drawString(currentFrame.name, centerX - (nameW / 2), 200)

    g2dCopy.setColor(Color.BLACK)
    currentFrame.description.split("\n").foreach(string =>
      val textWidth = fm.stringWidth(string)
      val x = centerX - (textWidth / 2)
      g2dCopy.drawString(string, x, y)
      y += fm.getHeight
    )

    g2dCopy.drawString(currentFrame.cost.toString, 900, 122)
    g2dCopy.setFont(font2)
    g2dCopy.setColor(Color.WHITE)
    g2dCopy.drawString(gp.getPlayer.stars.toString, 820, 52)
    g2dCopy.dispose()
    upgradeGUI.draw(g2d)

  /** Renders the play state UI, including player stats.
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawPlayState(g2d: Graphics2D): Unit =
    if gp.getSystemHandler.getStageManager.getCurrentWave.get == 0 then
      val msg = "Click on the top left\ncorner to start wave"
      var y = 530
      g2d.setFont(Constant.ARIAL_FONT_SMALL)
      g2d.setColor(Color.WHITE)
      msg.split("\n").foreach(string =>
        g2d.drawString(string, 795, y)
        y += 20
      )

    drawPlayerStats(g2d)
    currentFrame.foreach(_.draw(g2d))

  /** Renders a pause banner overlay in the pause state.
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawPauseBanner(g2d: Graphics2D): Unit =
    drawDarkScreen(g2d)
    val (drawX, drawY) = centeredPosition(Image.pause)
    g2d.drawImage(Image.pause, drawX, drawY, None.orNull)

  /** Renders a dark transparent overlay for pause, end, or win states.
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawDarkScreen(g2d: Graphics2D): Unit =
    val darkTransparentColor = new Color(0, 0, 0, 150)
    g2d.setColor(darkTransparentColor)
    g2d.fillRect(0, 0, gp.getWidth, gp.getHeight)

  /** Renders the pause state UI, including player stats and pause banner.
   * @param g2d The Graphics2D context for rendering.
   */
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

  /** Renders the title state UI, displaying stage selection buttons and upgrade option.
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawTitleState(g2d: Graphics2D): Unit =
    for coords <- map do
      g2d.drawImage(Image.red_stage, coords._1, coords._2, None.orNull)
    g2d.drawImage(Image.menu_upgrade, 700, 500, None.orNull)
    g2d.setFont(Constant.ARIAL_FONT_SMALL)
    g2d.setColor(Color.WHITE)
    g2d.drawString("Upgrade", 707, 550)

  /** Renders the end stage state UI, showing a lose message and quit/restart options.
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawEndStageState(g2d: Graphics2D): Unit =
    drawDarkScreen(g2d)
    val (loseX, loseY) = centeredPosition(Image.lose, offsetY = -40)
    g2d.drawImage(Image.lose, loseX, loseY, None.orNull)
    g2d.drawImage(Image.quit, Constant.quitEndStageCoords._1, Constant.quitEndStageCoords._2, None.orNull)
    g2d.drawImage(Image.restart, Constant.restartEndStageCoords._1, Constant.restartEndStageCoords._2, None.orNull)

  /** Renders the win stage state UI, showing a win message and continue/restart options.
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawWinStageState(g2d: Graphics2D): Unit =
    drawDarkScreen(g2d)
    val (winX, winY) = centeredPosition(Image.win, offsetY = -40)
    g2d.drawImage(Image.win, winX, winY, None.orNull)
    g2d.drawImage(Image.continue, Constant.continueEndStageCoords._1, Constant.continueEndStageCoords._2, None.orNull)
    g2d.drawImage(Image.restart, Constant.restartEndStageCoords._1, Constant.restartEndStageCoords._2, None.orNull)

  /** Renders the main menu state UI, showing start and game title images.
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawMenuState(g2d: Graphics2D): Unit =
    g2d.drawImage(Image.start, startCoords._1, startCoords._2, None.orNull)
    g2d.drawImage(Image.mythic_bastion, startCoords._1 - 45, startCoords._2 - 190, None.orNull)

  /** Renders the pre-stage state UI, showing stage preview and play/exit options.
   * @param g2d The Graphics2D context for rendering.
   * @param stageId The ID of the current stage.
   */
  private def drawPreStageState(g2d: Graphics2D, stageId: Int): Unit =
    g2d.drawImage(Image.prestage_bg, 40, 20, None.orNull)
    g2d.drawImage(currentPreStagebg, 200, 110, None.orNull)
    g2d.drawImage(Image.glass, 70, 50, None.orNull)
    g2d.drawImage(Image.exit, Constant.exitPreStageCoords._1, Constant.exitPreStageCoords._2, None.orNull)
    g2d.drawImage(Image.play, Constant.playPreStageCoords._1, Constant.playPreStageCoords._2, None.orNull)

    val (name, desc) = Tools.getStageInfo(stageId)
    g2d.setFont(Constant.HEADER_FONT)
    g2d.drawString(name, 470, 60)
    g2d.setFont(Constant.TEXT_FONT)
    var y = 90
    desc.split("\n").foreach(string =>
      g2d.drawString(string, 470, y)
      y += 30
    )
