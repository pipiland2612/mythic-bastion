package system

import entity.tower.{Frame, TowerBuild}
import game.{GamePanel, GameState}
import gui.{Image, UpgradeGUI}
import utils.{Constant, SoundConstant, Tools}

import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}
import java.awt.geom.Rectangle2D

case class Button(rect: Rectangle2D, action: (Int, Int) => Unit):
  def contains(x: Int, y: Int): Boolean = rect.contains(x, y)
  def execute(x: Int, y: Int): Unit = action(x, y)

class KeyHandler(gp: GamePanel) extends MouseListener with KeyListener:
  private val offSetY = 30
  var isUniting: Boolean = false

  private val stageButtons: List[Button] = List(
    (Constant.stage01Coords, 1),
    (Constant.stage02Coords, 2),
    (Constant.stage03Coords, 3),
    (Constant.stage04Coords, 4),
    (Constant.stage05Coords, 5)
  ).map((coords, id) =>
    Button(Tools.getRectInRange(coords, Image.red_stage), (_, _) => {
      gp.getGUI.currentPreStageId = Some(id)
      gp.getGUI.reloadPreStagebg()
      gp.handleReloadGameState(GameState.PreStageState)
    })
  )

  private val upgradeButtons: List[Button] =
    UpgradeGUI.getUpgradeList.toList.map((coords, component) =>
      Button(Tools.getRectInRange(coords, component.getCurrentImage),
      (_, _) => {
        gp.getSystemHandler.playSE(SoundConstant.SELECT)
        UpgradeGUI.setCurrentFrame(component)
      })
    )

  // Define buttons for each game state
  private val buttonsByState: Map[GameState, List[Button]] = Map(
    GameState.GameMenuState -> List(
      Button(Tools.getRectInRange(Constant.startCoords, Image.start),
        (_, _) => gp.handleReloadGameState(GameState.TitleState))
    ),
    GameState.TitleState -> (List(
      Button(Tools.getRectInRange((700, 500), Image.menu_upgrade),
        (_, _) => gp.handleReloadGameState(GameState.UpgradeState))
    ) ++ stageButtons),
    GameState.PlayState -> List(
      Button(Tools.getRectInRange(Constant.topRightCoords, Image.pause),
        (_, _) => gp.handleReloadGameState(GameState.PauseState))
    ),
    GameState.PauseState -> List(
      Button(Rectangle2D.Double(680, 165, 40, 40),
        (_, _) => gp.handleReloadGameState(GameState.PlayState)),
      Button(Rectangle2D.Double(500, 335, 170, 50),
        (_, _) => gp.handleReloadGameState(GameState.TitleState)),
      Button(Rectangle2D.Double(300, 335, 170, 50),
        (_, _) => gp.restart())
    ),
    GameState.EndStageState -> List(
      Button(Tools.getRectInRange(Constant.quitEndStageCoords, Image.quit),
        (_, _) => gp.handleReloadGameState(GameState.TitleState)),
      Button(Tools.getRectInRange(Constant.restartEndStageCoords, Image.restart),
        (_, _) => gp.restart())
    ),
    GameState.WinStageState -> List(
      Button(Tools.getRectInRange(Constant.quitEndStageCoords, Image.quit),
        (_, _) => gp.handleReloadGameState(GameState.TitleState)),
      Button(Tools.getRectInRange(Constant.continueEndStageCoords, Image.continue),
        (_, _) => gp.restart())
    ),
    GameState.PreStageState -> List(
      Button(Tools.getRectInRange(Constant.exitPreStageCoords, Image.exit),
        (_, _) => {
          gp.getGUI.currentPreStageId = None
          gp.handleReloadGameState(GameState.TitleState)
        }),
      Button(Tools.getRectInRange(Constant.playPreStageCoords, Image.play),
        (_, _) => {
          gp.getGUI.currentPreStageId.foreach(gp.setUpStage(_))
          gp.getGUI.currentPreStageId = None
          gp.handleReloadGameState(GameState.PlayState)
        })
    ),
    GameState.UpgradeState -> (List(
      Button(Tools.getRectInRange(Constant.xUpgradeStageCoords, Image.x),
        (_, _) => gp.handleReloadGameState(GameState.TitleState)
      ),
      Button(Tools.getRectInRange(Constant.buyUpgradeStageCoords, Image.buy_upgrade),
        (_, _) => {
          val currentUpgrade = UpgradeGUI.getCurrentFrame.upgrade
          if gp.getSystemHandler.getUpgradeManager.purchaseUpgrade(currentUpgrade, gp.getPlayer) then
            println("Buy successfully")
        }
      )
    )
      ++ upgradeButtons
    )
  )

  override def mouseClicked(e: MouseEvent): Unit =
    val (x, y) = (e.getX, e.getY - offSetY)
    println(s"Mouse clicked at: $x, $y")

    val currentState = gp.getCurrentGameState
    buttonsByState.getOrElse(currentState, Nil).find(_.contains(x, y)).foreach(_.execute(x, y))

    currentState match
      case GameState.PlayState => handlePlayStateSpecial(x, y)
      case _ =>

  override def mousePressed(e: MouseEvent): Unit = {}
  override def mouseReleased(e: MouseEvent): Unit = {}
  override def mouseEntered(e: MouseEvent): Unit = {}
  override def mouseExited(e: MouseEvent): Unit = {}

  override def keyTyped(e: KeyEvent): Unit = {}

  override def keyPressed(e: KeyEvent): Unit =
    gp.getCurrentGameState match
      case GameState.PlayState if e.getKeyCode == KeyEvent.VK_P =>
        gp.handleReloadGameState(GameState.PauseState)
      case GameState.PauseState if e.getKeyCode == KeyEvent.VK_P =>
        gp.handleReloadGameState(GameState.PlayState)
      case _ =>

  override def keyReleased(e: KeyEvent): Unit = {}

  private def handlePlayStateSpecial(x: Int, y: Int): Unit =
    handleTowerBuildOnClick(x, y)
    gp.getGUI.currentFrame.foreach(frame =>
      if (isUniting) then
        isUniting = false
        frame.handleUniting(x, y)
        frame.drawingFrame = true
        gp.getGUI.currentFrame = None
      else
        frame.handleFrameOnClick(x, y)
    )

    if (x <= 150 && y <= 150) then
      gp.getSystemHandler.getStageManager.startWave()

  private def handleTowerBuildOnClick(x: Int, y: Int): Unit =
    gp.getSystemHandler.getStageManager.getCurrentStage.map(_.getMap.getTowerPos).foreach(towerBuildList =>
      towerBuildList.find(_.isInBuildRange(x, y)).foreach(towerBuild =>
        gp.getSystemHandler.playSE(SoundConstant.SELECT)
        gp.getGUI.currentFrame = Some(Frame(gp, towerBuild))
      )
    )