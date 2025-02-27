package entity.tower

import gui.Image
import utils.Tools

import java.awt.Graphics2D
import java.awt.geom.{AffineTransform, Ellipse2D}
import java.awt.image.BufferedImage

case class TowerBuild(pos: (Double, Double), towerBuildImage: BufferedImage):
  private val transform: AffineTransform = AffineTransform()
  private val radius: Int = 30
  private val drawCoords: (Double, Double) = Tools.getCenterCoords(pos, towerBuildImage)
  private val offsetX: Int = 10
  private val offsetY: Int = 10
  private val range: Ellipse2D = new Ellipse2D.Double(drawCoords._1 + offsetX, drawCoords._2 + offsetY, radius * 2, radius)
  private var currentTower: Option[Tower] = None
  private val upgradeCoords: (Double, Double) =
    ((drawCoords._1) - (Image.frame.getWidth - towerBuildImage.getWidth)/2,
    (drawCoords._2) - (Image.frame.getHeight - towerBuildImage.getHeight)/2)

  var isDrawingFrame: Boolean = false

  def getCurrentTower: Option[Tower] = currentTower
  def setCurrentTower(tower: Tower): Unit = currentTower = Some(tower)

  def draw(g2d: Graphics2D): Unit =
    Tools.drawFrame(g2d, towerBuildImage, transform, drawCoords)

    if isDrawingFrame then
      transform.setToTranslation(upgradeCoords._1, upgradeCoords._2)
      g2d.drawImage(Image.frame, transform, None.orNull)
      currentTower match
        case Some(tower) =>
          g2d.drawImage(Image.upgrade, upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 + 9, upgradeCoords._2.toInt - 20, None.orNull)
          g2d.drawImage(Image.sell, upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 + 16, upgradeCoords._2.toInt + 120, None.orNull)
        case _ =>
          g2d.drawImage(Image.barrack01, upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 - 40, upgradeCoords._2.toInt + 7, None.orNull)
          g2d.drawImage(Image.explo01, upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 + 55, upgradeCoords._2.toInt + 7, None.orNull)
          g2d.drawImage(Image.arrow01, upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 - 40, upgradeCoords._2.toInt + 92, None.orNull)
          g2d.drawImage(Image.magic01, upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 + 55, upgradeCoords._2.toInt + 92, None.orNull)


  def isInBuildRange(x: Int, y: Int): Boolean =
    range.contains(x.toDouble, y.toDouble)