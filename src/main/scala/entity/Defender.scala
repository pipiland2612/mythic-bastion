package entity

trait Defender:
  protected val apDefense: Double
  protected val adDefense: Double

  def getApDefense: Double = apDefense
  def getAdDefense: Double = adDefense
