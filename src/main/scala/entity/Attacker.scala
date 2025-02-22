package entity

trait Attacker:
  protected val apDmg: Double
  protected val adDmg: Double
  protected val range: Double
  protected val speed: Double

  val maxAttackCoolDown: Double
  var attackCoolDown: Double = 0

  def getApDmg: Double = apDmg
  def getAdDmg: Double = adDmg
  def getRange: Double = range
  def getSpeed: Double = speed
