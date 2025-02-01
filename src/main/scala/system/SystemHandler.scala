package system

import game.GamePanel

class SystemHandler (gp: GamePanel):
  val keyHandler: KeyHandler = KeyHandler(gp)

