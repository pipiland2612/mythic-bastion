package entity.tower

import game.GamePanel
import gui.Image

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D, RadialGradientPaint, Rectangle}

object Frame :
  case class TowerButtonConfig(
    coords: (Int, Int),
    image: BufferedImage,
    createTower: (GamePanel, Int, (Double, Double)) => Tower
  )

  case class OptionButtonConfig(
    coords: (Int, Int),
    image: BufferedImage,
    action: String
  )

  // Constants for gradient
  private val DIST = Array(0.0f, 0.3f, 0.6f, 0.8f, 1.0f)
  private val COLORS = Array(
    Color(12, 227, 12, 0),
    Color(12, 227, 12, 25),
    Color(12, 227, 12, 75),
    Color(12, 227, 12, 200),
    Color(12, 227, 12, 255)
  )
end Frame

class Frame(gp: GamePanel, towerBuild: TowerBuild) :
  import Frame._

  private val upgradeCoords: (Double, Double) = calculateUpgradeCoords()
  private val transform: AffineTransform = new AffineTransform()
  var isDrawingFrame: Boolean = true
  val pos: (Double, Double) = towerBuild.pos

  private val towerButtons: Map[Rectangle, Tower] = initTowerButtons()
  private val optionButtons: Map[Rectangle, String] = initOptionButtons()

  private def calculateUpgradeCoords(): (Double, Double) =
    val xOffset = (Image.frame.getWidth - towerBuild.towerBuildImage.getWidth) / 2
    val yOffset = (Image.frame.getHeight - towerBuild.towerBuildImage.getHeight) / 2
    (towerBuild.drawCoords._1 - xOffset, towerBuild.drawCoords._2 - yOffset)

  private def initTowerButtons(): Map[Rectangle, Tower] =
    val centerX = upgradeCoords._1.toInt + towerBuild.towerBuildImage.getWidth / 2
    val topY = upgradeCoords._2.toInt + 7
    val bottomY = upgradeCoords._2.toInt + 92

    val configs = List(
      TowerButtonConfig((centerX - 40, topY), Image.barrack01, (gp, level, pos) => BarrackTower(gp, level, pos)),
      TowerButtonConfig((centerX + 55, topY), Image.explo01, (gp, level, pos) => ExploTower(gp, level, pos)),
      TowerButtonConfig((centerX - 40, bottomY), Image.arrow01, (gp, level, pos) => ArrowTower(gp, level, pos)),
      TowerButtonConfig((centerX + 55, bottomY), Image.magic01, (gp, level, pos) => MagicTower(gp, level, pos))
    )

    configs.map(config =>
      val rect = Rectangle(config.coords._1, config.coords._2, config.image.getWidth, config.image.getHeight)
      rect -> config.createTower(gp, 1, pos)
    ).toMap

  private def initOptionButtons(): Map[Rectangle, String] =
    val centerX = upgradeCoords._1.toInt + towerBuild.towerBuildImage.getWidth / 2
    Map(
      Rectangle(centerX + 9, upgradeCoords._2.toInt - 20, Image.upgrade.getWidth, Image.upgrade.getHeight) -> "L",
      Rectangle(centerX + 16, upgradeCoords._2.toInt + 120, Image.sell.getWidth, Image.sell.getHeight) -> "S",
      Rectangle(centerX + 20, upgradeCoords._2.toInt + 100, Image.unite.getWidth, Image.unite.getHeight) -> "U"
    )

  def handleFrameOnClick(x: Int, y: Int): Unit =
    towerBuild.getCurrentTower match
      case None => handleTowerSelection(x, y)
      case Some(_) => handleLevelUpSelection(x, y)

  private def handleTowerSelection(x: Int, y: Int): Unit =
    towerButtons.keys.find(_.contains(x, y)) match
      case Some(button) =>
        towerButtons.get(button) match
          case Some(tower) =>
            towerBuild.setCurrentTower(Some(tower))
          case _ =>
      case None =>
        setOffFrame(x, y)

  private def handleLevelUpSelection(x: Int, y: Int): Unit =
    optionButtons.keys.find(_.contains(x, y)) match
      case Some(button) =>
        towerBuild.getCurrentTower.foreach (tower =>
          optionButtons(button) match
            case "L" => towerBuild.setCurrentTower(Some(Tower.levelUp(tower)))
            case "S" => towerBuild.setCurrentTower(None)
            case "U" => if (tower.getTowerType == BarrackTower.towerType) then {}
            case _ => // No-op
        )

      case None =>
        setOffFrame(x, y)

  private def setOffFrame(x: Int, y: Int): Unit =
    if (!towerBuild.isInBuildRange(x, y)) then
      gp.getGUI.currentFrame = None

  def draw(g2d: Graphics2D): Unit =
    if (!isDrawingFrame) return

    val g2dCopy = g2d.create().asInstanceOf[Graphics2D]
    transform.setToTranslation(upgradeCoords._1, upgradeCoords._2)
    g2dCopy.drawImage(Image.frame, transform, null)

    towerBuild.getCurrentTower match
      case Some(tower) =>
        drawTowerRange(g2dCopy, tower)
        drawOptionButtons(g2dCopy, tower)
      case None =>
        drawTowerSelectionButtons(g2dCopy)

    g2dCopy.dispose()

  private def drawTowerRange(g2d: Graphics2D, tower: Tower): Unit =
    val circle = tower.attackCircle
    val (centerX, centerY) = (circle.getCenterX.toInt, circle.getCenterY.toInt)
    val (x, y, w, h) = (circle.getX.toInt, circle.getY.toInt, circle.getWidth.toInt, circle.getHeight.toInt)
    val gradient = new RadialGradientPaint(centerX, centerY, w, DIST, COLORS)
    g2d.setPaint(gradient)
    g2d.fillOval(x, y, w, h)
    g2d.drawOval(x, y, w, h)

  private def drawTowerSelectionButtons(g2d: Graphics2D): Unit =
    towerButtons.foreach ((rect, _) =>
      val image: Option[BufferedImage] = rect match
        case r if r == towerButtons.keys.find(_.contains(rect.x, rect.y)).get =>
          towerButtons(r).getTowerType match
            case BarrackTower.towerType => Some(Image.barrack01)
            case ExploTower.towerType => Some(Image.explo01)
            case ArrowTower.towerType => Some(Image.arrow01)
            case MagicTower.towerType => Some(Image.magic01)
            case _ => None
        case _ => None

      image.foreach(g2d.drawImage(_, rect.x, rect.y, None.orNull))
    )

  private def drawOptionButtons(g2d: Graphics2D, tower: Tower): Unit =
    optionButtons.foreach ((rect, action) =>
      val image: Option[BufferedImage] = action match
        case "L" => Some(Image.upgrade)
        case "S" => Some(Image.sell)
        case "U" if tower.getTowerType == BarrackTower.towerType => Some(Image.unite)
        case _ => None

      image.foreach(g2d.drawImage(_, rect.x, rect.y, None.orNull))
    )
