package entity.tower

import game.GamePanel
import gui.Image

import java.awt.geom.AffineTransform
import java.awt.{Graphics2D, Rectangle}

class Frame(gp: GamePanel, towerBuild: TowerBuild):
  private val upgradeCoords: (Double, Double) =
    ((towerBuild.drawCoords._1) - (Image.frame.getWidth - towerBuild.towerBuildImage.getWidth)/2,
    (towerBuild.drawCoords._2) - (Image.frame.getHeight - towerBuild.towerBuildImage.getHeight)/2)
  private val transform: AffineTransform = AffineTransform()

  var isDrawingFrame: Boolean = true
  val pos = towerBuild.pos
  private val barrackCoords: (Int, Int) = (upgradeCoords._1.toInt + towerBuild.towerBuildImage.getWidth / 2 - 40, upgradeCoords._2.toInt + 7)
  private val exploCoords: (Int, Int) =  (upgradeCoords._1.toInt + towerBuild.towerBuildImage.getWidth / 2 + 55, upgradeCoords._2.toInt + 7)
  private val arrowCoords: (Int, Int) = (upgradeCoords._1.toInt + towerBuild.towerBuildImage.getWidth / 2 - 40, upgradeCoords._2.toInt + 92)
  private val magicCoords: (Int, Int) = (upgradeCoords._1.toInt + towerBuild.towerBuildImage.getWidth / 2 + 55, upgradeCoords._2.toInt + 92)
  private val levelUpCoords: (Int, Int) = (upgradeCoords._1.toInt + towerBuild.towerBuildImage.getWidth / 2 + 9, upgradeCoords._2.toInt - 20)
  private val sellCoords: (Int, Int) = (upgradeCoords._1.toInt + towerBuild.towerBuildImage.getWidth / 2 + 16, upgradeCoords._2.toInt + 120)
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

  private val sellButton: Rectangle = Rectangle(
    sellCoords._1, sellCoords._2,
    Image.sell.getWidth, Image.sell.getHeight
  )

  private val levelUpButton: Rectangle = Rectangle(
    levelUpCoords._1, levelUpCoords._2,
    Image.upgrade.getWidth, Image.upgrade.getHeight
  )

  val buttons: Map[Rectangle, Tower] = Map(
    barrackButton -> BarrackTower(gp, 1, pos),
    exploButton -> ExploTower(gp, 1, pos),
    arrowButton -> ArrowTower(gp, 1, pos),
    magicButton -> MagicTower(gp, 1, pos)
  )

  val levelUpButtons = Map(
    sellButton -> "Sell",
    levelUpButton -> "Level up",
  )

  def handleFrameOnClick(x: Int, y: Int): Unit =
    if (towerBuild.getCurrentTower.isEmpty) then
      handleTowerSelection(x, y)
    else
      handleLevelUpSelection(x, y)

  private def handleTowerSelection(x: Int, y: Int): Unit =
    buttons.keys.find(_.contains(x, y)) match
      case Some(button) =>
        buttons.get(button).foreach(tower =>
          towerBuild.setCurrentTower(tower)
        )
      case None =>
        setOffFrame(x, y)

  private def handleLevelUpSelection(x: Int, y: Int): Unit =
    levelUpButtons.keys.find(_.contains(x, y)) match
      case Some(button) =>
        levelUpButtons.get(button) match
          case string => println(string)
      case None =>
        setOffFrame(x, y)

  private def setOffFrame(x: Int, y: Int): Unit =
    if (!towerBuild.isInBuildRange(x, y)) then
      gp.getGUI.currentFrame = None

  def draw(g2d: Graphics2D): Unit =
    if isDrawingFrame then
      transform.setToTranslation(upgradeCoords._1, upgradeCoords._2)
      g2d.drawImage(Image.frame, transform, None.orNull)
      towerBuild.getCurrentTower match
        case Some(tower) =>
          g2d.drawImage(Image.upgrade, levelUpCoords._1, levelUpCoords._2, None.orNull)
          g2d.drawImage(Image.sell, sellCoords._1, sellCoords._2, None.orNull)
        case _ =>
          g2d.drawImage(Image.barrack01, barrackCoords._1, barrackCoords._2, None.orNull)
          g2d.drawImage(Image.explo01, exploCoords._1, exploCoords._2, None.orNull)
          g2d.drawImage(Image.arrow01, arrowCoords._1, arrowCoords._2, None.orNull)
          g2d.drawImage(Image.magic01, magicCoords._1, magicCoords._2, None.orNull)