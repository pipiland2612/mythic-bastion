package entity.weapon

import entity.creature.enemy.Enemy
import entity.{Attacker, Direction, Entity, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.image.BufferedImage

abstract class Weapon(gp: GamePanel) extends Entity(gp: GamePanel) with Attacker:
  var pos: (Double, Double) = (0,0)
  val maxAttackCoolDown: Double = 0
  val range: Double = 0
  var hitAnimation: Animation = _
  var hitEndAnimation: Animation = _
  var hasHit: Boolean = false

  def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    hitAnimation = Animation(value(1), 10)
    hitEndAnimation = Animation(value(2), 10)

  def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, hitAnimation)

  def dealDamage(enemy: Enemy): Unit =
    val adDamage = getAdDmg - enemy.getAdDefense
    val apDamge = getApDmg - enemy.getApDefense
    enemy.takeDamage(adDamage + apDamge)

  def move(angle: Double): Unit =
    val radians = Math.toRadians(angle)
    val x = pos._1 + speed * Math.cos(radians)
    val y = pos._2 + speed * Math.sin(radians)
    this.pos = (x, y)

  def attack(enemy: Enemy): Unit =
    val angle = Tools.getAngle(pos, enemy.pos)
    move(angle)
    if this.pos == enemy.pos then
      dealDamage(enemy)
      hasHit = true

  override def update(): Unit =
    super.update()

object Weapon:
  var gp: GamePanel = _

  def clone(weapon: Weapon): Weapon =
    weapon.getName match
      case Explo.name => Explo(gp)
      case Arrow.name => Arrow(gp)
      case MagicBullet.name => MagicBullet(gp)