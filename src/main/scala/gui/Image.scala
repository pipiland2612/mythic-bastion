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
