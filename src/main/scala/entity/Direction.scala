package entity

enum Direction:
  case UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT

object Direction:
  val allCreatureDirections: Seq[Direction] =
    Seq(
      Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP,
      Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_LEFT, Direction.DOWN_RIGHT
    )

  val allEntityDirections: Seq[Direction] =
    Seq(
      Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP,
    )
