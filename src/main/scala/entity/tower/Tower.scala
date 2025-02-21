package entity.tower

import entity.Entity
import entity.creature.enemy.Enemy
import game.GamePanel


abstract class Tower(gp: GamePanel, private val level: Int) extends Entity(gp):
  var pos: (Double, Double) = (0,0)

  val towerType: String
  protected val apDmg: Double
  protected val adDmg: Double
  protected val range: Double

  override def getName: String = s"${towerType}0$level"
  override def getImagePath: String = s"towers/${getName}.png"
  override def getJsonPath: String = s"towers/${getName}.json"
  override def getAdDmg: Double = adDmg * level
  override def getApDmg: Double = apDmg * level
  override def getRange: Double = range * level

  def attack(enemy: Enemy): Unit = {}
