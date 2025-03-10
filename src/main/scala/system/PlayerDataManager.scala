package system

import game.GamePanel
import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

class PlayerData extends Serializable:
  var stars = 0

class PlayerDataManager(gp: GamePanel):

  def savePlayerData(): Unit =
    try
      val oos = new ObjectOutputStream(new FileOutputStream(new File("src/main/resources/player_save.dat")))
      val data = PlayerData()
      data.stars = gp.getPlayer.stars
      oos.writeObject(data)
      oos.close()
    catch
      case e: Exception => e.printStackTrace()

  def loadPlayerData(): Unit =
    try
      val ois = new ObjectInputStream(new FileInputStream(new File("src/main/resources/player_save.dat")))
      val loadedPlayerData = ois.readObject().asInstanceOf[PlayerData]
      gp.getPlayer.stars = loadedPlayerData.stars
      ois.close()
    catch
      case e: Exception =>