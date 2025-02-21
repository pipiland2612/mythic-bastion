package stage

import entity.Entity
import game.GamePanel
import utils.Tools

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

class StageManager (gp: GamePanel):

  private var backgroundImage: BufferedImage = _
  private val transform = new AffineTransform()
  val waveSpawner: WaveSpawner = WaveSpawner(this)
  var currentStage: Option[Stage] = None
  var currentPlayer: Option[PlayerStage] = None

  def setUpBackgroundImage(): Unit =
    this.backgroundImage = currentStage match
      case Some(stage) => Tools.scaleImage(Tools.loadImage(s"maps/map${stage.stageID}.jpg"), gp.screenWidth, gp.screenHeight)
      case _ => throw new Exception("Can not find background image path")

  def setStage(stage: Stage): Unit =
    currentStage = Some(stage)
    currentPlayer = Some(PlayerStage(stage.coins))

  def startWave(): Unit =
    currentStage.foreach(stage => waveSpawner.scheduleWaveSpawn(stage.waves))

  def update(): Unit =
    currentStage.foreach ( stage =>
      stage.enemyList.toList.foreach(_.update())
      stage.allianceList.toList.foreach(_.update())
      stage.map.towerPos.foreach(towerBuild =>
        towerBuild.currentTower.foreach(_.update())
      )
      stage.enemyList.filterInPlace(enemy => !enemy.haveReachBase)
    )

  def draw(g2d: Graphics2D): Unit =
    currentStage.foreach(stage =>
      g2d.drawImage(backgroundImage, 0, 0, None.orNull)
      stage.map.towerPos.foreach(towerBuild =>
        val x = towerBuild.pos._1 - stage.map.towerImage.getWidth() / 2
        val y = towerBuild.pos._2 - stage.map.towerImage.getHeight() / 2
        transform.setToTranslation(x, y)
        g2d.drawImage(stage.map.towerImage, transform, None.orNull)
      )

//      val sortedEntities: List[Entity] = (stage.enemyList.toList ++ stage.allianceList.toList ++ stage.towerList.toList).sortBy(_.pos._2)
//      sortedEntities.foreach(_.draw(g2d))
    )