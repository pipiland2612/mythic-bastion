package utils

import entity.creature.enemy.Enemy
import game.GamePanel

import scala.collection.mutable.ListBuffer

class GridCell:
  private val enemyList: ListBuffer[Enemy] = ListBuffer()

  def addEnemy(enemy: Enemy) = enemyList += enemy
  def getList = enemyList

class Grid(gp: GamePanel):

  private val cellSize: Int = 48
  private val rows: Int = gp.screenWidth / cellSize
  private val cols: Int = gp.screenHeight / cellSize
  val cells: Array[Array[GridCell]] = Array.ofDim[GridCell](rows, cols)

  initialise()
  def initialise(): Unit =
    for (i <- 0 until rows) do
      for (j <- 0 until cols) do
        cells(i)(j) = new GridCell

  def addEnemy(enemy: Enemy): Unit =
    val pos = (enemy.attackBox.getX, enemy.attackBox.getY)
    val gridX = enemy.pos._1.toInt / cellSize
    val gridY = enemy.pos._2.toInt / cellSize
    if (gridX >= 0 && gridX < rows && gridY >= 0 && gridY < cols) then
      cells(gridX)(gridY).addEnemy(enemy)

  def scanForEnemiesInRange(x: Int, y: Int, range: Int): ListBuffer[Enemy] =
    val towerGridX = x / cellSize
    val towerGridY = y / cellSize
    val nearbyEnemies: ListBuffer[Enemy] = ListBuffer()

    for (dx <- -1 to 1) do
      for (dy <- -1 to 1) do
        val nx = towerGridX + dx
        val ny = towerGridY + dy
        if (nx >= 0 && nx < rows && ny >= 0 && ny < cols)
          nearbyEnemies.addAll(cells(nx)(ny).getList)

    nearbyEnemies

