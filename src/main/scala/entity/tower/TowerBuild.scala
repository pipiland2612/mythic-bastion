package entity.tower

case class TowerBuild(pos: (Double, Double)):
  var currentTower: Option[Tower] = None