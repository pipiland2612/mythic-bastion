package stage

class PlayerStage (var coins: Int):

  private val maxHealth: Int = 20
  private var health: Int = maxHealth

  def updateCoin(dx: Int): Unit = coins += dx

  def updateHealth(dx: Int): Unit =
    var result = health + dx
    if (health + dx > maxHealth) result = maxHealth
    this.health = result

