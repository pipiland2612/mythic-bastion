package utils

import entity.tower.Frame

import java.awt.image.BufferedImage

object Cache:

  var animationCached: Map[String, Vector[Vector[BufferedImage]]] = Map()
  var frameCached: Map[(Double, Double), Frame] = Map()
