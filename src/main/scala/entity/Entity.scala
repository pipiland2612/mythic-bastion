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
  protected val apDmg: Double
  protected val adDmg: Double
  protected val range: Double
  protected val speed: Double

  private val transform = new AffineTransform()
  protected var currentAnimation: Option[Animation] = None
  protected var scaleFactor: Double = 1
  protected var needsAnimationUpdate: Boolean = false
  protected var state: State = State.IDLE
  protected var direction: Direction = Direction.RIGHT
  protected var images: Map[(Direction, State), Animation] = Map()

  protected var idleAnimation: Animation = _

  parse()
  setUpImages()

  def getName: String = name
  def getJsonPath: String = jsonPath
  def getImagePath: String = imagePath
  def getApDmg: Double = apDmg
  def getAdDmg: Double = adDmg
  def getRange: Double = range
  def getSpeed: Double = speed

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
