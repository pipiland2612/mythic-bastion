package entity.tower

import game.GamePanel
import utils.Tools

import java.awt.Graphics2D
import java.awt.geom.{AffineTransform, Ellipse2D}
import java.awt.image.BufferedImage

case class TowerBuild(gp: GamePanel, pos: (Double, Double), towerBuildImage: BufferedImage):
  private val transform: AffineTransform = AffineTransform()
  private val radius: Int = 30
  private val offsetX: Int = 10
  private val offsetY: Int = 10
  val drawCoords: (Double, Double) = Tools.getCenterCoords(pos, towerBuildImage)
  private val range: Ellipse2D = new Ellipse2D.Double(drawCoords._1 + offsetX, drawCoords._2 + offsetY, radius * 2, radius)
  var hasTower: Boolean = false

  def draw(g2d: Graphics2D): Unit =
    Tools.drawFrame(g2d, towerBuildImage, transform, drawCoords)

  def isInBuildRange(x: Int, y: Int): Boolean =
    range.contains(x.toDouble, y.toDouble)