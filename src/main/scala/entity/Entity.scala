package entity

import game.GamePanel
import utils.{Animation, Cache, Tools}

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

abstract class Entity(gp: GamePanel):
  protected var pos: (Double, Double)
  protected val name: String
  protected val jsonPath, imagePath: String

  protected var currentAnimation: Option[Animation] = None
  protected var scaleFactor: Double = 1
  protected var images: Map[(Direction, State), Animation] = Map()

  protected var state: State = State.IDLE
  protected var direction: Direction = Direction.RIGHT
  protected var idleAnimation: Animation = _

  protected val range: Double
  protected val maxAttackCoolDown: Double
  protected var attackCoolDown: Double = 0
  var needsAnimationUpdate: Boolean = false

  private val transform = new AffineTransform()

  def getRange: Double = range
  def getName: String = name
  def getJsonPath: String = jsonPath
  def getImagePath: String = imagePath
  def getCenterCoords: (Double, Double) = Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame)
  def getMaxAttackCoolDown: Double = maxAttackCoolDown
  def getPosition: (Double, Double) = pos
  def setPosition(pos: (Double, Double)): Unit = this.pos = pos
  def setState(state: State) = this.state = state

  parse()
  setUpImages()

  protected def setUpImages(): Unit
  protected def parseInformation(value: Vector[Vector[BufferedImage]]): Unit

  protected def parse(): Unit =
    Cache.animationCached.get(this.name) match
      case Some(value) =>
        parseInformation(value)
      case _ =>
        Tools.parser(getJsonPath, getImagePath, scaleFactor) match
          case Some(value) =>
            Cache.animationCached += this.name -> value
            parseInformation(value)
          case _ => throw new Exception(s"Parsing error")

  protected def checkAnimationUpdate(): Unit =
    if(needsAnimationUpdate) then
      needsAnimationUpdate = false
      currentAnimation = images.get(this.direction, this.state)
      currentAnimation.foreach(_.update())

  def update(): Unit =
    checkAnimationUpdate()

  def draw(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform, pos)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform, pos)
