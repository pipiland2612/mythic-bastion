package utils

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import entity.creature.Creature

import java.awt.geom.AffineTransform
import java.awt.{Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.{File, InputStream}
import javax.imageio.ImageIO

object Tools:

  def flipImageHorizontally(image: BufferedImage): BufferedImage =
    val flippedImage = new BufferedImage(image.getWidth, image.getHeight, image.getType)
    val g2d: Graphics2D = flippedImage.createGraphics()
    val at = AffineTransform.getScaleInstance(-1, 1)
    at.translate(-image.getWidth, 0)
    g2d.drawImage(image, at, null)
    g2d.dispose()
    flippedImage

  def loadImage(path: String): BufferedImage =
    try
      val imageStream = getClass.getResourceAsStream(s"/images/$path")
      if (imageStream == null) throw new RuntimeException(s"Image not found: /images/$path")
      ImageIO.read(imageStream)
    catch
      case e: Exception =>
        throw new RuntimeException(s"Failed to load image at path: /$path with exception: $e")

  def loadImageCoords(x: Int, y: Int, w: Int, h: Int, path: String): BufferedImage =
    try
      val image = loadImage(path)
      image.getSubimage(x, y, w, h)
    catch
      case e: Exception =>
        throw new RuntimeException(s"Failed to load image at path: /$path with exception: $e")

  def scaleImage(origin: BufferedImage, width: Int, height: Int): BufferedImage =
    val resized = origin.getScaledInstance(width, height, Image.SCALE_DEFAULT)
    val scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g: Graphics2D = scaledImage.createGraphics()
    g.drawImage(resized, 0, 0, width, height, null)
    g.dispose()
    scaledImage

  //TODO: Implement this, to read data from JSON file, parse in enemy object
  def creatureParser(jsonPath: String, imagePath: String, creature: Creature): Unit =
    try
      val objectMapper: ObjectMapper = ObjectMapper()
      val root: JsonNode = objectMapper.readTree(getClass.getResourceAsStream(s"/images/$jsonPath"))
      val image = loadImage(imagePath)

      val mNode: JsonNode = root.path("mc").get(0)
      val map: Vector[(String, Int)] = Vector()
      mNode.path("labels").forEach(data =>
        val string: String = data.path("name").asText()
        val value: Int = data.path("frame").asInt()
        map :+ (string, value)
      )

      mNode.path("frames").forEach(data =>


      )

      val resNode: JsonNode = root.path("res")
      resNode.forEach(key =>
        val x: Int = key.path("x").asInt()
        val y: Int = key.path("y").asInt()
        val w: Int = key.path("w").asInt()
        val h: Int = key.path("h").asInt()
      )
    catch
      case e: Exception => e.printStackTrace()