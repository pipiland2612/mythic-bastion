package entity.creature.enemy

import game.GamePanel

import java.awt.geom.Rectangle2D

case class Creep(
  name: String,
  maxHealth: Double,
  playerDamage: Double,
  apDmg: Double,
  apDefense: Double,
  adDmg: Double,
  adDefense: Double,
  range: Double,
  speed: Double,
  maxAttackCoolDown: Double,
  maxDeadCounter: Double,
  coin: Double,
  jsonPath: String,
  imagePath: String,
  rect: Rectangle2D,
  gp: GamePanel,
  healthOffSet: (Int, Int),
  var pos: (Double, Double) = (0, 0),
) extends Enemy(gp):
  var health: Double = maxHealth

  this.haveReachBase = false
end Creep

// data = [maxHealth, playerDamage, apDmg, apDefense, adDmg, adDefense, range, speed, attackCoolDown, maxDeadCounter, coin]
object Monster01:
  val data: Vector[Double] = Vector(70, 1, 0, 0, 5, 2, 20, 0.25, 1 * 60, 45, 5)
  val name: String = "Monster01"
  val jsonPath: String = "enemies/Monster01.json"
  val imagePath: String = "enemies/Monster01.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 0, 10, 20)
  val healthOffSet: (Int, Int) = (10, 5)

object Monster02:
  val data: Vector[Double] = Vector(50, 1, 20, 10, 0, 0, 20, 0.5, 1 * 60, 60, 5)
  val name: String = "Monster02"
  val jsonPath: String = "enemies/Monster02.json"
  val imagePath: String = "enemies/Monster02.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 10, 20, 10)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster03:
  val data: Vector[Double] = Vector(50, 1, 0, 0 , 10, 5, 20, 0.5, 1 * 60, 45, 20)
  val name: String = "Monster03"
  val jsonPath: String = "enemies/Monster03.json"
  val imagePath: String = "enemies/Monster03.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 5, 10, 20)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster04:
  val data: Vector[Double] = Vector(20, 1, 0, 0, 5, 2, 20, 0.25, 1 * 60, 45, 5)
  val name: String = "Monster04"
  val jsonPath: String = "enemies/Monster04.json"
  val imagePath: String = "enemies/Monster04.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 0, 10, 20)
  val healthOffSet: (Int, Int) = (-5, 5)

object Monster05:
  val data: Vector[Double] = Vector(20, 1, 20, 10, 0, 0, 20, 0.5, 1 * 60, 60, 5)
  val name: String = "Monster05"
  val jsonPath: String = "enemies/Monster05.json"
  val imagePath: String = "enemies/Monster05.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 10, 20, 10)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster06:
  val data: Vector[Double] = Vector(50, 1, 0, 0 , 10, 5, 20, 0.5, 1 * 60, 45, 20)
  val name: String = "Monster06"
  val jsonPath: String = "enemies/Monster06.json"
  val imagePath: String = "enemies/Monster06.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 5, 10, 20)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster07:
  val data: Vector[Double] = Vector(20, 1, 0, 0, 5, 2, 20, 0.25, 1 * 60, 45, 5)
  val name: String = "Monster07"
  val jsonPath: String = "enemies/Monster07.json"
  val imagePath: String = "enemies/Monster07.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 0, 10, 20)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster08:
  val data: Vector[Double] = Vector(20, 1, 20, 10, 0, 0, 20, 0.5, 1 * 60, 60, 5)
  val name: String = "Monster08"
  val jsonPath: String = "enemies/Monster08.json"
  val imagePath: String = "enemies/Monster08.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 10, 20, 10)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster09:
  val data: Vector[Double] = Vector(50, 1, 0, 0 , 10, 5, 20, 0.5, 1 * 60, 45, 20)
  val name: String = "Monster09"
  val jsonPath: String = "enemies/Monster09.json"
  val imagePath: String = "enemies/Monster09.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 5, 10, 20)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster10:
  val data: Vector[Double] = Vector(20, 1, 0, 0, 5, 2, 20, 0.25, 1 * 60, 45, 5)
  val name: String = "Monster10"
  val jsonPath: String = "enemies/Monster10.json"
  val imagePath: String = "enemies/Monster10.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 0, 10, 20)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster11:
  val data: Vector[Double] = Vector(20, 1, 20, 10, 0, 0, 20, 0.5, 1 * 60, 60, 5)
  val name: String = "Monster11"
  val jsonPath: String = "enemies/Monster11.json"
  val imagePath: String = "enemies/Monster11.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 10, 20, 10)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster12:
  val data: Vector[Double] = Vector(50, 1, 0, 0 , 10, 5, 20, 0.5, 1 * 60, 45, 20)
  val name: String = "Monster12"
  val jsonPath: String = "enemies/Monster12.json"
  val imagePath: String = "enemies/Monster12.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 5, 10, 20)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster13:
  val data: Vector[Double] = Vector(20, 1, 0, 0, 5, 2, 20, 0.25, 1 * 60, 45, 5)
  val name: String = "Monster13"
  val jsonPath: String = "enemies/Monster13.json"
  val imagePath: String = "enemies/Monster13.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 0, 10, 20)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster14:
  val data: Vector[Double] = Vector(20, 1, 20, 10, 0, 0, 20, 0.5, 1 * 60, 60, 5)
  val name: String = "Monster14"
  val jsonPath: String = "enemies/Monster14.json"
  val imagePath: String = "enemies/Monster14.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 10, 20, 10)
  val healthOffSet: (Int, Int) = (5, 5)

object Monster15:
  val data: Vector[Double] = Vector(50, 1, 0, 0 , 10, 5, 20, 0.5, 1 * 60, 45, 20)
  val name: String = "Monster15"
  val jsonPath: String = "enemies/Monster15.json"
  val imagePath: String = "enemies/Monster15.png"
  val rect: Rectangle2D = Rectangle2D.Double(20, 5, 10, 20)
  val healthOffSet: (Int, Int) = (5, 5)