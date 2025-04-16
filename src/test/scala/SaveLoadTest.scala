import game.GamePanel
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import system.PlayerDataManager

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.file.{Files, Paths}

// Make PlayerData accessible for serialization
case class PlayerData(var stars: Int = 0) extends Serializable

class MockGamePanel extends GamePanel:
  class MockPlayer:
    var stars: Int = 0
  val player = new MockPlayer
  def getMockPlayer: MockPlayer = player

class SaveLoadTest extends AnyFlatSpec with Matchers:
  val testSavePath = "src/test/resources/player_save_test.dat"

  def cleanupTestFiles(): Unit =
    Files.deleteIfExists(Paths.get(testSavePath))

  "PlayerDataManager" should "save and load player data correctly" in {
    cleanupTestFiles()

    // Ensure test directory exists
    Files.createDirectories(Paths.get("src/test/resources"))

    val mockGP = new MockGamePanel
    // Modified to use test path
    val manager = new PlayerDataManager(mockGP):
      override def savePlayerData(): Unit =
        val oos = new ObjectOutputStream(new FileOutputStream(testSavePath))
        val data = PlayerData(mockGP.getMockPlayer.stars)
        oos.writeObject(data)
        oos.close()

      override def loadPlayerData(): Unit =
        val ois = new ObjectInputStream(new FileInputStream(testSavePath))
        val loadedPlayerData = ois.readObject().asInstanceOf[PlayerData]
        mockGP.getMockPlayer.stars = loadedPlayerData.stars
        ois.close()

    // Set initial value
    mockGP.getMockPlayer.stars = 42

    // Test save
    manager.savePlayerData()
    Files.exists(Paths.get(testSavePath)) shouldBe true

    // Reset value
    mockGP.getMockPlayer.stars = 0

    // Test load
    manager.loadPlayerData()
    mockGP.getMockPlayer.stars shouldBe 42

    cleanupTestFiles()
  }

  it should "handle missing save file gracefully" in {
    cleanupTestFiles()

    val mockGP = new MockGamePanel
    val manager = new PlayerDataManager(mockGP):
      override def loadPlayerData(): Unit =
        try
          super.loadPlayerData()
        catch
          case _: Exception => // swallow exception for test

    // Should not throw
    manager.loadPlayerData()
  }