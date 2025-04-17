package system

import entity.tower.Frame
import game.{GamePanel, GameState}
import gui.Image
import utils.{Constant, SoundConstant, Tools}

import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}
import java.awt.geom.Rectangle2D

/** Represents a clickable button with a defined rectangular area and associated action.
 *
 * @param rect The rectangular area that defines the button's clickable region.
 * @param action The function to execute when the button is clicked, taking x and y coordinates as parameters.
 */
case class Button(rect: Rectangle2D, action: (Int, Int) => Unit):
  /** Checks if a point is within the button's rectangular area.
   *
   * @param x The x-coordinate of the point.
   * @param y The y-coordinate of the point.
   * @return True if the point is within the button's area, false otherwise.
   */
  def contains(x: Int, y: Int): Boolean = rect.contains(x, y)

  /** Executes the button's action with the given coordinates.
   *
   * @param x The x-coordinate of the click.
   * @param y The y-coordinate of the click.
   */
  def execute(x: Int, y: Int): Unit = action(x, y)

/** Handles keyboard and mouse input for the game, managing interactions with buttons and game-specific actions.
 *
 * This class listens for mouse clicks and key presses, mapping them to actions based on the current game state.
 * It manages buttons for different game states (e.g., menu, play, pause) and handles special interactions like
 * tower building and unit placement during gameplay.
 *
 * @param gp The GamePanel instance that provides access to game state and system handlers.
 */
class KeyHandler(gp: GamePanel) extends MouseListener with KeyListener:
  private val offSetY = 30
  var isUniting: Boolean = false

  /** Buttons for selecting stages in the title state. */
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

  /** Buttons for selecting upgrades in the upgrade state. */
  private val upgradeButtons: List[Button] =
    gp.getGUI.getupgradeGUI.getUpgradeList.toList.map((coords, component) =>
      Button(Tools.getRectInRange(coords, component.getCurrentImage),
      (_, _) => {
        gp.getSystemHandler.playSE(SoundConstant.SELECT)
        gp.getGUI.getupgradeGUI.setCurrentFrame(component)
      })
    )

  /** Maps game states to their corresponding buttons for handling mouse clicks.
   *
   * Each game state has a list of buttons that are active during that state, defining clickable areas
   * and their associated actions (e.g., navigating to another state, restarting, or purchasing upgrades).
   */
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
      Button(Tools.getRectInRange(Constant.continueEndStageCoords, Image.continue),
        (_, _) => gp.handleReloadGameState(GameState.TitleState)),
      Button(Tools.getRectInRange(Constant.restartEndStageCoords, Image.restart),
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
          gp.getGUI.currentPreStageId.foreach(gp.setUpStage)
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
          val currentFrame = gp.getGUI.getupgradeGUI.getCurrentFrame
          if currentFrame.canBuy && gp.getSystemHandler.getUpgradeManager.purchaseUpgrade(currentFrame.upgrade, gp.getPlayer) then
            gp.getGUI.getupgradeGUI.reload()
            gp.getSystemHandler.getPlayerDataManager.savePlayerData()
            gp.getSystemHandler.getUpgradeManager.saveUpgrades()
            gp.getGUI.getupgradeGUI.getNextComponent(currentFrame) match
              case Some(component) => gp.getGUI.getupgradeGUI.setCurrentFrame(component)
              case _ =>
        }
      )
    ) ++ upgradeButtons)
  )

  /** Handles mouse click events by executing the action of the clicked button based on the current game state.
   *
   * Checks if the click coordinates fall within any button's area for the current game state and executes
   * the button's action. Additionally, handles special interactions for the PlayState (e.g., tower building).
   *
   * @param e The MouseEvent containing click coordinates.
   */
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

  /** Handles key press events, specifically toggling between PlayState and PauseState with the 'P' key.
   *
   * @param e The KeyEvent containing the pressed key.
   */
  override def keyPressed(e: KeyEvent): Unit =
    gp.getCurrentGameState match
      case GameState.PlayState if e.getKeyCode == KeyEvent.VK_P =>
        gp.handleReloadGameState(GameState.PauseState)
      case GameState.PauseState if e.getKeyCode == KeyEvent.VK_P =>
        gp.handleReloadGameState(GameState.PlayState)
      case _ =>

  override def keyReleased(e: KeyEvent): Unit = {}

  /** Handles special interactions in the PlayState, such as tower building, unit placement, and wave starting.
   *
   * This method processes mouse clicks in the PlayState to handle tower building, unit frame interactions,
   * and starting waves when clicking in a specific area.
   *
   * @param x The x-coordinate of the click.
   * @param y The y-coordinate of the click.
   */
  private def handlePlayStateSpecial(x: Int, y: Int): Unit =
    handleTowerBuildOnClick(x, y)
    gp.getGUI.currentFrame.foreach(frame =>
      if isUniting then
        isUniting = false
        frame.handleUniting(x, y)
        frame.drawingFrame = true
        gp.getGUI.currentFrame = None
      else
        frame.handleFrameOnClick(x, y)
    )

    if x <= 150 && y <= 150 then
      gp.getSystemHandler.getStageManager.startWave()

  /** Handles tower building when clicking on a valid tower build position.
   *
   * Checks if the click is within a tower build position and, if so, plays a sound effect and sets the
   * current frame to a new Frame instance for tower building.
   *
   * @param x The x-coordinate of the click.
   * @param y The y-coordinate of the click.
   */
  private def handleTowerBuildOnClick(x: Int, y: Int): Unit =
    gp.getSystemHandler.getStageManager.getCurrentStage.map(_.getMap.getTowerPos).foreach(towerBuildList =>
      towerBuildList.find(_.isInBuildRange(x, y)).foreach(towerBuild =>
        gp.getSystemHandler.playSE(SoundConstant.SELECT)
        gp.getGUI.currentFrame = Some(Frame(gp, towerBuild))
      )
    )