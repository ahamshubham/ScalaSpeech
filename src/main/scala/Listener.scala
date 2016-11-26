/* Created by Shubham Bajpai on 26/11/2016*/


import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine


object Listener {

  /*
  Oracle Documentation:

  public AudioFormat(float sampleRate,
                     int sampleSizeInBits,
                     int channels,
                     boolean signed,
                     boolean bigEndian)
  Constructs an AudioFormat with a linear PCM encoding and the given parameters. The frame size is set to the number of bytes required to contain one sample from each channel, and the frame rate is set to the sample rate.
          Parameters:
  sampleRate - the number of samples per second
  sampleSizeInBits - the number of bits in each sample
  channels - the number of channels (1 for mono, 2 for stereo, and so on)
  signed - indicates whether the data is signed or unsigned
  bigEndian - indicates whether the data for a single sample is stored in big-endian byte order (false means little-endian)
  */

    private val sampleRate = 16000
    private val sampleSizeInBits = 16
    private val channels = 1
    private val signed = true
    private val bigEndian = true


    def main(args: Array[String]) {
        audioToStream()
    }

    def audioToStream(): Unit = {
        val format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian)

        val targetInfo = new DataLine.Info(classOf[TargetDataLine], format)
        val sourceInfo = new DataLine.Info(classOf[SourceDataLine], format)
        try {
            val targetLine = AudioSystem.getLine(targetInfo).asInstanceOf[TargetDataLine]
            targetLine.open(format)
            targetLine.start()

            val sourceLine = AudioSystem.getLine(sourceInfo).asInstanceOf[SourceDataLine]
            sourceLine.open(format)
            sourceLine.start()
            var numBytesRead = 0
            var targetData = new Array[Byte](targetLine.getBufferSize() / 5)

            while (numBytesRead != -1) {
                numBytesRead = targetLine.read(targetData, 0, targetData.length)
                sourceLine.write(targetData, 0, numBytesRead)
            }
        }
        catch {
            case e: Exception => println(e)
        }
    }
}
