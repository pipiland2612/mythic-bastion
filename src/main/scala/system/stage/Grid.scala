package system.stage

import entity.Entity
import entity.creature.Creature
import entity.creature.alliance.Alliance
import entity.creature.enemy.Enemy
import entity.tower.Tower
import game.GamePanel
import utils.Tools

import java.awt.geom.Ellipse2D
import java.awt.{Color, Graphics2D}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class GridCell:
  private val enemyMap: mutable.HashMap[Int, Enemy] = mutable.HashMap()
  private val allianceMap: mutable.HashMap[Int, Alliance] = mutable.HashMap()

  def addCreature(creature: Creature): Unit =
    creature match
      case enemy: Enemy =>
        enemyMap.put(enemy.getId, enemy)
      case alliance: Alliance =>
        allianceMap.put(alliance.getId, alliance)
      case _ =>

  def removeCreature(creature: Creature): Unit =
    creature match
      case enemy: Enemy =>
        enemyMap.remove(enemy.getId)
      case alliance: Alliance =>
        allianceMap.remove(alliance.getId)
      case _ =>

  def getEnemies: Iterable[Enemy] = enemyMap.values
  def getAlliances: Iterable[Alliance] = allianceMap.values

class Grid(gp: GamePanel):
  private val cellSize: Int = 96
  private val rows: Int = gp.screenWidth / cellSize
  private val cols: Int = gp.screenHeight / cellSize
  private val cells: Array[Array[GridCell]] = initializeCells()

  private def initializeCells(): Array[Array[GridCell]] =
    val grid = Array.ofDim[GridCell](rows, cols)
    for i <- 0 until rows do
      for j <- 0 until cols do
        grid(i)(j) = new GridCell
    grid

  private def checkBounds(x: Int, y: Int): Boolean =
    x >= 0 && x < rows && y >= 0 && y < cols

  private def creatureCenterPos(creature: Creature): (Int, Int) =
    val pos = (creature.attackBox.getCenterX.toInt / cellSize, creature.attackBox.getCenterY.toInt / cellSize)
    (pos._1, pos._2)

  def reset(): Unit =
    for
      i <- 0 until rows
      j <- 0 until cols
    do
      cells(i)(j) = new GridCell

  def remove(creature: Creature): Unit =
    val (gridX, gridY) = creatureCenterPos(creature)
    if checkBounds(gridX, gridY) then
      cells(gridX)(gridY).removeCreature(creature)

  def updateCreaturePosition(creature: Creature, prevPos: (Int, Int)): Unit =
    val (newGridX, newGridY) = creatureCenterPos(creature)
    val (prevX, prevY) = (prevPos._1 / cellSize, prevPos._2 / cellSize)

    if newGridX != prevX || newGridY != prevY then
      if checkBounds(prevX, prevY) then
        cells(prevX)(prevY).removeCreature(creature)
      if checkBounds(newGridX, newGridY) then
        cells(newGridX)(newGridY).addCreature(creature)
    else if prevPos == (creature.attackBox.getCenterX.toInt, creature.attackBox.getCenterY.toInt) then
      if checkBounds(newGridX, newGridY) then
        cells(newGridX)(newGridY).addCreature(creature)

  private def scanForCreaturesInRange[T <: Creature](
    attacker: Entity,
    getCreatures: (Int, Int) => Iterable[T]
  ): ListBuffer[T] =
    val ellipse = attacker.attackCircle
    val nearbyCreatures: ListBuffer[T] = ListBuffer()
    val bounds = calculateScanBounds(ellipse)

    for i <- bounds.minX to bounds.maxX do
      for j <- bounds.minY to bounds.maxY do
        getCreatures(i, j).foreach(creature =>
          val pos = (creature.attackBox.getCenterX, creature.attackBox.getCenterY)
          if ellipse.contains(pos._1, pos._2) then
            nearbyCreatures += creature
        )
    nearbyCreatures

  private def calculateScanBounds(ellipse: Ellipse2D): ScanBounds =
    val maxRect = Tools.getInnerRectangle(ellipse)
    ScanBounds(
      minX = (maxRect.getX.toInt / cellSize).max(0),
      maxX = (maxRect.getMaxX.toInt / cellSize).min(rows - 1),
      minY = (maxRect.getY.toInt / cellSize).max(0),
      maxY = (maxRect.getMaxY.toInt / cellSize).min(cols - 1)
    )

  def scanForEnemiesInRange(attacker: Entity): ListBuffer[Enemy] =
    scanForCreaturesInRange(attacker, (x, y) => cells(x)(y).getEnemies)

  def scanForAlliancesInRange(enemy: Enemy): ListBuffer[Alliance] =
    scanForCreaturesInRange(enemy, (x, y) => cells(x)(y).getAlliances)

  def draw(g: Graphics2D, tower: Tower): Unit =
    drawGridLines(g)
    drawAttackRange(g, tower.attackCircle)

  private def drawGridLines(g: Graphics2D): Unit =
    g.setColor(Color.GRAY)
    for i <- 0 until rows do
      for j <- 0 until cols do
        g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize)

  private def drawAttackRange(g: Graphics2D, ellipse: Ellipse2D): Unit =
    g.setColor(Color.BLUE)
    g.drawOval(
      (ellipse.getCenterX - ellipse.getWidth / 2).toInt,
      (ellipse.getCenterY - ellipse.getHeight / 2).toInt,
      ellipse.getWidth.toInt,
      ellipse.getHeight.toInt
    )
    val bounds = calculateScanBounds(ellipse)
    g.setColor(new Color(0, 0, 255, 50))
    for i <- bounds.minX to bounds.maxX do
      for j <- bounds.minY to bounds.maxY do
        g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize)

private case class ScanBounds(minX: Int, maxX: Int, minY: Int, maxY: Int)