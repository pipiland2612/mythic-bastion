package gui

import utils.Tools

import java.awt.image.BufferedImage

object Image:
  val heart_img: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/heart.png"), 1.7, 1.7)
  val coins_img: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/coins.png"), 1.7, 1.7)
  val cup_img: BufferedImage = Tools.loadImage("gui/cup.png")
  val diamond_img: BufferedImage = Tools.loadImage("gui/diamond.png")
  val pause_img: BufferedImage = Tools.loadImage("gui/pause.png")
  val skull_img: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/skull.png"), 1.7, 1.7)
  val star_img: BufferedImage = Tools.loadImage("gui/star.png")
  val book_img: BufferedImage = Tools.loadImage("gui/book.png")
  val coin_img: BufferedImage = Tools.loadImage("gui/coin.png")

  private val buildTool: BufferedImage = Tools.loadImage("build/buildTool.png")

  val frame: BufferedImage = Tools.scaleImage(buildTool.getSubimage(1, 1, 118, 118), 1.2, 1.2)
  val explo01: BufferedImage = buildTool.getSubimage(251, 75, 46, 44)
  val explo03_1: BufferedImage = buildTool.getSubimage(443, 75, 46, 44)
  val explo03_2: BufferedImage = buildTool.getSubimage(203, 75, 46, 44)
  val buildbarbg: BufferedImage = buildTool.getSubimage(409, 121, 40, 8)
  val buildbartop: BufferedImage = buildTool.getSubimage(473, 57, 37, 5)
  val building1: BufferedImage = buildTool.getSubimage(121, 1, 86, 72)
  val building2: BufferedImage = buildTool.getSubimage(209, 1, 86, 72)
  val building3: BufferedImage = buildTool.getSubimage(297, 1, 86, 72)
  val building4: BufferedImage = buildTool.getSubimage(385, 1, 86, 72)
  val cashbg: BufferedImage = buildTool.getSubimage(473, 35, 30, 20)
  val barrack01: BufferedImage = buildTool.getSubimage(299, 75, 46, 44)
  val barrack03_1: BufferedImage = buildTool.getSubimage(347, 75, 46, 44)
  val barrack03_2: BufferedImage = buildTool.getSubimage(395, 75, 46, 44)
  val empty01: BufferedImage = buildTool.getSubimage(121, 75, 80, 42)
  val arrow01: BufferedImage = buildTool.getSubimage(121, 119, 46, 44)
  val arrow03_1: BufferedImage = buildTool.getSubimage(1, 121, 46, 44)
  val arrow03_2: BufferedImage = buildTool.getSubimage(49, 121, 46, 44)
  val lock: BufferedImage = buildTool.getSubimage(169, 121, 46, 44)
  val magic01: BufferedImage = buildTool.getSubimage(217, 121, 46, 44)
  val magic03_1: BufferedImage = buildTool.getSubimage(265, 121, 46, 44)
  val magic03_2: BufferedImage = buildTool.getSubimage(313, 121, 46, 44)
  val sell: BufferedImage = buildTool.getSubimage(473, 1, 32, 32)
  val upgrade: BufferedImage = buildTool.getSubimage(361, 121, 46, 44)

  val towerBuildImage: BufferedImage = Tools.loadImage("build/Base01.png")
  val towerBuildWidth: Int = towerBuildImage.getWidth
  val towerBuildHeight: Int = towerBuildImage.getHeight

  val unite: BufferedImage = Tools.scaleImage(Tools.loadImage("build/Unite.png"), 0.45, 0.45)
  val pause: BufferedImage = Tools.scaleImage(Tools.loadImage("maps/pause.jpg"), 0.35, 0.35)
  val start: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/start.png"), 0.5, 0.5)

  val red_stage: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/red_stage.png"), 0.5, 0.5)
  val grey_stage: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/grey_stage.png"), 0.5, 0.5)
  val blue_stage: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/blue_stage.png"), 0.5, 0.5)
  val mythic_bastion: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/mythic_bastion.png"), 0.5, 0.5)

  val menu_upgrade: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/upgrade.png"), 0.5, 0.5)
  val lose: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/lose.png"), 0.7, 0.7)
  val win: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/win.png"), 0.7, 0.7)

  val quit: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/quit.jpg"), 0.4, 0.4)
  val restart: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/restart.png"), 0.4, 0.4)
  val continue: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/continue.png"), 0.4, 0.4)

  val glass: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/glass.png"), 0.7, 0.7)
  val prestage_bg: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/prestage_background.png"), 0.8, 0.8)
  val exit: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/exit.png"), 0.4, 0.4)
  val play: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/play.png"), 0.2, 0.2)

  val board: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/board.png"), 2.0, 2.0)
  val desc_board: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/description_board.png"), 0.7, 0.7)

  val arrow: BufferedImage = Tools.scaleImage(Tools.loadImage(s"upgrade/arrow/arrow.png"), 0.75, 0.75)
  val mage: BufferedImage = Tools.scaleImage(Tools.loadImage(s"upgrade/mage/mage.png"), 0.75, 0.75)
  val barrack: BufferedImage = Tools.scaleImage(Tools.loadImage(s"upgrade/barrack/barrack.png"), 0.75, 0.75)
  val explo: BufferedImage = Tools.scaleImage(Tools.loadImage(s"upgrade/explo/explo.png"), 0.75, 0.75)
  val rock: BufferedImage = Tools.scaleImage(Tools.loadImage(s"upgrade/rock/rock.png"), 0.75, 0.75)
  val alliance: BufferedImage = Tools.scaleImage(Tools.loadImage(s"upgrade/alliance/alliance.png"), 0.75, 0.75)

  val starCost: BufferedImage = Tools.scaleImage(Tools.loadImage(s"gui/star_cost.png"), 0.7, 0.6)
  val grey_starCost: BufferedImage = Tools.scaleImage(Tools.loadImage(s"gui/grey_star_cost.png"), 0.7, 0.6)
  val x: BufferedImage = Tools.scaleImage(Tools.loadImage(s"gui/x.png"), 0.5, 0.5)

  val buy_upgrade: BufferedImage = Tools.scaleImage(Tools.loadImage(s"gui/buy_upgrade.png"), 0.3, 0.3)