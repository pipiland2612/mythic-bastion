package entity.weapon

import entity.creature.enemy.Enemy
import entity.{Attacker, Direction, Entity, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.image.BufferedImage

abstract class Weapon(gp: GamePanel, enemy: Enemy) extends Entity(gp: GamePanel) with Attacker:
  val maxAttackCoolDown: Double = 0
  val range: Double = 0
  var hitAnimation: Animation = _
  var hitEndAnimation: Animation = _
  private var deadCounter: Int = 0
  private val deadDuration: Int = 100
  var hasHit: Boolean = false

  def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    hitAnimation = Animation(value(1), 10)
    hitEndAnimation = Animation(value(2), 10)

  def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, hitAnimation)

  def dealDamage(): Unit =
    val adDamage = getAdDmg - enemy.getAdDefense
    val apDamge = getApDmg - enemy.getApDefense
    enemy.takeDamage(adDamage + apDamge)

  def move(angle: Double): Unit =
    val radians = Math.toRadians(angle)
    val x = pos._1 + speed * Math.cos(radians)
    val y = pos._2 + speed * Math.sin(radians)
    this.pos = (x, y)
    needsAnimationUpdate = true

  def attack(): Unit =
    val angle = Tools.getAngle(pos, enemy.pos)
    move(angle)
    val (xDist, yDist) = (enemy.pos._1 - this.pos._1, enemy.pos._2 - this.pos._2)
    if (Math.abs(xDist) <= this.speed && Math.abs(yDist) <= this.speed) then
      dealDamage()
      this.state = State.ATTACK
      needsAnimationUpdate = true
      checkAnimationUpdate()
      this.pos = (enemy.pos._1, enemy.pos._2 - 45)

  override def update(): Unit =
    super.update()
    if this.state == State.ATTACK then
      deadCounter += 1
      needsAnimationUpdate = true
      if deadCounter >= deadDuration then hasHit = true
    else attack()

object Weapon:
  var gp: GamePanel = _

  def clone(weapon: String, enemy: Enemy, pos: (Double, Double)): Weapon =
    weapon match
      case Explo.name => Explo(gp, enemy, pos)
      case Arrow.name => Arrow(gp, enemy, pos)
      case MagicBullet.name => MagicBullet(gp, enemy, pos)