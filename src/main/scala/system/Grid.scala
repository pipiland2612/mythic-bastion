package system

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

  def addEnemy(enemy: Enemy): Unit =
    enemyMap.getOrElseUpdate(enemy.getId, enemy)

  def removeEnemy(enemy: Enemy): Unit =
    // race condition:
    this.synchronized(
      enemyMap.remove(enemy.getId)
    )

  def getEnemies: Iterable[Enemy] = enemyMap.values

class Grid(gp: GamePanel):

  private val cellSize: Int = 128
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

  private def enemyCenterPos(enemy: Enemy): (Int, Int) =
    val pos = (enemy.attackBox.getCenterX, enemy.attackBox.getCenterY)
    val gridX = enemy.getPosition._1.toInt / cellSize
    val gridY = enemy.getPosition._2.toInt / cellSize
    (gridX, gridY)

  def remove(enemy: Enemy): Unit =
    val (gridX, gridY) = enemyCenterPos(enemy)
    if checkBounds(gridX, gridY) then
      cells(gridX)(gridY).removeEnemy(enemy)

  def updateEnemyPosition(enemy: Enemy, prevPos: (Int, Int)): Unit =

    val (newGridX, newGridY) = enemyCenterPos(enemy)
    val (prevX, prevY) = prevPos
    val oldGridX = prevX / cellSize
    val oldGridY = prevY / cellSize

    if (oldGridX != newGridX || oldGridY != newGridY) then
      if checkBounds(oldGridX, oldGridY) then
        cells(oldGridX)(oldGridY).removeEnemy(enemy)

      if checkBounds(newGridX, newGridY) then
        cells(newGridX)(newGridY).addEnemy(enemy)

  def scanForEnemiesInRange(tower: Tower): ListBuffer[Enemy] =
    val ellipse = tower.attackCircle
    val nearbyEnemies: ListBuffer[Enemy] = ListBuffer()

    val maxRect: Rectangle2D = Tools.getInnerRectangle(ellipse)

    val minX: Int = (maxRect.minX.toInt / cellSize)
    val maxX: Int = (maxRect.maxX.toInt / cellSize)
    val minY: Int = (maxRect.minY.toInt / cellSize)
    val maxY: Int = (maxRect.maxY.toInt / cellSize)

    for (i <- Math.max(0, minX) to Math.min(rows - 1, maxX)) do
      for (j <- Math.max(0, minY) to Math.min(cols - 1, maxY)) do
        cells(i)(j).getEnemies.foreach(enemy =>
          val pos = (enemy.attackBox.getCenterX, enemy.attackBox.getCenterY)
          if (ellipse.contains(pos._1, pos._2)) then
            nearbyEnemies += enemy
        )

    nearbyEnemies

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
