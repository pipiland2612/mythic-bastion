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

object Alliance:
  private var gp: GamePanel = _
  def setUp(gp: GamePanel): Unit = this.gp = gp

  def allianceOfName(key: String, position: (Double, Double)): Option[Alliance] =
    val allianceData = Map(
      Helper01.name -> (Helper01.data, Helper01.jsonPath, Helper01.imagePath, Helper01.rect),
      Helper02.name -> (Helper02.data, Helper02.jsonPath, Helper02.imagePath, Helper02.rect),
      Soldier01.name -> (Soldier01.data, Soldier01.jsonPath, Soldier01.imagePath, Soldier01.rect)
    )

    allianceData.get(key).map ((data, jsonData, imageData, rect) =>
      val soldier = Soldier(key, data(0), data(1), data(2), data(3), data(4), data(5), data(6), data(7), data(8), jsonData, imageData, rect, gp)
      soldier.setPosition(position)
      soldier
    )

  def clone(alliance: Alliance): Alliance =
    Soldier(
      alliance.getName, alliance.getMaxHealth,
      alliance.getApDmg, alliance.getApDefense,alliance.getAdDmg, alliance.getAdDefense,
      alliance.getRange, alliance.getSpeed, alliance.getMaxAttackCoolDown, alliance.getMaxDeadCounter, alliance.getJsonPath, alliance.getImagePath, alliance.getRect, gp
    )