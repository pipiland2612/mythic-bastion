package entity.tower

import utils.Tools

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

case class TowerBuild(pos: (Double, Double), towerBuildImage: BufferedImage):
  private val transform: AffineTransform = AffineTransform()
  var currentTower: Option[Tower] = None

  def draw(g2d: Graphics2D): Unit =
    val (x,y) = Tools.getCenterCoords(pos._1, pos._2, towerBuildImage)
    transform.setToTranslation(x, y)
    g2d.drawImage(towerBuildImage, transform, None.orNull)