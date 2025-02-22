package entity.weapon

import entity.{Direction, Entity, State}
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.image.BufferedImage

abstract class Weapon(gp: GamePanel) extends Entity(gp: GamePanel):
  var pos: (Double, Double) = (0,0)

  var hitAnimation: Animation = _
  var hitEndAnimation: Animation = _

  def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    hitAnimation = Animation(value(1), 10)
    hitEndAnimation = Animation(value(2), 10)

  def setUpImages(): Unit =
    this.images =
      Tools.fillMap(Direction.allEntityDirections, State.IDLE, idleAnimation) ++
      Tools.fillMap(Direction.allEntityDirections, State.ATTACK, hitAnimation)
