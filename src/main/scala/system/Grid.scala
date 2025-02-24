package system

import entity.creature.enemy.Enemy
import entity.tower.Tower
import game.GamePanel
import scalafx.geometry.Rectangle2D
import utils.Tools

import java.awt.{Color, Graphics2D}
import scala.collection.mutable.ListBuffer

class GridCell:
  private val enemyList: ListBuffer[Enemy] = ListBuffer()

  def addEnemy(enemy: Enemy) = enemyList += enemy
  def getList = enemyList

class Grid(gp: GamePanel):

  private val cellSize: Int = 96
  private val rows: Int = gp.screenWidth / cellSize
  private val cols: Int = gp.screenHeight / cellSize
  val cells: Array[Array[GridCell]] = Array.ofDim[GridCell](rows, cols)

  initialise()
  def initialise(): Unit =
    for (i <- 0 until rows) do
      for (j <- 0 until cols) do
        cells(i)(j) = new GridCell

  def reset(): Unit =
    for (i <- 0 until rows) do
      for (j <- 0 until cols) do
        cells(i)(j).getList.clear()

  def addEnemy(enemy: Enemy): Unit =
    val pos = (enemy.attackBox.getCenterX, enemy.attackBox.getCenterY)
    val gridX = enemy.pos._1.toInt / cellSize
    val gridY = enemy.pos._2.toInt / cellSize
    if (gridX >= 0 && gridX < rows && gridY >= 0 && gridY < cols) then
      cells(gridX)(gridY).addEnemy(enemy)

  def scanForEnemiesInRange(tower: Tower): ListBuffer[Enemy] =
    val ellipse = tower.attackCircle
    val nearbyEnemies: ListBuffer[Enemy] = ListBuffer()

    val maxRect: Rectangle2D = Tools.getInnerRectangle(ellipse)

    val minX: Int = (maxRect.minX.toInt / cellSize) - 1
    val maxX: Int = (maxRect.maxX.toInt / cellSize) + 1
    val minY: Int = (maxRect.minY.toInt / cellSize)
    val maxY: Int = (maxRect.maxY.toInt / cellSize)

    for (i <- Math.max(0, minX) to Math.min(rows - 1, maxX)) do
      for (j <- Math.max(0, minY) to Math.min(cols - 1, maxY)) do
        cells(i)(j).getList.foreach(enemy =>
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

    for (i <- 0 until rows) do
      for (j <- 0 until cols) do
        cells(i)(j).getList.toList.foreach ( enemy =>
          g.setColor(Color.RED)
          val pos = (enemy.attackBox.getCenterX, enemy.attackBox.getCenterY)
          g.fillOval(pos._1.toInt - i * cellSize, pos._2.toInt - j * cellSize, 8, 8)
        )

    val ellipse = tower.attackCircle
    g.setColor(Color.BLUE)
    g.drawOval(
      (ellipse.getCenterX - ellipse.getWidth / 2).toInt,
      (ellipse.getCenterY - ellipse.getHeight / 2).toInt,
      ellipse.getWidth.toInt,
      ellipse.getHeight.toInt
    )

    val maxRect: Rectangle2D = Tools.getInnerRectangle(ellipse)
    val minX: Int = (maxRect.minX.toInt / cellSize) - 1
    val maxX: Int = (maxRect.maxX.toInt / cellSize) + 1
    val minY: Int = (maxRect.minY.toInt / cellSize)
    val maxY: Int = (maxRect.maxY.toInt / cellSize)

    g.setColor(new Color(0, 0, 255, 50))

    for (i <- Math.max(0, minX) to Math.min(rows - 1, maxX)) do
      for (j <- Math.max(0, minY) to Math.min(cols - 1, maxY)) do
        // Draw the checked cells with a transparent fill
        g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize)
