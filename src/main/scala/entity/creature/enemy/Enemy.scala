package entity.creature.enemy

import entity.creature.Creature
import utils.Tools

import java.awt.Graphics2D
import java.awt.image.BufferedImage

abstract class Enemy extends Creature:

  var walkingFrames: Vector[BufferedImage] = Vector()
  var walkingUpFrames: Vector[BufferedImage] = Vector()
  var walkingDownFrames: Vector[BufferedImage] = Vector()
  var idleFrames: Vector[BufferedImage] = Vector()
  var fightingFrames: Vector[BufferedImage] = Vector()
  var deadFrames: Vector[BufferedImage] = Vector()

  val jsonPath, imagePath: String
  def enemyParse(): Unit =
    Tools.parser(jsonPath, imagePath) match
      case Some(value) =>
        walkingFrames = value(0)
        walkingUpFrames = value(1)
        walkingDownFrames = value(2)
        idleFrames = value(3)
        fightingFrames = value(4)
        deadFrames = value(5)
      case _ =>

  val playerDamage: Int

  def attackPlayer(): Unit = {}

  override def draw(g2d: Graphics2D): Unit =
    super.draw(g2d)

end Enemy

object Enemy:

  def enemyOfName(key: String, difficulty: Int): Option[Enemy] =
    var initialData: Vector[Int] = Vector()
    var jsonData, imageData: String = ""
    key match
      case Monster01.name =>
        initialData = Monster01.data
        jsonData = Monster01.jsonPath
        imageData = Monster01.imagePath
      case _ => return None
    val data: Vector[Int] = initialData.map(element => element * difficulty)
    Some(Creep(data(0), data(1), data(2), data(3), data(4), data(5), jsonData, imageData))
