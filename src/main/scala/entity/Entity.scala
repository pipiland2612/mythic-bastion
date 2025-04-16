package entity

import game.GamePanel
import utils.{Animation, Cache, Tools}

import java.awt.Graphics2D
import java.awt.geom.{AffineTransform, Ellipse2D}
import java.awt.image.BufferedImage

/** Abstract base class for game entities, providing core functionality for rendering, animation, and positioning.
 * @param gp The GamePanel instance managing the game.
 */
abstract class Entity(gp: GamePanel):
  private val id = Entity.nextId()
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
  protected var needsAnimationUpdate: Boolean = false
  private val transform = new AffineTransform()

  /** Returns the attack range circle for the entity.
   * @return An Ellipse2D representing the entity's attack range.
   */
  def attackCircle: Ellipse2D
  def getRange: Double = range
  def getName: String = name
  def getJsonPath: String = jsonPath
  def getImagePath: String = imagePath
  def getCenterCoords: (Double, Double) = Tools.getCenterCoords(pos, idleAnimation.getCurrentFrame)
  def getMaxAttackCoolDown: Double = maxAttackCoolDown
  def getPosition: (Double, Double) = pos
  def getState: State = state
  def setPosition(pos: (Double, Double)): Unit = this.pos = pos
  def setState(state: State): Unit = this.state = state
  def getId: Int = id

  parse()
  setUpImages()

  /** Sets up the entity's animation map, to be implemented by subclasses. */
  protected def setUpImages(): Unit

  /** Parses animation frames for the entity, to be implemented by subclasses.
   * @param value A vector of vectors containing BufferedImage frames for animations.
   */
  protected def parseInformation(value: Vector[Vector[BufferedImage]]): Unit

  /** Loads and parses animation data from a cache or file system, caching the result if necessary. */
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
    if needsAnimationUpdate then
      needsAnimationUpdate = false
      currentAnimation = images.get(this.direction, this.state)
      currentAnimation.foreach(_.update())

  def update(): Unit =
    checkAnimationUpdate()

  /** Renders the entity using its current animation or idle animation as a fallback.
   * @param g2d The Graphics2D context for rendering.
   */
  def draw(g2d: Graphics2D): Unit =
    currentAnimation match
      case Some(animation) =>
        Tools.drawFrame(g2d, animation.getCurrentFrame, transform, pos)
      case _ =>
        Tools.drawFrame(g2d, idleAnimation.getCurrentFrame, transform, pos)

  /** Generates a hash code based on the entity's unique ID.
   * @return The hash code.
   */
  override def hashCode(): Int = id.hashCode()

  /** Checks equality with another object based on the entity's unique ID.
   * @param obj The object to compare with.
   * @return True if the objects are equal, false otherwise.
   */
  override def equals(obj: Any): Boolean = obj match
    case other: Entity => this.id == other.id
    case _ => false

/** Companion object for Entity, managing unique ID generation. */
object Entity:
  private var idCounter: Int = 0

  /** Generates the next unique ID for a new entity.
   * @return The next available ID.
   */
  private def nextId(): Int =
    idCounter += 1
    idCounter