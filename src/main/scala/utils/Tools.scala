package utils

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import entity.{Direction, State}
import entity.creature.enemy.Enemy
import entity.tower.TowerBuild
import game.GamePanel
import system.stage.{EnemyData, GameMap, Stage, Wave}

import java.awt.geom.{AffineTransform, Ellipse2D, Rectangle2D}
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/** Represents coordinates and dimensions for cropping an image.
 *
 * @param x The x-coordinate of the top-left corner.
 * @param y The y-coordinate of the top-left corner.
 * @param w The width of the image crop.
 * @param h The height of the image crop.
 */
case class ImageProb(x: Int, y: Int, w: Int, h: Int)

/** A utility object providing helper methods for image processing, JSON parsing, stage loading, and geometric calculations.
 *
 * This object is initialized with a GamePanel instance and provides functionality for loading and manipulating images,
 * parsing JSON data, loading game stages, and performing various geometric operations used throughout the game.
 */
object Tools:
  private var gp: GamePanel = _

  /** Initializes the Tools object with a GamePanel instance.
   *
   * @param gp The GamePanel instance to be used by utility methods.
   */
  def setUp(gp: GamePanel): Unit = this.gp = gp

  /** Flips an image horizontally.
   *
   * Creates a new BufferedImage with the input image mirrored along the vertical axis.
   *
   * @param image The BufferedImage to flip.
   * @return A new BufferedImage that is horizontally flipped.
   */
  def flipImageHorizontally(image: BufferedImage): BufferedImage =
    val flippedImage = new BufferedImage(image.getWidth, image.getHeight, image.getType)
    val g2d: Graphics2D = flippedImage.createGraphics()
    val at = AffineTransform.getScaleInstance(-1, 1)
    at.translate(-image.getWidth, 0)
    g2d.drawImage(image, at, None.orNull)
    g2d.dispose()
    flippedImage

  /** Calculates the top-left coordinates to center an image at the given coordinates with optional offsets.
   *
   * @param coords The (x, y) coordinates where the image should be centered.
   * @param image The BufferedImage to be centered.
   * @param offsetX Optional x-coordinate offset.
   * @param offsetY Optional y-coordinate offset.
   * @return A tuple of (x, y) coordinates representing the top-left corner of the centered image.
   */
  def getCenterCoords(coords: (Double, Double), image: BufferedImage, offsetX: Int = 0, offsetY: Int = 0): (Double, Double) =
    (coords._1 - image.getWidth()/2 + offsetX, coords._2 - image.getHeight()/2 + offsetY)

  /** Flips an animation's frames horizontally.
   *
   * @param animation The Animation to flip.
   * @return A new Animation with all frames flipped horizontally.
   */
  def flipAnimation(animation: Animation): Animation =
    val vector: Vector[BufferedImage] =
      for image <- animation.frames
      yield flipImageHorizontally(image)
    Animation(vector, animation.frameDuration)

  /** Loads an image from the specified resource path.
   *
   * @param path The path to the image file (relative to /images/).
   * @return The loaded BufferedImage.
   * @throws RuntimeException If the image cannot be found or loaded.
   */
  def loadImage(path: String): BufferedImage =
    try
      val imageStream = getClass.getResourceAsStream(s"/images/$path")
      if (Option(imageStream).isEmpty) throw new RuntimeException(s"Image not found: /images/$path") // this is same as: if (imageStream == null)
      ImageIO.read(imageStream)
    catch
      case e: Exception =>
        throw new RuntimeException(s"Failed to load image at path: /$path with exception: $e")

  /** Loads a JSON file from the specified resource path.
   *
   * @param path The path to the JSON file (relative to /json/).
   * @return The parsed JsonNode.
   * @throws RuntimeException If the JSON file cannot be found or parsed.
   */
  def loadJson(path: String): JsonNode =
    try
      val objectMapper: ObjectMapper = ObjectMapper()
      val root: JsonNode = objectMapper.readTree(getClass.getResourceAsStream(s"/json/$path"))
      if (Option(root).isEmpty) throw new RuntimeException(s"Json not found: /json/$path")
      root
    catch
      case e: Exception =>
        throw new RuntimeException(s"Failed to load json file at path: /$path with exception: $e")

  /** Loads and crops an image from the specified resource path.
   *
   * @param x The x-coordinate of the top-left corner of the crop.
   * @param y The y-coordinate of the top-left corner of the crop.
   * @param w The width of the crop.
   * @param h The height of the crop.
   * @param path The path to the image file (relative to /images/).
   * @return The cropped BufferedImage.
   * @throws RuntimeException If the image cannot be loaded or cropped.
   */
  def loadImageCoords(x: Int, y: Int, w: Int, h: Int, path: String): BufferedImage =
    try
      val image = loadImage(path)
      image.getSubimage(x, y, w, h)
    catch
      case e: Exception =>
        throw new RuntimeException(s"Failed to load image at path: /$path with exception: $e")

  /** Scales an image to the specified dimensions.
   *
   * @param origin The original BufferedImage to scale.
   * @param newWidth The desired width of the scaled image.
   * @param newHeight The desired height of the scaled image.
   * @return The scaled BufferedImage.
   */
  def scaleImage(origin: BufferedImage, newWidth: Int, newHeight: Int): BufferedImage =
    val scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g: Graphics2D = scaledImage.createGraphics()
    g.drawImage(origin, 0, 0, newWidth, newHeight, None.orNull)
    g.dispose()
    scaledImage

  /** Scales an image by the specified scale factors.
   *
   * @param origin The original BufferedImage to scale.
   * @param scaleX The scale factor for the width.
   * @param scaleY The scale factor for the height.
   * @return The scaled BufferedImage.
   */
  def scaleImage(origin: BufferedImage, scaleX: Double, scaleY: Double): BufferedImage =
    scaleImage(origin, (origin.getWidth * scaleX).toInt, (origin.getHeight * scaleY).toInt)

  /** Loads a game stage from a JSON configuration file.
   *
   * Parses the JSON file to extract stage details, including name, ID, difficulty, coins, spawn positions,
   * waves, and map data (paths and tower positions). Constructs a Stage instance with the parsed data.
   *
   * @param jsonPath The path to the JSON file (relative to /json/).
   * @return The constructed Stage instance.
   * @throws Exception If the stage cannot be loaded or parsed.
   */
  def loadStage(jsonPath: String): Stage =
    try
      val root: JsonNode = loadJson(jsonPath)
      val stageName: String = root.path("stageName").asText()
      val stageID: Int = root.path("stageID").asInt()
      val difficulty: Int = root.path("difficulty").asInt()
      val coins: Int = root.path("coins").asInt()
      var waves: Vector[Wave] = Vector()
      val spawnPosition: Vector[(Double, Double)] = getPosition(root.path("spawnPosition"))

      val mapPath: JsonNode = root.path("map")
      val towerPos: Vector[(Double,Double)] = getPosition(mapPath.path("towerSpots"))
      val towerImage: BufferedImage = Tools.loadImage(s"build/${mapPath.path("towerImage").asText()}.png")
      val towerBuilds: Vector[TowerBuild] = towerPos.map(TowerBuild(gp, _, towerImage))
      var path: Vector[Vector[(Double,Double)]] = Vector()
      mapPath.path("path").forEach(data =>
        val pos: Vector[(Double,Double)] = getPosition(data)
        path = path :+ pos
      )

      val map: GameMap = GameMap(path, towerBuilds)

      root.path("waves").forEach(data =>
        val delay: Int = data.path("delay").asInt()
        var currentWave: Vector[EnemyData] = Vector()
        data.path("enemies").forEach(enemyData =>
          val enemyType: Option[Enemy] = Enemy.enemyOfName(enemyData.path("type").asText(), difficulty)
          enemyType match
            case Some(enemy) =>
              val count: Int = enemyData.path("count").asInt()
              val spawnInterval: Double = enemyData.path("spawnInterval").asDouble()
              val spawnIndex: Int = enemyData.path("spawnIndex").asInt()
              currentWave = currentWave :+ EnemyData(enemy, count, spawnInterval, spawnIndex)
            case _ =>
        )
        val wave: Wave = Wave(delay, currentWave)
        waves = waves :+ wave
      )

      Stage(gp, stageName, stageID, difficulty, coins, spawnPosition, waves, map)

    catch
      case e: Exception =>
        throw new Exception(s"Failed to load stage at path $jsonPath with exeption ${e.printStackTrace()}")

  /** Extracts position coordinates from a JSON node.
   *
   * @param node The JsonNode containing an array of position objects with x and y fields.
   * @return A Vector of (x, y) coordinate tuples.
   */
  private def getPosition(node: JsonNode): Vector[(Double, Double)] =
    var storage: Vector[(Double, Double)] = Vector()
    node.forEach(data =>
      val x: Double = data.path("x").asDouble()
      val y: Double = data.path("y").asDouble()
      storage = storage :+ (x,y)
    )
    storage

  /** Parses a JSON file and image to create a vector of animation frames.
   *
   * Reads a JSON configuration to map image regions to animation frames, crops the specified regions from
   * the provided image, scales them, and organizes them into animation sequences.
   *
   * @param jsonPath The path to the JSON file (relative to /json/).
   * @param imagePath The path to the image file (relative to /images/).
   * @param scaleFactor The factor by which to scale the cropped images.
   * @return An Option containing a Vector of animation frame sequences, or None if parsing fails.
   * @throws Exception If the JSON or image cannot be parsed or loaded.
   */
  def parser(jsonPath: String, imagePath: String, scaleFactor: Double): Option[Vector[Vector[BufferedImage]]] =
    try
      val root: JsonNode = loadJson(jsonPath)
      val image: BufferedImage = loadImage(imagePath)
      var animation: Vector[Vector[BufferedImage]] = Vector()

      val resNode: JsonNode = root.path("res")
      val resToPos: mutable.Map[String, ImageProb] = mutable.Map()

      resNode.fields().forEachRemaining ( entry =>
        val key = entry.getKey
        val node = entry.getValue

        val x = node.path("x").asInt()
        val y = node.path("y").asInt()
        val w = node.path("w").asInt()
        val h = node.path("h").asInt()

        resToPos += (key -> ImageProb(x, y, w, h))
      )

      root.path("mc").fieldNames().asScala.take(1).toList.headOption match
        case Some(name) =>
          val mNode: JsonNode = root.path("mc").path(name)
          val ref: ListBuffer[Int] = ListBuffer()
          mNode.path("labels").forEach(data =>
            val string: String = data.path("name").asText()
            val value: Int = data.path("frame").asInt()
            ref += value
          )
          var currSum: Int = 0
          val frames = mNode.path("frames")
          var index: Int = 0
          var temp: Vector[BufferedImage] = Vector()

          frames.forEach(data =>
            var value: Int = data.path("duration").asInt()
            if (value == 0) value = 1
            currSum += value

            resToPos.get(data.path("res").asText()) match
              case Some(value) =>
                val currImage: BufferedImage = image.getSubimage(value.x, value.y, value.w, value.h)
                val scaledImage: BufferedImage = Tools.scaleImage(currImage, scaleFactor, scaleFactor)
                temp = temp :+ scaledImage
              case None =>
            if index + 1 < ref.length && (currSum + ref(index) >= ref(index + 1)) then
              animation = animation :+ temp
              index += 1
              temp = Vector()
              currSum = 0
          )
          animation = animation :+ temp
          Some(animation)
        case None => None
    catch
      case e: Exception =>
        throw new Exception(s"Failed to parse the file at $jsonPath and $imagePath with exception $e")

  /** Creates a mapping of direction and state to an animation.
   *
   * @param directions The sequence of Directions to map.
   * @param state The State to map.
   * @param animation The Animation to associate with each (Direction, State) pair.
   * @return A Map of (Direction, State) to Animation.
   */
  def fillMap(directions: Seq[Direction], state: State, animation: Animation): Map[(Direction, State), Animation] =
    directions.map(dir => (dir, state) -> animation).toMap

  /** Draws an image frame at the specified coordinates with an optional offset.
   *
   * @param g2d The Graphics2D context for rendering.
   * @param frame The BufferedImage frame to draw.
   * @param transform The AffineTransform to apply.
   * @param coords The (x, y) coordinates where the frame should be drawn.
   * @param offsetX Optional x-coordinate offset.
   * @param offsetY Optional y-coordinate offset.
   */
  def drawFrame(g2d: Graphics2D, frame: BufferedImage, transform: AffineTransform, coords: (Double, Double), offsetX: Double = 0, offsetY: Double = 0): Unit =
    transform.setToTranslation(coords._1 + offsetX, coords._2 + offsetY)
    g2d.drawImage(frame, transform, None.orNull)

  /** Calculates the angle between two points in degrees.
   *
   * @param pos The starting point (x, y).
   * @param goal The target point (x, y).
   * @return The angle in degrees from pos to goal.
   */
  def getAngle(pos: (Double, Double), goal: (Double, Double)): Double =
    Math.toDegrees(Math.atan2(goal._2-pos._2, goal._1-pos._1))

  /** Calculates a point on a quadratic Bezier curve.
   *
   * @param t The parameter (0 to 1) along the curve.
   * @param p0 The starting point (x, y).
   * @param p1 The control point (x, y).
   * @param p2 The ending point (x, y).
   * @return The (x, y) coordinates of the point on the Bezier curve.
   */
  def bezier(t: Double, p0: (Double, Double), p1: (Double, Double), p2: (Double, Double)): (Double, Double) =
    val x = (1 - t) * (1 - t) * p0._1 + 2 * (1 - t) * t * p1._1 + t * t * p2._1
    val y = (1 - t) * (1 - t) * p0._2 + 2 * (1 - t) * t * p1._2 + t * t * p2._2
    (x, y)

  /** Calculates the inner rectangle of an ellipse.
   *
   * Computes a rectangle that fits inside the ellipse, with dimensions scaled by sqrt(2) to ensure it is fully contained.
   *
   * @param ellipse The Ellipse2D to compute the inner rectangle for.
   * @return The inner Rectangle2D.
   */
  def getInnerRectangle(ellipse: Ellipse2D): Rectangle2D =
    val cx = ellipse.getCenterX
    val cy = ellipse.getCenterY
    val rx = ellipse.getWidth / 2
    val ry = ellipse.getHeight / 2
    val width = rx * Math.sqrt(2)
    val height = ry * Math.sqrt(2)
    Rectangle2D.Double(cx - width / 2, cy - height / 2, width, height)

  /** Calculates the Euclidean distance between two points.
   *
   * @param end The ending point (x, y).
   * @param start The starting point (x, y).
   * @return The distance between the points.
   */
  def distance(end: (Double, Double), start: (Double, Double)): Double =
    Math.sqrt(Math.pow(end._1 - start._1, 2) + Math.pow(end._2 - start._2, 2))

  /** Creates a Rectangle2D for an image at the specified coordinates.
   *
   * @param coords The (x, y) coordinates of the top-left corner.
   * @param image The BufferedImage whose dimensions define the rectangle.
   * @return The Rectangle2D representing the image's bounds.
   */
  def getRectInRange(coords: (Int, Int), image: BufferedImage): Rectangle2D =
    Rectangle2D.Double(coords._1, coords._2, image.getWidth, image.getHeight)

  /** Converts an image to grayscale while preserving transparency.
   *
   * Applies a weighted grayscale conversion (using standard luminance coefficients) to each pixel while maintaining the alpha channel.
   *
   * @param img The BufferedImage to convert.
   * @return A new BufferedImage in grayscale.
   */
  def applyGrayscale(img: BufferedImage): BufferedImage =
    val width = img.getWidth
    val height = img.getHeight
    val grayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
  
    for (x <- 0 until width; y <- 0 until height) do
      val rgba = img.getRGB(x, y)
      val alpha = (rgba >> 24) & 0xFF
      val red = (rgba >> 16) & 0xFF
      val green = (rgba >> 8) & 0xFF
      val blue = rgba & 0xFF
      val gray = (red * 0.299 + green * 0.587 + blue * 0.114).toInt
      val grayColor = (alpha << 24) | (gray << 16) | (gray << 8) | gray
      grayImage.setRGB(x, y, grayColor)

    grayImage