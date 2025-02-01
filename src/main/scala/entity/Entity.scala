package entity

import entity.creature.Creature

abstract class Entity:

  var pos: (Int, Int)
  val apDmg: Int
  val adDmg: Int
  val range: Int

  def attack(creature: Creature): Unit = {}

  def update(): Unit = {}

  def draw(): Unit = {}