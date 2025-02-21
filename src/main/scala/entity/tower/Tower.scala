package entity.tower

import entity.Entity
import entity.creature.enemy.Enemy
import game.GamePanel
import utils.{Animation, Cache, Tools}

import java.awt.image.BufferedImage

abstract class Tower(gp: GamePanel) extends Entity(gp):
  var pos: (Double, Double)
  val apDmg: Double
  val adDmg: Double
  val range: Double

  var idleAnimation: Animation = _
  var shootAnimation: Animation = _
  var shootEndAnimation: Animation = _

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    shootAnimation = Animation(value(1), 10)
    shootEndAnimation = Animation(value(2), 10)

  def attack(enemy: Enemy): Unit = {}


