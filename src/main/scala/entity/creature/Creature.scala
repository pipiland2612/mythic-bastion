package entity.creature

import entity.Entity

import java.awt.image.BufferedImage

abstract class Creature extends Entity:
  val maxHealth: Int
  var health: Int

  val walkingFrames: Vector[BufferedImage] = Vector()
  val walkingUpFrames: Vector[BufferedImage] = Vector()
  val walkingDownFrames: Vector[BufferedImage] = Vector()
  val idleFrames: Vector[BufferedImage] = Vector()
  val fightingFrames: Vector[BufferedImage] = Vector()
  val deadFrames: Vector[BufferedImage] = Vector()