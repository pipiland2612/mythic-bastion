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

/** Represents a single cell in the game grid, storing enemies and allies.
 *
 * This class manages collections of enemies and allies within a grid cell, allowing for efficient
 * addition, removal, and retrieval of creatures.
 */
class GridCell:
  private val enemyMap: mutable.HashMap[Int, Enemy] = mutable.HashMap()
  private val allianceMap: mutable.HashMap[Int, Alliance] = mutable.HashMap()

  /** Adds a creature to the cell.
   *
   * Stores the creature in the appropriate map (enemy or alliance) based on its type, using its ID as the key.
   *
   * @param creature The Creature to add (either an Enemy or Alliance).
   */
  def addCreature(creature: Creature): Unit =
    creature match
      case enemy: Enemy =>
        enemyMap.put(enemy.getId, enemy)
      case alliance: Alliance =>
        allianceMap.put(alliance.getId, alliance)
      case _ =>

  /** Removes a creature from the cell.
   *
   * Removes the creature from the appropriate map (enemy or alliance) based on its type and ID.
   *
   * @param creature The Creature to remove (either an Enemy or Alliance).
   */
  def removeCreature(creature: Creature): Unit =
    creature match
      case enemy: Enemy =>
        enemyMap.remove(enemy.getId)
      case alliance: Alliance =>
        allianceMap.remove(alliance.getId)
      case _ =>

  def getEnemies: Iterable[Enemy] = enemyMap.values
  def getAlliances: Iterable[Alliance] = allianceMap.values

/** Manages a spatial grid for efficient creature tracking.
 *
 * This class divides the game screen into a grid of cells, each containing enemies and allies. It supports
 * updating creature positions, scanning for creatures within an attack range, and rendering the grid and
 * attack ranges for debugging or visualization.
 *
 * @param gp The GamePanel instance for accessing screen dimensions and game state.
 */
