
import game.GamePanel
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.Tools

class ToolsTest extends AnyFlatSpec with Matchers:

  val mockGamePanel: GamePanel = new GamePanel()
  Tools.setUp(mockGamePanel)

  val testAnimationJson = "alliances/Helper01.json"
  val testAnimationImage = "alliances/Helper01.png"

  "parser" should "correctly parse animation data from JSON and image" in {
    val scaleFactor = 1.0
    val result = Tools.parser(testAnimationJson, testAnimationImage, scaleFactor)

    result should not be None
    result.get should not be empty
    result.get.foreach(animationFrames =>
      animationFrames should not be empty
      animationFrames.foreach(frame =>
        frame should not be null
        frame.getWidth should be > 0
        frame.getHeight should be > 0
      )
    )
  }

  it should "apply correct scaling to animation frames" in {
    val originalScale = 1.0
    val largerScale = 2.0

    val originalResult = Tools.parser(testAnimationJson, testAnimationImage, originalScale)
    val scaledResult = Tools.parser(testAnimationJson, testAnimationImage, largerScale)

    (originalResult, scaledResult) match
      case (Some(original), Some(scaled)) =>
        original.zip(scaled).foreach((origFrames, scaledFrames) =>
          origFrames.zip(scaledFrames).foreach((origFrame, scaledFrame) =>
            scaledFrame.getWidth shouldBe (origFrame.getWidth * largerScale).toInt
            scaledFrame.getHeight shouldBe (origFrame.getHeight * largerScale).toInt
          ))
      case _ => fail("Both parsings should have succeeded")
  }
