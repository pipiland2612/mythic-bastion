package system

import javax.sound.sampled.{AudioInputStream, AudioSystem, Clip, FloatControl}

class Sound:
  private var clip: Clip = _
  private var fc: FloatControl = _
  private val volumeScale: Int = 3
  private var volume: Float = _
  private val soundUrls: Array[String] = Array(
    "arrow_fire1.wav",  // 0
    "arrow_fire2.wav",  // 1
    "arrow_hit1.wav",   // 2
    "arrow_hit2.wav",   // 3
    "arrow_ready1.wav", // 4
    "arrow_ready3.wav", // 5
    "bo_start.wav",     // 6
    "building.wav",     // 7
    "explo_fireend1.wav",  // 8
    "explo_firestart1.wav", // 9
    "explo_ready1.wav",  // 10
    "explo_ready2.wav",  // 11
    "explo_ready3.wav",  // 12
    "gamebgsound.wav",   // 13
    "loaderClose.wav",   // 14
    "loaderOpen.wav",    // 15
    "magic_fire1.wav",   // 16
    "magic_ready1.wav",  // 17
    "magic_ready2.wav",  // 18
    "magic_ready3.wav",  // 19
    "map0bg.wav",        // 20
    "map_theme_1.wav",   // 21
    "mapbgsound.wav",    // 22
    "monster_die1.wav",  // 23
    "monster_die2.wav",  // 24
    "monster_die3.wav",  // 25
    "monster_die4.wav",  // 26
    "savage_music_desert_battle.wav", // 27
    "savage_music_jungle_battle.wav", // 28
    "select.wav",        // 29
    "sell_tower.wav",    // 30
    "shield_fire1.wav",  // 31
    "shield_fire2.wav",  // 32
    "shield_fire3.wav",  // 33
    "shield_ready1.wav", // 34
    "shield_ready2.wav", // 35
    "shield_ready3.wav", // 36
    "victory.wav"        // 37
  )

  def setFile(index: Int): Unit =
    val soundPath = s"/music/${soundUrls(index)}"
    val resourceStream = Option(getClass.getResourceAsStream(soundPath))

    resourceStream match
      case Some(stream) =>
        try
          val ais: AudioInputStream = AudioSystem.getAudioInputStream(stream)
          clip = AudioSystem.getClip()
          clip.open(ais)
          fc = clip.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl]
          checkVolume()
        catch
          case e: Exception =>
            e.printStackTrace()
      case None =>

  def play(): Unit =
    def play(): Unit =
      if (Option(clip).isDefined) then
        println("Playing sound...")
        clip.start()
      else
        println("Error: Clip is not initialized")

  def stop(): Unit =
    if (Option(clip).isDefined) then clip.stop()

  def loop(): Unit =
    if (Option(clip).isDefined) then clip.loop(Clip.LOOP_CONTINUOUSLY)

  def checkVolume(): Unit =
    volume = volumeScale match
      case 0 => -80f
      case 1 => -20f
      case 2 => -12f
      case 3 => -5f
      case 4 => 1f
      case 5 => 6f
    if Option(fc).isDefined then fc.setValue(volume)
end Sound