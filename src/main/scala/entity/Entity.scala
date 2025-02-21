package entity

import entity.creature.Creature
import game.GamePanel
import utils.{Animation, Cache, Tools}

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

abstract class Entity(gp: GamePanel):
  val name: String
  val jsonPath, imagePath: String
  var pos: (Double, Double)
  val apDmg: Double
  val adDmg: Double
  val range: Double

  var scaleFactor: Double = 1
  var currentAnimation: Option[Animation] = None
  val transform = new AffineTransform()

  parse()

  def parseInformation(value: Vector[Vector[BufferedImage]]): Unit = {}

  def parse(): Unit =
    Cache.animationCached.get(this.name) match
      case Some(value) =>
        println(s"reusing for $name")
        parseInformation(value)
      case _ =>
        Tools.parser(jsonPath, imagePath, scaleFactor) match
          case Some(value) =>
            Cache.animationCached += this.name -> value
            parseInformation(value)
          case _ => throw new Exception(s"Parsing error")

  def update(): Unit = {}

  def draw(g2d: Graphics2D): Unit =
    currentAnimation.foreach(animation =>
      transform.setToTranslation(pos._1, pos._2)
      g2d.drawImage(animation.getCurrentFrame, transform, None.orNull)
    )
