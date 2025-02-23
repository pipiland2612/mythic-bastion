package system

import game.GamePanel

class SystemHandler (gp: GamePanel):
  val keyHandler: KeyHandler = KeyHandler(gp)
  val grid: Grid = Grid(gp)

  def update(): Unit =
    grid.reset()