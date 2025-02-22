package entity

trait Attacker:
  protected val apDmg: Double
  protected val adDmg: Double
  protected val speed: Double

  def getApDmg: Double = apDmg
  def getAdDmg: Double = adDmg
  def getSpeed: Double = speed
