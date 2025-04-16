package entity.tower

import entity.creature.enemy.Enemy
import entity.weapon.Weapon
import entity.{Entity, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.geom.{AffineTransform, Ellipse2D}
import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D}
import scala.collection.mutable.ListBuffer
import scala.util.Random

/** Abstract base class for towers in the game, responsible for attacking enemies within range.
 * Extends Entity for rendering and position management.
 * @param gp The GamePanel instance managing the game.
 * @param level The current level of the tower.
 */
abstract class Tower(val gp: GamePanel, var level: Int) extends Entity(gp):
  this.currentAnimation = Some(idleAnimation)

  override def getName: String = s"$name"
  override def getImagePath: String = s"towers/$getName.png"
  override def getJsonPath: String = s"towers/$getName.json"
  override def getRange: Double = (range + level * 5) * getRangeMultiplier

  private val transform: AffineTransform = AffineTransform()
  private var attackCounter: Int = 0
  private var hasShoot = false
  private var prepareCounter: Int = 0
  protected val towerType: String
  protected val towerImagePath: String
  protected var towerImage: BufferedImage = Tools.loadImage(s"towers/${towerImagePath}0$level.png")
  protected val offsetX: Double = 0
  protected val offsetY: Double = -10
  protected val drawOffsetX: Double = 25
  protected val drawOffsetY: Double = 15
  protected val bulletList: ListBuffer[Weapon] = ListBuffer()
  protected var shootAnimation: Animation = _
  protected val weaponType: String
  protected val maxAttackCounter: Int
  protected val maxPrepareCounter: Int
  protected val readySoundEffect: Array[String]
  val centerCoords: (Double, Double) = calculateCenterCoords()
  val attackCircle: Ellipse2D = createAttackCircle()
  var isShowingRange: Boolean = false

  /** Selects a target enemy from a list of enemies in range.
   * @param enemyList List of enemies within the tower's range.
   * @return The chosen enemy, if any.
   */
  protected def chooseEnemy(enemyList: ListBuffer[Enemy]): Option[Enemy]

  /** Returns the range multiplier for the tower, to be implemented by subclasses. */
  protected def getRangeMultiplier: Double

  /** Calculates the starting position for bullets fired by the tower.
   * @return The coordinates for bullet spawning.
   */
  def bulletPosition: (Double, Double) =
    val frame = idleAnimation.getCurrentFrame
    (centerCoords._1 + frame.getWidth() / 2, centerCoords._2 + frame.getHeight() / 4)

  def updateTowerImage(): Unit =
    this.towerImage = Tools.loadImage(s"towers/${towerImagePath}0$level.png")

  def getTowerType: String = towerType
  def getBulletList: List[Weapon] = bulletList.toList

  /** Updates the tower's state, including attack cooldown, enemy targeting, bullets, and animations. */
  override def update(): Unit =
    updateAttackCooldown()
    super.update()
    handleEnemyAttack()
    updateBullets()
    handleAttackState()
    handlePrepareState()

  /** Renders the tower, its bullets, and optionally its range circle.
   * @param g2d The Graphics2D context for rendering.
   */
  override def draw(g2d: Graphics2D): Unit =
    drawBullets(g2d)
    drawRangeCircle(g2d)
    Tools.drawFrame(g2d, this.towerImage, transform, centerCoords, offsetX, offsetY)
    currentAnimation.foreach(anim =>
      Tools.drawFrame(g2d, anim.getCurrentFrame, transform, centerCoords, drawOffsetX, drawOffsetY)
    )

  /** Calculates the center coordinates of the tower for rendering and attack range.
   * @return The center coordinates of the tower.
   */
  private def calculateCenterCoords(): (Double, Double) =
    if Option(idleAnimation).isDefined then
      Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame)
    else
      Tools.getCenterCoords(pos, towerImage)

  /** Creates an elliptical attack range circle for the tower.
   * @return The Ellipse2D representing the tower's attack range.
   */
  private def createAttackCircle(): Ellipse2D =
    val image = if Option(idleAnimation).isDefined then idleAnimation.getCurrentFrame else towerImage
    new Ellipse2D.Double(
      centerCoords._1 - (getRange * 2 - image.getWidth()) / 2,
      centerCoords._2 - (getRange * 4 / 3 - image.getHeight()) / 2,
      getRange * 2,
      getRange * 4 / 3
    )

  private def updateAttackCooldown(): Unit =
    if attackCoolDown > 0 then
      attackCoolDown -= 1

  /** Initiates an attack on a specified enemy, firing a bullet if conditions are met.
   * @param enemy The target enemy.
   */
  private def attack(enemy: Enemy): Unit =
    if attackCoolDown <= 0 && this.state != State.PREPARE then
      state = State.ATTACK
      attackCoolDown = maxAttackCoolDown
      needsAnimationUpdate = true
      if shootAnimation.isInAttackInterval && !hasShoot then
        val pos = bulletPosition
        val bullet = Weapon.clone(weaponType, enemy, pos)
        bulletList += bullet
        val se = bullet.getFlySE
        if se.nonEmpty then
          val random = Random.nextInt(se.length)
          gp.getSystemHandler.playSE(se(random))
        hasShoot = true

  private def handleEnemyAttack(): Unit =
    findEnemy() match
      case Some(list) if list.nonEmpty => chooseEnemy(list).foreach(attack)
      case _ =>

  /** Updates all active bullets fired by the tower and removes those that have hit their target. */
  private def updateBullets(): Unit =
    bulletList.toList.foreach(_.update())
    bulletList.filterInPlace(!_.hit)

  /** Manages the attack state, transitioning to prepare state after the attack duration. */
  private def handleAttackState(): Unit =
    if this.state == State.ATTACK then
      attackCounter += 1
      if attackCounter >= maxAttackCounter then
        attackCounter = 0
        currentAnimation.foreach(_.reset())
        state = State.PREPARE
        hasShoot = false
      needsAnimationUpdate = true

  private var hasPlayReadySound: Boolean = false
  private def handlePrepareState(): Unit =
    if this.state == State.PREPARE then
      prepareCounter += 1
      if prepareCounter >= maxPrepareCounter then
        currentAnimation.foreach(_.reset())
        prepareCounter = 0
        this.state = State.IDLE
      needsAnimationUpdate = true

  private def playReadySound(): Unit =
    if readySoundEffect.nonEmpty then
      val random = Random.nextInt(readySoundEffect.length)
      gp.getSystemHandler.playSE(readySoundEffect(random))
      hasPlayReadySound = true

  /** Renders all active bullets fired by the tower.
   * @param g2d The Graphics2D context for rendering.
   */
  private def drawBullets(g2d: Graphics2D): Unit =
    bulletList.toList.foreach(_.draw(g2d))

  /** Draws the tower's attack range circle if enabled.
   * @param g2d The Graphics2D context for rendering.
   */
  protected def drawRangeCircle(g2d: Graphics2D): Unit =
    if isShowingRange then
      g2d.setColor(Color.RED)
      g2d.draw(attackCircle)

  /** Scans for enemies within the tower's attack range.
   * @return An optional list of enemies in range, if any.
   */
  def findEnemy(): Option[ListBuffer[Enemy]] =
    gp.getSystemHandler.getStageManager.getGrid match
      case Some(grid) =>
        Some(grid.scanForEnemiesInRange(this))
      case _ => None

