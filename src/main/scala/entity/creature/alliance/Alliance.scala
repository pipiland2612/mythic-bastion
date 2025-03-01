package entity.creature.alliance

import entity.{Direction, State}
import entity.creature.Creature
import game.GamePanel
import utils.{Animation, Tools}

import java.awt.image.BufferedImage

abstract class Alliance(gp: GamePanel) extends Creature(gp):
  val maxHealth: Double
  var health: Double
  scaleFactor = 1.25

  override def setUpImages(): Unit =
    val mirroredDirections = Seq(Direction.LEFT, Direction.UP_LEFT, Direction.DOWN_LEFT)
    this.images =
      Tools.fillMap(mirroredDirections, State.IDLE, Tools.flipAnimation(idleAnimation)) ++
      Tools.fillMap(Direction.allCreatureDirections.diff(mirroredDirections), State.IDLE, idleAnimation) ++
      Tools.fillMap(mirroredDirections, State.RUN, Tools.flipAnimation(walkingAnimation)) ++
      Tools.fillMap(Direction.allCreatureDirections.diff(mirroredDirections), State.RUN, walkingAnimation) ++
      Tools.fillMap(mirroredDirections, State.ATTACK, Tools.flipAnimation(fightingAnimation)) ++
      Tools.fillMap(Direction.allCreatureDirections.diff(mirroredDirections), State.ATTACK, fightingAnimation) ++
      Tools.fillMap(mirroredDirections, State.DEAD, Tools.flipAnimation(deadAnimation)) ++
      Tools.fillMap(Direction.allCreatureDirections.diff(mirroredDirections), State.DEAD, deadAnimation)

  override def parseInformation(value: Vector[Vector[BufferedImage]]): Unit =
    idleAnimation = Animation(value(0), 10)
    walkingAnimation = Animation(value(1), 10)
    fightingAnimation = Animation(value(2), 10)
    deadAnimation = Animation(value(3), 10)