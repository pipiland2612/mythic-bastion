package entity.creature.alliance

import game.GamePanel

import java.awt.geom.Rectangle2D

case class Soldier(
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
) extends Alliance(gp):
end Soldier

// data = [maxHealth, health, playerDamage, apDmg, apDefense, adDmg, adDefense, range, speed, attackCoolDown, maxDeadCounter]
object Helper01:
  val data: Vector[Double] = Vector(10, 10, 1, 0, 0, 5, 2, 2, 0.25, 1 * 60, 45)
  val name: String = "Helper01"
  val jsonPath: String = "alliances/Helper01.json"
  val imagePath: String = "alliances/Helper01.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 0, 10, 20)

object Helper02:
  val data: Vector[Double] = Vector(10, 10, 1, 20, 10, 0, 0, 5, 0.5, 1 * 60, 60)
  val name: String = "Helper02"
  val jsonPath: String = "alliances/Helper02.json"
  val imagePath: String = "alliances/Helper02.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 10, 20, 10)

object Soldier01:
  val data: Vector[Double] = Vector(50, 50, 1, 0, 0 , 10, 5, 1, 0.5, 1 * 60, 45)
  val name: String = "Soldier01"
  val jsonPath: String = "enemies/Soldier01.json"
  val imagePath: String = "enemies/Soldier01.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 5, 10, 20)