class Grid(gp: GamePanel):
  private val cellSize: Int = 96
  private val rows: Int = gp.screenWidth / cellSize
  private val cols: Int = gp.screenHeight / cellSize
  private val cells: Array[Array[GridCell]] = initializeCells()

  def getRows: Int = rows
  def getCols: Int = cols

  /** Initializes the grid with empty cells.
   *
   * Creates a 2D array of GridCell instances, one for each grid position.
   *
   * @return The initialized 2D array of GridCell objects.
   */
  private def initializeCells(): Array[Array[GridCell]] =
    val grid = Array.ofDim[GridCell](rows, cols)
    for i <- 0 until rows do
      for j <- 0 until cols do
        grid(i)(j) = new GridCell
    grid

  /** Checks if the given grid coordinates are within bounds.
   *
   * @param x The x-coordinate of the grid cell.
   * @param y The y-coordinate of the grid cell.
   * @return True if the coordinates are within the grid bounds, false otherwise.
   */
  private def checkBounds(x: Int, y: Int): Boolean =
    x >= 0 && x < rows && y >= 0 && y < cols

  /** Calculates the grid cell coordinates for a creature's center position.
   *
   * @param creature The Creature whose center position is used.
   * @return A tuple of (x, y) grid coordinates.
   */
  private def creatureCenterPos(creature: Creature): (Int, Int) =
    val pos = (creature.attackBox.getCenterX.toInt / cellSize, creature.attackBox.getCenterY.toInt / cellSize)
    (pos._1, pos._2)

  /** Resets the grid by clearing all cells.
   *
   * Replaces all cells with new, empty GridCell instances.
   */
  def reset(): Unit =
    for
      i <- 0 until rows
      j <- 0 until cols
    do
      cells(i)(j) = new GridCell

  /** Removes a creature from its current grid cell.
   *
   * @param creature The Creature to remove.
   */
  def remove(creature: Creature): Unit =
    val (gridX, gridY) = creatureCenterPos(creature)
    if checkBounds(gridX, gridY) then
      cells(gridX)(gridY).removeCreature(creature)

  /** Updates a creature's position in the grid.
   *
   * Moves the creature to a new grid cell if its position has changed, removing it from the previous cell
   * and adding it to the new one. Handles cases where the creature is newly added or remains in the same cell.
   *
   * @param creature The Creature whose position is updated.
   * @param prevPos The previous (x, y) position of the creature.
   */
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

  /** Scans for creatures within an attacker's attack range.
   *
   * Checks grid cells overlapping the attacker's attack circle to find creatures whose attack boxes are
   * within the circle, returning them in a ListBuffer.
   *
   * @param attacker The Entity (e.g., Tower or Enemy) with an attack circle.
   * @param getCreatures A function to retrieve creatures (Enemies or Alliances) from a grid cell.
   * @return A ListBuffer of creatures within the attack range.
   */
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

  /** Calculates the grid bounds for scanning an attack circle.
   *
   * Determines the range of grid cells that overlap with the attack circle's bounding rectangle,
   * ensuring the bounds stay within the grid.
   *
   * @param ellipse The Ellipse2D representing the attack range.
   * @return A ScanBounds object with the minimum and maximum x and y grid coordinates.
   */
  private def calculateScanBounds(ellipse: Ellipse2D): ScanBounds =
    val maxRect = Tools.getInnerRectangle(ellipse)
    ScanBounds(
      minX = (maxRect.getX.toInt / cellSize).max(0),
      maxX = (maxRect.getMaxX.toInt / cellSize).min(rows - 1),
      minY = (maxRect.getY.toInt / cellSize).max(0),
      maxY = (maxRect.getMaxY.toInt / cellSize).min(cols - 1)
    )

  /** Scans for enemies within an attacker's attack range.
   *
   * @param attacker The Entity (e.g., Tower) with an attack circle.
   * @return A ListBuffer of Enemy objects within the attack range.
   */
  def scanForEnemiesInRange(attacker: Entity): ListBuffer[Enemy] =
    scanForCreaturesInRange(attacker, (x, y) => cells(x)(y).getEnemies)

  /** Scans for alliances within an enemy's attack range.
   *
   * @param enemy The Enemy with an attack circle.
   * @return A ListBuffer of Alliance objects within the attack range.
   */
  def scanForAlliancesInRange(enemy: Enemy): ListBuffer[Alliance] =
    scanForCreaturesInRange(enemy, (x, y) => cells(x)(y).getAlliances)

  /** Draws the grid and a tower's attack range for visualization.
   *
   * Renders grid lines and highlights the tower's attack circle and the grid cells it overlaps.
   *
   * @param g The Graphics2D context for rendering.
   * @param tower The Tower whose attack range is drawn.
   */
  def draw(g: Graphics2D, tower: Tower): Unit =
    drawGridLines(g)
    drawAttackRange(g, tower.attackCircle)

  /** Draws the grid lines.
   *
   * Renders a gray grid outline for each cell.
   *
   * @param g The Graphics2D context for rendering.
   */
  private def drawGridLines(g: Graphics2D): Unit =
    g.setColor(Color.GRAY)
    for i <- 0 until rows do
      for j <- 0 until cols do
        g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize)

  /** Draws a tower's attack range and highlights overlapping grid cells.
   *
   * Renders the attack circle in blue and fills overlapping grid cells with a semi-transparent blue.
   *
   * @param g The Graphics2D context for rendering.
   * @param ellipse The Ellipse2D representing the attack range.
   */
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

/** Represents the bounds of a grid scan for an attack circle.
 *
 * @param minX The minimum x-coordinate of the grid cells to scan.
 * @param maxX The maximum x-coordinate of the grid cells to scan.
 * @param minY The minimum y-coordinate of the grid cells to scan.
 * @param maxY The maximum y-coordinate of the grid cells to scan.
 */
private case class ScanBounds(minX: Int, maxX: Int, minY: Int, maxY: Int)