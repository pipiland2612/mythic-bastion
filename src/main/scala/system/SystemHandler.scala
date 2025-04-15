package system

import game.GamePanel
import system.stage.{Stage, StageManager}
import system.upgrade.UpgradeManager
import utils.Tools

import java.awt.Graphics2D

class SystemHandler (gp: GamePanel):
  private val keyHandler: KeyHandler = KeyHandler(gp)
  private val stageManager: StageManager = StageManager(gp)
  private val sound: Sound = Sound()
  private val soundEffect: Sound = Sound()
  private val upgradeManager: UpgradeManager = UpgradeManager()
  private val playerDataManager: PlayerDataManager = PlayerDataManager(gp)

  def getKeyHandler: KeyHandler = keyHandler
  def getStageManager: StageManager = stageManager
  def getSound: Sound = sound
  def getUpgradeManager: UpgradeManager = upgradeManager
  def getPlayerDataManager: PlayerDataManager = playerDataManager

  def setUp(int: Int): Unit =
    require(int >= 1 && int <= 5)
    val stage: Stage = Tools.loadStage(s"stages/Stage0$int.json")
    stageManager.setStage(stage)

  def restart(): Unit = 
    stageManager.restart()

  def continue(): Unit =
    stageManager.continue()

  def update(): Unit =
    stageManager.update()

  def draw(g2d: Graphics2D): Unit =
    stageManager.draw(g2d)

  def playMusic(path: String): Unit =
    this.sound.setFile(path)
    this.sound.play()
    this.sound.loop()

  def stopMusic (): Unit = this.sound.stop()

  def playSE (path: String): Unit =
    this.soundEffect.setFile(path)
    this.soundEffect.play()
