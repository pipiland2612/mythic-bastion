package entity

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DirectionTest extends AnyFunSuite with Matchers:

  test("Direction enum should have all expected values"):
    val allDirections = Direction.values.toSet
    
    allDirections should contain(Direction.UP)
    allDirections should contain(Direction.DOWN)
    allDirections should contain(Direction.LEFT)
    allDirections should contain(Direction.RIGHT)
    allDirections should contain(Direction.UP_LEFT)
    allDirections should contain(Direction.UP_RIGHT)
    allDirections should contain(Direction.DOWN_LEFT)
    allDirections should contain(Direction.DOWN_RIGHT)
    
    allDirections should have size 8

  test("Direction should support pattern matching"):
    def getDirectionName(dir: Direction): String = dir match
      case Direction.UP => "up"
      case Direction.DOWN => "down"
      case Direction.LEFT => "left"
      case Direction.RIGHT => "right"
      case Direction.UP_LEFT => "up-left"
      case Direction.UP_RIGHT => "up-right"
      case Direction.DOWN_LEFT => "down-left"
      case Direction.DOWN_RIGHT => "down-right"
    
    getDirectionName(Direction.UP) should be("up")
    getDirectionName(Direction.DOWN_RIGHT) should be("down-right")

  test("allCreatureDirections should contain all 8 directions"):
    Direction.allCreatureDirections should have size 8
    Direction.allCreatureDirections should contain(Direction.UP)
    Direction.allCreatureDirections should contain(Direction.DOWN)
    Direction.allCreatureDirections should contain(Direction.LEFT)
    Direction.allCreatureDirections should contain(Direction.RIGHT)
    Direction.allCreatureDirections should contain(Direction.UP_LEFT)
    Direction.allCreatureDirections should contain(Direction.UP_RIGHT)
    Direction.allCreatureDirections should contain(Direction.DOWN_LEFT)
    Direction.allCreatureDirections should contain(Direction.DOWN_RIGHT)

  test("allEntityDirections should contain only 4 cardinal directions"):
    Direction.allEntityDirections should have size 4
    Direction.allEntityDirections should contain(Direction.UP)
    Direction.allEntityDirections should contain(Direction.DOWN)
    Direction.allEntityDirections should contain(Direction.LEFT)
    Direction.allEntityDirections should contain(Direction.RIGHT)
    
    Direction.allEntityDirections should not contain Direction.UP_LEFT
    Direction.allEntityDirections should not contain Direction.UP_RIGHT
    Direction.allEntityDirections should not contain Direction.DOWN_LEFT
    Direction.allEntityDirections should not contain Direction.DOWN_RIGHT

  test("allCreatureDirections should be a superset of allEntityDirections"):
    val creatureSet = Direction.allCreatureDirections.toSet
    val entitySet = Direction.allEntityDirections.toSet
    
    creatureSet should contain allElementsOf entitySet

  test("Direction values should be unique"):
    val allValues = Direction.values
    allValues.toSet should have size allValues.length

  test("Direction ordering should be consistent"):
    val directions = Direction.values.toList
    directions should not be empty
    
    // Enum values should maintain consistent ordering
    directions.indexOf(Direction.UP) should be >= 0
    directions.indexOf(Direction.DOWN) should be >= 0

  test("Direction collections should not be empty"):
    Direction.allCreatureDirections should not be empty
    Direction.allEntityDirections should not be empty

  test("Direction collections should be immutable"):
    val originalSize = Direction.allCreatureDirections.size
    
    // Attempting to modify should not affect the original
    val modified = Direction.allCreatureDirections :+ Direction.UP
    Direction.allCreatureDirections should have size originalSize
    modified should have size (originalSize + 1)

  test("Each direction should have a string representation"):
    Direction.values.foreach { direction =>
      direction.toString should not be empty
      direction.toString should not be null
    }