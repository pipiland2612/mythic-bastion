package system

import entity.Entity
import entity.creature.Creature
import entity.creature.alliance.Alliance
import entity.creature.enemy.Enemy
import entity.tower.Tower
import game.GamePanel
import scalafx.geometry.Rectangle2D
import utils.Tools

import java.awt.{Color, Graphics2D}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class GridCell:
  private val enemyMap: mutable.HashMap[Int, Enemy] = mutable.HashMap()
  private val allianceMap: mutable.HashMap[Int, Alliance] = mutable.HashMap()

  def addCreature(creature: Creature): Unit =
    creature match
      case enemy: Enemy =>
        enemyMap.getOrElseUpdate(enemy.getId, enemy)
      case alliance: Alliance =>
        allianceMap.getOrElseUpdate(alliance.getId, alliance)
      case _ =>

  def removeCreature(creature: Creature): Unit =
    creature match
      case enemy: Enemy =>
        enemyMap.remove(enemy.getId)
      case alliance: Alliance =>
        allianceMap.remove(alliance.getId)
      case _ =>

  def getEnemies: Iterable[Enemy] = enemyMap.values
  def getAlliance: Iterable[Alliance] = allianceMap.values

class Grid(gp: GamePanel):

  private val cellSize: Int = 64
  private val rows: Int = gp.screenWidth / cellSize
  private val cols: Int = gp.screenHeight / cellSize
  private val cells: Array[Array[GridCell]] = Array.ofDim[GridCell](rows, cols)

  initialise()
  private def initialise(): Unit =
    for (i <- 0 until rows) do
      for (j <- 0 until cols) do
        cells(i)(j) = new GridCell

  private def checkBounds(x: Int, y: Int): Boolean =
    x >= 0 && x < rows && y >= 0 && y < cols

  private def creatureCenterPos(creature: Creature): (Int, Int) =
    val pos = (creature.attackBox.getCenterX, creature.attackBox.getCenterY)
    val gridX = pos._1.toInt / cellSize
    val gridY = pos._2.toInt / cellSize
    (gridX, gridY)

  def remove(creature: Creature): Unit =
    val (gridX, gridY) = creatureCenterPos(creature)
    if checkBounds(gridX, gridY) then
      cells(gridX)(gridY).removeCreature(creature) // Rename to a more general method

  def updateCreaturePosition(creature: Creature, prevPos: (Int, Int)): Unit =
    val (newGridX, newGridY) = creatureCenterPos(creature)
    val (prevX, prevY) = prevPos
    val oldGridX = prevX / cellSize
    val oldGridY = prevY / cellSize

    if (oldGridX != newGridX || oldGridY != newGridY) then
      if checkBounds(oldGridX, oldGridY) then
        cells(oldGridX)(oldGridY).removeCreature(creature)

      if checkBounds(newGridX, newGridY) then
        cells(newGridX)(newGridY).addCreature(creature) // Rename to a more general method
    else if prevX == creature.attackBox.getCenterX && prevY == creature.attackBox.getCenterY then
      if checkBounds(oldGridX, oldGridY) then
        cells(oldGridX)(oldGridY).addCreature(creature)

  private def scanForCreaturesInRange[T <: Creature](
    attacker: Entity,
    getCreatures: (Int, Int) => Iterable[T]
  ): ListBuffer[T] =
    val ellipse = attacker.attackCircle
    val nearbyCreatures: ListBuffer[T] = ListBuffer()
    val maxRect: Rectangle2D = Tools.getInnerRectangle(ellipse)

    val minX = (maxRect.minX.toInt / cellSize).max(0)
    val maxX = (maxRect.maxX.toInt / cellSize).min(rows - 1)
    val minY = (maxRect.minY.toInt / cellSize).max(0)
    val maxY = (maxRect.maxY.toInt / cellSize).min(cols - 1)

    for i <- minX to maxX do
      for j <- minY to maxY do
        getCreatures(i, j).foreach ( creature =>
          val pos = (creature.attackBox.getCenterX, creature.attackBox.getCenterY)
          if ellipse.contains(pos._1, pos._2) then
            nearbyCreatures += creature
        )

    nearbyCreatures

  def scanForEnemiesInRange(attacker: Entity): ListBuffer[Enemy] =
    scanForCreaturesInRange(attacker, (x, y) => cells(x)(y).getEnemies)

  def scanForAlliancesInRange(enemy: Enemy): ListBuffer[Alliance] =
    scanForCreaturesInRange(enemy, (x, y) => cells(x)(y).getAlliance)

  def draw(g: Graphics2D, tower: Tower): Unit =
    g.setColor(Color.GRAY)

    for (i <- 0 until rows) do
      for (j <- 0 until cols) do
        g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize)

    val ellipse = tower.attackCircle
    g.setColor(Color.BLUE)
    g.drawOval(
      (ellipse.getCenterX - ellipse.getWidth / 2).toInt,
      (ellipse.getCenterY - ellipse.getHeight / 2).toInt,
      ellipse.getWidth.toInt,
      ellipse.getHeight.toInt
    )

    val maxRect: Rectangle2D = Tools.getInnerRectangle(ellipse)
    val minX: Int = (maxRect.minX.toInt / cellSize)
    val maxX: Int = (maxRect.maxX.toInt / cellSize)
    val minY: Int = (maxRect.minY.toInt / cellSize)
    val maxY: Int = (maxRect.maxY.toInt / cellSize)

    g.setColor(new Color(0, 0, 255, 50))

    for (i <- Math.max(0, minX) to Math.min(rows - 1, maxX)) do
      for (j <- Math.max(0, minY) to Math.min(cols - 1, maxY)) do
        // Draw the checked cells with a transparent fill
        g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize)
