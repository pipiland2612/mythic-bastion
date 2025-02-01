package entity

import entity.creature.Creature

import java.awt.Graphics2D

abstract class Entity:

  var pos: (Int, Int)
  val apDmg: Int
  val adDmg: Int
  val range: Int

  def attack(creature: Creature): Unit = {}

  def update(): Unit = {}

  // TODO: Implement this
  def draw(g2d: Graphics2D): Unit = {}