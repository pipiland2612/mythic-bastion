package utils

import java.awt.image.BufferedImage

class Animation(val frames: Vector[BufferedImage], val frameDuration: Int):
  private var currentFrame: Int = 0
  private var frameCount: Int = 0

  def update(): Unit =
    frameCount += 1
    if (frameCount >= frameDuration) then
      frameCount = 0
      currentFrame = (currentFrame + 1) % frames.length

  def getCurrentFrame: BufferedImage = frames(currentFrame)

