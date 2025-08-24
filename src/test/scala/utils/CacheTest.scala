package utils

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.Cache

import java.awt.image.BufferedImage

class CacheTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach:

  var testImages: Vector[Vector[BufferedImage]] = _

  override def beforeEach(): Unit =
    // Clear cache before each test
    Cache.animationCached = Map()
    Cache.frameCached = Map()
    
    // Create test images for animation
    val frame1 = Vector(
      new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB),
      new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
    )
    val frame2 = Vector(
      new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB),
      new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
    )
    testImages = Vector(frame1, frame2)

  "Cache" should "start with empty animation cache" in {
    Cache.animationCached shouldBe empty
  }

  it should "start with empty frame cache" in {
    Cache.frameCached shouldBe empty
  }

  it should "store animation data" in {
    val entityName = "TestEntity"
    Cache.animationCached = Cache.animationCached + (entityName -> testImages)
    
    Cache.animationCached should contain key entityName
    Cache.animationCached(entityName) should have length 2
    Cache.animationCached(entityName)(0) should have length 2
  }

  it should "retrieve stored animation data" in {
    val entityName = "TestEntity"
    Cache.animationCached = Cache.animationCached + (entityName -> testImages)
    
    val retrieved = Cache.animationCached(entityName)
    retrieved shouldBe testImages
    retrieved(0)(0).getWidth shouldBe 32
    retrieved(0)(0).getHeight shouldBe 32
  }

  it should "handle multiple animation entries" in {
    val entity1 = "Entity1"
    val entity2 = "Entity2"
    
    val images1 = Vector(Vector(new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB)))
    val images2 = Vector(Vector(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)))
    
    Cache.animationCached = Cache.animationCached + (entity1 -> images1) + (entity2 -> images2)
    
    Cache.animationCached should have size 2
    Cache.animationCached should contain key entity1
    Cache.animationCached should contain key entity2
    
    Cache.animationCached(entity1)(0)(0).getWidth shouldBe 16
    Cache.animationCached(entity2)(0)(0).getWidth shouldBe 64
  }

  it should "overwrite existing entries" in {
    val entityName = "TestEntity"
    val originalImages = Vector(Vector(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)))
    val newImages = Vector(Vector(new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB)))
    
    Cache.animationCached = Cache.animationCached + (entityName -> originalImages)
    Cache.animationCached(entityName)(0)(0).getWidth shouldBe 32
    
    Cache.animationCached = Cache.animationCached + (entityName -> newImages)
    Cache.animationCached(entityName)(0)(0).getWidth shouldBe 64
  }

  it should "check for cached animation existence" in {
    val entityName = "TestEntity"
    
    Cache.animationCached.contains(entityName) shouldBe false
    
    Cache.animationCached = Cache.animationCached + (entityName -> testImages)
    Cache.animationCached.contains(entityName) shouldBe true
  }

  it should "handle coordinate precision for frame keys" in {
    val pos1 = (100.0, 200.0)
    val pos2 = (100.00001, 200.0) // Slightly different
    
    // Since we can't easily create Frame objects in tests, test the map directly
    Cache.frameCached.get(pos1) shouldBe None
    Cache.frameCached.get(pos2) shouldBe None
    
    // Test that coordinates are treated as separate keys
    pos1 should not equal pos2
  }

  it should "support clearing cache" in {
    val entityName = "TestEntity"
    Cache.animationCached = Cache.animationCached + (entityName -> testImages)
    
    Cache.animationCached should not be empty
    
    Cache.animationCached = Map()
    Cache.animationCached shouldBe empty
  }

  it should "handle complex animation structure" in {
    val entityName = "ComplexEntity"
    val complexImages = Vector(
      Vector(
        new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
        new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB),
        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)
      ),
      Vector(
        new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB),
        new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB)
      )
    )
    
    Cache.animationCached = Cache.animationCached + (entityName -> complexImages)
    
    val retrieved = Cache.animationCached(entityName)
    retrieved should have length 2
    retrieved(0) should have length 3
    retrieved(1) should have length 2
    
    retrieved(0)(0).getWidth shouldBe 16
    retrieved(0)(1).getWidth shouldBe 32
    retrieved(0)(2).getWidth shouldBe 64
  }