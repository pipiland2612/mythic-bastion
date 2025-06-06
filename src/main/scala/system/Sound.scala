package system

import java.io.BufferedInputStream
import javax.sound.sampled.{AudioInputStream, AudioSystem, Clip, FloatControl}

class Sound:
  private var clip: Clip = _
  private var fc: FloatControl = _
  private val volumeScale: Int = 3
  private var volume: Float = _
  private var isPlaying: Boolean = false

  def setFile(path: String): Unit =
    val soundPath = s"/music/$path"
    val resourceStream = Option(getClass.getResourceAsStream(soundPath))

    resourceStream match
      case Some(stream) =>
        try
          val bufferedStream = BufferedInputStream(stream)
          val ais: AudioInputStream = AudioSystem.getAudioInputStream(bufferedStream)
          clip = AudioSystem.getClip()
          clip.open(ais)
          fc = clip.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl]
          checkVolume()
        catch
          case e: Exception =>
            e.printStackTrace()
            println(s"Error loading sound: ${e.getMessage}")
      case None =>
        println(s"Error: Could not find sound file: $soundPath")

  def play(): Unit =
    if Option(clip).isDefined then
      clip.setFramePosition(0)
      clip.start()

  def stop(): Unit =
    if Option(clip).isDefined then clip.stop()

  def loop(): Unit =
    if Option(clip).isDefined then clip.loop(Clip.LOOP_CONTINUOUSLY)

  private def checkVolume(): Unit =
    volume = volumeScale match
      case 0 => -80f
      case 1 => -20f
      case 2 => -12f
      case 3 => -5f
      case 4 => 1f
      case 5 => 6f
    if Option(fc).isDefined then fc.setValue(volume)
end Sound