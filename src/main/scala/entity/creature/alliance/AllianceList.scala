package entity.creature.alliance

import game.GamePanel

import java.awt.geom.{Ellipse2D, Rectangle2D}

case class Soldier(
  name: String,
  maxHealth: Double,
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
  healthOffSet: (Int, Int),
  var pos: (Double, Double) = (0, 0),
) extends Alliance(gp):
  var health: Double = maxHealth
  override def attackCircle: Ellipse2D = Ellipse2D.Double(pos._1 + 16, pos._2 + 15, getRange*2, getRange*4/3)

end Soldier

// data = [maxHealth, apDmg, apDefense, adDmg, adDefense, range, speed, attackCoolDown, maxDeadCounter]
object Helper01:
  val data: Vector[Double] = Vector(50, 0, 0, 5, 2, 2, 0.25, 1 * 60, 45)
  val name: String = "Helper01"
  val jsonPath: String = "alliances/Helper01.json"
  val imagePath: String = "alliances/Helper01.png"
  val rect: Rectangle2D = Rectangle2D.Double(30, 20, 10, 20)
  val healthOffSet: (Int, Int) = (5,5)

object Helper02:
  val data: Vector[Double] = Vector(70, 20, 10, 0, 0, 5, 0.5, 1 * 60, 60)
  val name: String = "Helper02"
  val jsonPath: String = "alliances/Helper02.json"
  val imagePath: String = "alliances/Helper02.png"
  val rect: Rectangle2D = Rectangle2D.Double(30, 20, 20, 10)
  val healthOffSet: (Int, Int) = (5,5)

object Soldier01:
  val data: Vector[Double] = Vector(70, 0, 0, 10, 0, 20, 0.5, 1 * 60, 45)
  val name: String = "Soldier01"
  val jsonPath: String = "alliances/Soldier01.json"
  val imagePath: String = "alliances/Soldier01.png"
  val rect: Rectangle2D = Rectangle2D.Double(32, 15, 10, 20)
  val healthOffSet: (Int, Int) = (5,5)