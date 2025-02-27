package entity.tower

import game.GamePanel
import gui.Image
import scalafx.geometry.Rectangle2D
import utils.Tools

import java.awt.{Graphics2D, Rectangle}
import java.awt.geom.{AffineTransform, Ellipse2D}
import java.awt.image.BufferedImage

case class TowerBuild(gp: GamePanel, pos: (Double, Double), towerBuildImage: BufferedImage):
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
  private val barrackCoords: (Int, Int) = (upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 - 40, upgradeCoords._2.toInt + 7)
  private val exploCoords: (Int, Int) =  (upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 + 55, upgradeCoords._2.toInt + 7)
  private val arrowCoords: (Int, Int) = (upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 - 40, upgradeCoords._2.toInt + 92)
  private val magicCoords: (Int, Int) = (upgradeCoords._1.toInt + towerBuildImage.getWidth / 2 + 55, upgradeCoords._2.toInt + 92)
  private val barrackButton: Rectangle = Rectangle(
    barrackCoords._1, barrackCoords._2,
    Image.barrack01.getWidth, Image.barrack01.getHeight
  )

  private val exploButton: Rectangle = Rectangle(
    exploCoords._1, exploCoords._2,
    Image.explo01.getWidth, Image.explo01.getHeight
  )

  private val arrowButton: Rectangle = Rectangle(
    arrowCoords._1, arrowCoords._2,
    Image.arrow01.getWidth, Image.arrow01.getHeight
  )

  private val magicButton: Rectangle = Rectangle(
    magicCoords._1, magicCoords._2,
    Image.magic01.getWidth, Image.magic01.getHeight
  )
  val buttons: Map[Rectangle, Tower] = Map(
//    barrackButton -> "Barracks selected! Train your soldiers here.",
    exploButton -> ExploTower(gp, 1, pos),
    arrowButton -> ArrowTower(gp, 1, pos),
    magicButton -> MagicTower(gp, 1, pos)
  )

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
          g2d.drawImage(Image.barrack01, barrackCoords._1, barrackCoords._2, None.orNull)
          g2d.drawImage(Image.explo01, exploCoords._1, exploCoords._2, None.orNull)
          g2d.drawImage(Image.arrow01, arrowCoords._1, arrowCoords._2, None.orNull)
          g2d.drawImage(Image.magic01, magicCoords._1, magicCoords._2, None.orNull)


  def isInBuildRange(x: Int, y: Int): Boolean =
    range.contains(x.toDouble, y.toDouble)