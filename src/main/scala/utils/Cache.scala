package utils

import java.awt.image.BufferedImage

object Cache:

  var animationCached: Map[String, Vector[Vector[BufferedImage]]] = Map()
  
