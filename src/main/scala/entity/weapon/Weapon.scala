package entity.weapon

import entity.creature.enemy.Enemy
import entity.{Attacker, Direction, Entity, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.image.BufferedImage

abstract class Weapon(gp: GamePanel, enemy: Enemy) extends Entity(gp: GamePanel) with Attacker:
  private var deadCounter: Int = 0

  protected val deadDuration: Int = 100
  protected val maxAttackCoolDown: Double = 0
  protected val range: Double = 0
  protected var hitAnimation: Animation = _
  protected var hitEndAnimation: Animation = _
  protected val curveConst: Double
  protected val yDrawOffSet: Double
  protected val hitTime: Double = 1.0

  private var attackT: Double = 0.0
  private var attackInProgress: Boolean = false
  private var attackCurve: Option[((Double, Double), (Double, Double), (Double, Double))] = None
  private var angleOffset: Double = 0

  private var hasHit: Boolean = false

  def hit: Boolean = hasHit
  def getCurrentEnemy: Enemy = enemy

  protected def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    hitAnimation = Animation(value(1), 10)
    hitEndAnimation = Animation(value(2), 10)

  protected def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, hitAnimation)

  protected def dealDamage(): Unit =
    val adDamage = getAdDmg - enemy.getAdDefense
    val apDamge = getApDmg - enemy.getApDefense
    enemy.takeDamage(adDamage + apDamge)

  protected def move(angle: Double): Unit =
    val radians = Math.toRadians(angle)
    val x = pos._1 + speed * Math.cos(radians)
    val y = pos._2 + speed * Math.sin(radians)
    this.pos = (x, y)
    needsAnimationUpdate = true

  override def update(): Unit =
    super.update()
    if this.state == State.ATTACK then
      deadCounter += 1
      needsAnimationUpdate = true
      if deadCounter >= deadDuration then hasHit = true
    else attack()


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
    val distance = Math.sqrt(Math.pow(end._1 - start._1, 2) + Math.pow(end._2 - start._2, 2))
    val yOffset = -distance * curveConst  // make the curve higher in the middle (adjust for more or less curve)

    ((start._1 + end._1) / 2 + xOffset, (start._2 + end._2) / 2 + yOffset)

  private def moveAlongCurve(): Unit =
    attackCurve match
      case Some((start, mid, end)) =>
        attackT = (attackT + 0.02).min(1.0)
        pos = Tools.bezier(attackT, start, mid, end)
      case None =>
        attackInProgress = false

  private def updateProjectileMovement(): Unit =
    val angle = Tools.getAngle(pos, enemy.getPosition) - angleOffset
    move(angle)
    angleOffset *= 0.98 // gradually reduce the angle for the projectile to straighten

  private def finalizeAttack(): Unit =
    if (attackT >= hitTime) then
      attackInProgress = false
      dealDamage()
      this.state = State.ATTACK
      needsAnimationUpdate = true
      checkAnimationUpdate()
      this.pos = (enemy.getPosition._1, enemy.getPosition._2 - yDrawOffSet) // set the final position to the enemy's position to play the boom animation

  def attack(): Unit =
    if (!attackInProgress) then
      initializeAttackCurve()
    else
      moveAlongCurve()
      updateProjectileMovement()
      finalizeAttack()
// In the Weapon companion object, initialize velocities after creation
object Weapon:
  private var gp: GamePanel = _

  def setUp(gp: GamePanel): Unit = this.gp = gp

  def clone(weapon: String, enemy: Enemy, pos: (Double, Double)): Weapon =
    weapon match
      case Explo.name => Explo(gp, enemy, pos)
      case Arrow.name => Arrow(gp, enemy, pos)
      case MagicBullet.name => MagicBullet(gp, enemy, pos)