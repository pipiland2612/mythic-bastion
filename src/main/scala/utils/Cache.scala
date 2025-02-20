package utils

import java.awt.image.BufferedImage

object Cache:

  var cachedResult: Map[String, Vector[Vector[BufferedImage]]] = Map()
  
