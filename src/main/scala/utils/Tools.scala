package utils

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}

import java.awt.geom.AffineTransform
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

case class ImageProb(val x: Int, val y: Int, val w: Int, val h: Int)

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
        
  def loadJson(path: String): JsonNode =
    try
      val objectMapper: ObjectMapper = ObjectMapper()
      val root: JsonNode = objectMapper.readTree(getClass.getResourceAsStream(s"/json/$path"))
      if (root == null) throw new RuntimeException(s"Json not found: /json/$path")
      root
    catch
      case e: Exception =>
        throw new RuntimeException(s"Failed to load json file at path: /$path with exception: $e")
  
  def loadImageCoords(x: Int, y: Int, w: Int, h: Int, path: String): BufferedImage =
    try
      val image = loadImage(path)
      image.getSubimage(x, y, w, h)
    catch
      case e: Exception =>
        throw new RuntimeException(s"Failed to load image at path: /$path with exception: $e")

  def scaleImage(origin: BufferedImage, scaleX: Int, scaleY: Int): BufferedImage =
    val newWidth = origin.getWidth() * scaleX
    val newHeight = origin.getHeight() * scaleY

    val scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g: Graphics2D = scaledImage.createGraphics()
    g.drawImage(origin, 0, 0, newWidth, newHeight, null)
    g.dispose()
    scaledImage

  def parser(jsonPath: String, imagePath: String, scaleFactor: Int): Option[Vector[Vector[BufferedImage]]] =
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
          val map: Vector[(String, Int)] = Vector()
          val ref: ListBuffer[Int] = ListBuffer()
          mNode.path("labels").forEach(data =>
            val string: String = data.path("name").asText()
            val value: Int = data.path("frame").asInt()
            map :+ (string, value)
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
        throw new Exception(s"Failed to parse the file at $jsonPath and $imagePath")