/** Companion object for Tower, handling tower upgrades and pricing. */
object Tower:
  /** Calculates the cost to level up a tower.
   * @param tower The tower to level up.
   * @param level The current level of the tower.
   * @return The cost to upgrade, if applicable.
   */
  def moneyToLevelUp(tower: Tower, level: Int): Option[Int] =
    tower.getTowerType match
      case BarrackTower.towerType =>
        BarrackTower.updatePrice(level)
      case ArrowTower.towerType =>
        ArrowTower.updatePrice(level)
      case MagicTower.towerType =>
        MagicTower.updatePrice(level)
      case ExploTower.towerType =>
        ExploTower.updatePrice(level)
      case _ => None

  /** Creates a new tower instance with an incremented level.
   * @param tower The tower to level up.
   * @param currentLevel The current level of the tower.
   * @return A new tower instance with the upgraded level.
   */
  def levelUp(tower: Tower, currentLevel: Int): Tower =
    tower.towerType match
      case BarrackTower.towerType => BarrackTower(tower.gp, currentLevel + 1, tower.getPosition)
      case ArrowTower.towerType => ArrowTower(tower.gp, currentLevel + 1, tower.getPosition)
      case MagicTower.towerType => MagicTower(tower.gp, currentLevel + 1, tower.getPosition)
      case ExploTower.towerType => ExploTower(tower.gp, currentLevel + 1, tower.getPosition)