package system.stage

class PlayerStage (private var coins: Int):

  private val maxHealth: Int = 30
  private var health: Int = maxHealth

  def getHealth: Int = health
  def getCoins: Int = coins

  def updateCoin(dx: Int): Unit =
    if coins + dx < 0 then return
    coins += dx

  def updateHealth(dx: Int): Unit =
    var result = health + dx
    if (health + dx > maxHealth) result = maxHealth
    this.health = result