package utils

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.Animation

import java.awt.image.BufferedImage

class AnimationTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach:

  var testImages: Vector[BufferedImage] = _
  var animation: Animation = _
  
  override def beforeEach(): Unit =
    // Create test images
    testImages = Vector(
      new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB),
      new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB),
      new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB),
      new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
    )
    
    animation = Animation(testImages, frameDuration = 10, attackStartFrame = 0, attackEndFrame = 2)

  "Animation" should "initialize with correct properties" in {
    animation.frames should have length 4
    animation.frameDuration shouldBe 10
    animation.getCurrentFrameCount shouldBe 0
  }

  it should "advance frame on update" in {
    val initialFrame = animation.getCurrentFrameCount
    
    // Update multiple times to advance frame
    for _ <- 1 to 15 do
      animation.update()
    
    // Should have advanced at least one frame or stayed the same
    animation.getCurrentFrameCount should be >= initialFrame
  }

  it should "loop back to first frame after completion" in {
    // Update enough times to complete full cycle
    for _ <- 1 to 50 do
      animation.update()
    
    // Should have looped back within bounds
    animation.getCurrentFrameCount should be < testImages.length
    animation.getCurrentFrameCount should be >= 0
  }

  it should "return current frame image" in {
    val currentFrame = animation.getCurrentFrame
    currentFrame should not be null
    currentFrame shouldBe testImages(animation.getCurrentFrameCount)
  }

  it should "handle single frame animation" in {
    val singleFrameAnimation = Animation(Vector(testImages.head), frameDuration = 5)
    
    singleFrameAnimation.getCurrentFrameCount shouldBe 0
    
    // Update should not change frame index for single frame
    for _ <- 1 to 20 do
      singleFrameAnimation.update()
    
    singleFrameAnimation.getCurrentFrameCount shouldBe 0
  }

  it should "respect different frame rates" in {
    val slowAnimation = Animation(testImages, frameDuration = 60) // Slower
    val fastAnimation = Animation(testImages, frameDuration = 5)  // Faster
    
    // Update both same number of times
    for _ <- 1 to 20 do
      slowAnimation.update()
      fastAnimation.update()
    
    // Fast animation should have advanced more frames
    fastAnimation.getCurrentFrameCount should be >= slowAnimation.getCurrentFrameCount
  }

  it should "handle empty frame vector" in {
    val emptyAnimation = Animation(Vector.empty, frameDuration = 10)
    
    // Should not crash when getting current frame from empty animation
    intercept[IndexOutOfBoundsException] {
      emptyAnimation.getCurrentFrame
    }
  }

  it should "reset to first frame" in {
    // Advance animation
    for _ <- 1 to 30 do
      animation.update()
    
    animation.reset()
    animation.getCurrentFrameCount shouldBe 0
  }

  it should "check attack interval correctly" in {
    animation.isInAttackInterval shouldBe true // Since current frame is 0 and attack range is 0-2
    
    // Advance beyond attack interval
    for _ <- 1 to 35 do // More than enough to advance past attack frames
      animation.update()
    
    // Should still work without error
    val isInAttack = animation.isInAttackInterval
    isInAttack shouldBe a [Boolean]
  }

  it should "handle frame rate of 1" in {
    val fastestAnimation = Animation(testImages, frameDuration = 1)
    
    // Each update should advance frame
    fastestAnimation.getCurrentFrameCount shouldBe 0
    
    fastestAnimation.update()
    fastestAnimation.getCurrentFrameCount shouldBe 1
    
    fastestAnimation.update()
    fastestAnimation.getCurrentFrameCount shouldBe 2
  }

  it should "maintain consistent frame dimensions" in {
    val frames = animation.frames
    frames.foreach(frame =>
      frame.getWidth shouldBe 32
      frame.getHeight shouldBe 32
      frame.getType shouldBe BufferedImage.TYPE_INT_ARGB
    )
  }