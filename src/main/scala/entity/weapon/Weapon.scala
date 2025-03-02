package entity.weapon

import entity.creature.enemy.Enemy
import entity.{Attacker, Direction, Entity, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage

abstract class Weapon(gp: GamePanel, enemy: Enemy) extends Entity(gp: GamePanel) with Attacker:
  this.currentAnimation = Some(idleAnimation)
  private var deadCounter: Int = 0
  private var attackInProgress: Boolean = false
  private var angleOffset: Double = 0
  private var hasHit: Boolean = false

  protected val deadDuration: Int = 100
  protected val maxAttackCoolDown: Double = 0
  protected val range: Double = 0
  protected var hitAnimation: Animation = _
  protected var hitEndAnimation: Animation = _
  protected val curveConst: Double
  protected val yDrawOffSet: Double
  protected val hitTime: Double = 1.0
  protected val weight: Double = 2
  protected var attackT: Double = 0.0
  protected var attackCurve: Option[((Double, Double), (Double, Double), (Double, Double))] = None

  private val baseSpeed: Double = 0.02

  def hit: Boolean = hasHit
  def getCurrentEnemy: Enemy = enemy
  def attackCircle: Ellipse2D = Ellipse2D.Double(0, 0, 0, 0)

  protected def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(frames = value(0), frameDuration = 10)
    hitAnimation = Animation(frames = value(1), frameDuration = 10)
    hitEndAnimation = Animation(frames = value(2), frameDuration = 10)

  protected def setUpImages(): Unit =
    this.images = AnimationFactory.createWeaponAnimationMap(
      directions = Direction.allEntityDirections,
      idleAnim = idleAnimation,
      attackAnim = hitAnimation
    )

  protected def dealDamage(): Unit =
    val adDamage = Math.max(getAdDmg - enemy.getAdDefense, 0)
    val apDamage = Math.max(getApDmg - enemy.getApDefense, 0)
    enemy.takeDamage(adDamage + apDamage)

  protected def move(angle: Double): Unit =
    val radians = Math.toRadians(angle)
    val x = pos._1 + speed * Math.cos(radians)
    val y = pos._2 + speed * Math.sin(radians)
    this.pos = (x, y)
    needsAnimationUpdate = true

  override def update(): Unit =
    super.update()
    if this.state == State.ATTACK then
      updateDeadState()
    else
      attack()

  private def updateDeadState(): Unit =
    deadCounter += 1
    needsAnimationUpdate = true
    if deadCounter >= deadDuration then
      hasHit = true

  def attack(): Unit =
    if !attackInProgress then
      initializeAttackCurve()
    else
      moveAlongCurve()
      updateProjectileMovement()
      finalizeAttack()

  private def initializeAttackCurve(): Unit =
    val start = pos
    val end = (enemy.attackBox.getCenterX, enemy.attackBox.getCenterY)
    val mid = calculateMidPoint(start, end)
    attackCurve = Some((start, mid, end))
    attackT = 0.0
    attackInProgress = true
    angleOffset = Math.toRadians(40)

  private def calculateMidPoint(start: (Double, Double), end: (Double, Double)): (Double, Double) =
    val xOffset = Math.random() * 40 - 20
    val distance = Tools.distance(end, start)
    val yOffset = -distance * curveConst
    ((start._1 + end._1) / 2 + xOffset, (start._2 + end._2) / 2 + yOffset)

  private def moveAlongCurve(): Unit =
    attackCurve match
      case Some((start, mid, end)) =>
        val distance = Tools.distance(enemy.getPosition, start)
        val normalizedDist = (distance / 100).max(1.0)
        val dynamicSpeed = baseSpeed * (speed / normalizedDist)
        val isDescending = attackT >= 0.45
        val weightFactor = if isDescending then 1 + (weight * 0.2) else 1.0
        attackT = (attackT + dynamicSpeed * weightFactor).min(1.0)
        pos = Tools.bezier(attackT, start, mid, enemy.getPosition)
      case None =>
        attackInProgress = false

  private def updateProjectileMovement(): Unit =
    val angle = Tools.getAngle(pos, enemy.getPosition) - angleOffset
    move(angle)
    angleOffset *= 0.98

  private def finalizeAttack(): Unit =
    if attackT >= hitTime then
      attackInProgress = false
      dealDamage()
      this.state = State.ATTACK
      needsAnimationUpdate = true
      checkAnimationUpdate()
      this.pos = (enemy.getPosition._1, enemy.getPosition._2 - yDrawOffSet)

object Weapon:
  private var gp: GamePanel = _

  def setUp(gp: GamePanel): Unit = this.gp = gp

  def clone(weapon: String, enemy: Enemy, pos: (Double, Double)): Weapon =
    weapon match
      case Explo.name => Explo(gp, enemy, pos)
      case Arrow.name => Arrow(gp, enemy, pos)
      case MagicBullet.name => MagicBullet(gp, enemy, pos)

object AnimationFactory:
  def createWeaponAnimationMap(
    directions: Seq[Direction],
    idleAnim: Animation,
    attackAnim: Animation
  ): Map[(Direction, State), Animation] =
    Tools.fillMap(directions, State.IDLE, idleAnim) ++
    Tools.fillMap(directions, State.ATTACK, attackAnim)