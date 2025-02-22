package entity.creature.enemy

import game.GamePanel

import java.awt.geom.Rectangle2D

case class Creep(
  name: String,
  maxHealth: Double,
  var health: Double,
  playerDamage: Double,
  apDmg: Double,
  apDefense: Double,
  adDmg: Double,
  adDefense: Double,
  range: Double,
  speed: Double,
  maxAttackCoolDown: Double,
  maxDeadCounter: Double,
  jsonPath: String,
  imagePath: String,
  rect: Rectangle2D,
  gp: GamePanel,
  var pos: (Double, Double) = (0, 0),
) extends Enemy(gp):

  this.haveReachBase = false
  currentAnimation = Some(idleAnimation)
end Creep

// data = [maxHealth, health, playerDamage, apDmg, apDefense, adDmg, adDefense, range, speed, attackCoolDown, maxDeadCounter]
object Monster01:
  val data: Vector[Double] = Vector(10, 10, 1, 0, 0, 5, 2, 2, 0.25, 1 * 60, 45)
  val name: String = "Monster01"
  val jsonPath: String = "enemies/Monster01.json"
  val imagePath: String = "enemies/Monster01.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 0, 10, 20)

object Monster02:
  val data: Vector[Double] = Vector(10, 10, 1, 20, 10, 0, 0, 5, 0.5, 1 * 60, 60)
  val name: String = "Monster02"
  val jsonPath: String = "enemies/Monster02.json"
  val imagePath: String = "enemies/Monster02.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 10, 20, 10)

object Monster03:
  val data: Vector[Double] = Vector(50, 50, 1, 0, 0 , 10, 5, 1, 0.5, 1 * 60, 60)
  val name: String = "Monster03"
  val jsonPath: String = "enemies/Monster03.json"
  val imagePath: String = "enemies/Monster03.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 5, 10, 20)