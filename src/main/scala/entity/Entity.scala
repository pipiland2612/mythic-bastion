package entity

import game.GamePanel
import utils.{Animation, Cache, Tools}

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

abstract class Entity(gp: GamePanel):
  var pos: (Double, Double)
  protected val name: String
  protected val jsonPath, imagePath: String

  private val transform = new AffineTransform()
  protected var currentAnimation: Option[Animation] = None
  protected var scaleFactor: Double = 1
  protected var needsAnimationUpdate: Boolean = false
  protected var images: Map[(Direction, State), Animation] = Map()

  protected var idleAnimation: Animation = _
  protected var state: State = State.IDLE
  protected var direction: Direction = Direction.RIGHT
  protected val range: Double
  val maxAttackCoolDown: Double
  var attackCoolDown: Double = 0
  
  def getRange: Double = range
  def getName: String = name
  def getJsonPath: String = jsonPath
  def getImagePath: String = imagePath
  def getCenterCoords: (Double, Double) = Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame)

  parse()
  setUpImages()

  def setUpImages(): Unit
  def parseInformation(value: Vector[Vector[BufferedImage]]): Unit

  def parse(): Unit =
    Cache.animationCached.get(this.name) match
      case Some(value) =>
        parseInformation(value)
      case _ =>
        Tools.parser(getJsonPath, getImagePath, scaleFactor) match
          case Some(value) =>
            Cache.animationCached += this.name -> value
            parseInformation(value)
          case _ => throw new Exception(s"Parsing error")

  def checkAnimationUpdate(): Unit =
    if(needsAnimationUpdate) then
      needsAnimationUpdate = false
      currentAnimation = images.get(this.direction, this.state)
      currentAnimation.foreach(animation => animation.update())

  def update(): Unit =
    checkAnimationUpdate()

  def draw(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform, pos)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform, pos)
