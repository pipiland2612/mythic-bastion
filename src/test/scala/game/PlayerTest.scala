package game

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PlayerTest extends AnyFlatSpec with Matchers:

  "Player" should "initialize with correct default values" in {
    val player = new Player()
    player.stars shouldBe 60
  }

  it should "allow stars to be modified" in {
    val player = new Player()
    player.stars = 100
    player.stars shouldBe 100
  }

  it should "handle negative star values" in {
    val player = new Player()
    player.stars = -10
    player.stars shouldBe -10
  }

  it should "handle zero star values" in {
    val player = new Player()
    player.stars = 0
    player.stars shouldBe 0
  }

  it should "support large star values" in {
    val player = new Player()
    player.stars = Int.MaxValue
    player.stars shouldBe Int.MaxValue
  }

  it should "be mutable across multiple operations" in {
    val player = new Player()
    val initialStars = player.stars
    
    player.stars += 50
    player.stars shouldBe initialStars + 50
    
    player.stars -= 20
    player.stars shouldBe initialStars + 30
    
    player.stars *= 2
    player.stars shouldBe (initialStars + 30) * 2
  }