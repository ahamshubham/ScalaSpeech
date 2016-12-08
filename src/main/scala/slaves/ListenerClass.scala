/*
Created by Shubham Bajpai on 30/11/16.
*/

package main.scala.slaves

import java.util.concurrent.LinkedBlockingQueue
import javax.sound.sampled._
import scala.util.control._


class ListenerClass (queue1: LinkedBlockingQueue[Double],queue2: LinkedBlockingQueue[Double]) extends Runnable {

  //Audio Settings
  private val sharedQueue1 = queue1
  private val sharedQueue2 = queue2
  private val sampleRate = 16000
  private val sampleSizeInBits = 16
  private val channels = 1
  private val signed = true
  private val bigEndian = true

  def run() {
    try {
      val format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian)

      //Audio Capture Device
      val targetInfo = new DataLine.Info(classOf[TargetDataLine], format)

      try {
        val targetLine = AudioSystem.getLine(targetInfo).asInstanceOf[TargetDataLine]
        targetLine.open(format)
        targetLine.start()


        var numberOfBytesRead = 0
        //val numberOfBytesToRead = 3200
        val numberOfBytesToRead = targetLine.getBufferSize() / 5
        var audioInformationInBytes = new Array[Byte](numberOfBytesToRead)

        while (numberOfBytesRead != -1) {
          numberOfBytesRead = targetLine.read(audioInformationInBytes, 0, numberOfBytesToRead)

          //Convert audio information from byte notation to integer notation
          val audioInformation = audioDecoder(audioInformationInBytes)
          for (sample <- audioInformation) {
            sharedQueue1.put(sample)
            sharedQueue2.put(sample)
          }
        }
      }
      catch {
        case e: Exception => println(e)
      }

    } catch {
      case ex: InterruptedException => println("Interrupted Exception")
    }
  }

  def audioDecoder(audioInformationInBytes: Array[Byte]):Array[Double] = {
    val numberOfBytes = audioInformationInBytes.length
    val bytesPerSample = sampleSizeInBits/8
    val amplification = 100.0  //Choose a number of your choice
    val micBufferData = new Array[Double](numberOfBytes - bytesPerSample +1)

    var index1 = 0
    var index2 = 0

    val loop = new Breaks

    loop.breakable
    {
      while (true) {
        if (index1 > numberOfBytes - bytesPerSample)
          loop.break
        var sample: Double = 0.0

        for (index3: Int <- 0 to bytesPerSample - 1) {
          var temp1: Int = audioInformationInBytes(index1 + index3)

          if (index3 < bytesPerSample - 1 || bytesPerSample == 1) {
            temp1 = temp1 & 0xff
          }

          sample += (temp1 << (index3 * 8))
        }

        val converted_sample = amplification * (sample / 32768.0)
        //println(converted_sample)
        micBufferData(index2) = converted_sample

        index1 = index1 + bytesPerSample
        index2 = index2 + 1
      }
    }

      return micBufferData
  }
}
