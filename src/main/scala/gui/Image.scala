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
  val towerBuildWidth = towerBuildImage.getWidth
  val towerBuildHeight = towerBuildImage.getHeight

  val unite: BufferedImage = Tools.scaleImage(Tools.loadImage("build/Unite.png"), 0.45, 0.45)
  val pause: BufferedImage = Tools.scaleImage(Tools.loadImage("maps/pause.jpg"), 0.35, 0.35)
  val start: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/start.png"), 0.5, 0.5)

  val red_stage: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/red_stage.png"), 0.5, 0.5)
  val grey_stage: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/grey_stage.png"), 0.5, 0.5)
  val blue_stage: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/blue_stage.png"), 0.5, 0.5)
  val mythic_bastion: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/mythic_bastion.png"), 0.5, 0.5)

  val menu_upgrade: BufferedImage = Tools.scaleImage(Tools.loadImage("gui/upgrade.png"), 0.5, 0.5)