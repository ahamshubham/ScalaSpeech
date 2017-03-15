/*
 Created by Shubham Bajpai on 7/12/16.
*/
package main.scala

import java.awt.{Color, Dimension, Graphics2D}
import java.util.concurrent.LinkedBlockingQueue

import scala.collection.mutable.Queue
import scala.swing.{MainFrame, Panel, SimpleSwingApplication}
import scala.util.control.Breaks._


class RealTimeAudioPlotterClass (queue: LinkedBlockingQueue[Double]) extends Runnable {
  private val sharedQueue = queue
  private var audioBatch = new Queue[Double]()

  val batch_size = 1000
  val initial_queue_size = 3200
  val shift = batch_size

  def run() {
    for (i <- 1 to initial_queue_size) {
      val element = sharedQueue.take()

      if (element != null)
        audioBatch.enqueue(element)
      else
        break
    }

    windowMaker()

    plotter()

    while (true) {
      try {
        for (i <- 1 to shift) {
          val element = sharedQueue.take()

          if (element != null) {
            audioBatch.dequeue()
            audioBatch.enqueue(element)
          }
          else
            break
        }

        plotter()

      } catch {

        case ex: InterruptedException => println("Interrupted Exception")

      }
    }
  }

  private val window_maker = new DrawingClass()

  def plotter(): Unit = {
    window_maker.paintAgain(audioBatch.toList)
  }

  def windowMaker(): Unit = {
    window_maker.main(Array())
  }

}

//Class to create the window
class DrawingClass extends SimpleSwingApplication {

  lazy val window = new DataPanel() {
    preferredSize = new Dimension(1000, 500)
  }

  lazy val top = new MainFrame {
    contents = window
  }

  def paintAgain(audioBatch: List[Double]) = {
    window.plot(audioBatch)
    window.repaint()
  }
}

//Panel to plot the audio signal
class DataPanel() extends Panel {

  private var audioInfoList = List(0.0)

  //Set color for background
  background = Color.WHITE

  //Plots the audio signal
  override def paintComponent(g: Graphics2D) {
    super.paintComponent(g)

    //Set color for plotting
    g.setColor(new Color(29,176,249))

    val mid_y = (g.getClipBounds.height.toFloat)/2
    var x2 = 1
    var prev_point = 0.0

    audioInfoList foreach { point =>
      val y1 = prev_point.toInt + mid_y.toInt
      val y2 = point.toInt + mid_y.toInt
      val x1 = x2-1

      g.drawLine(x1,y1,x2,y2)

      x2 = x2 + 1
      prev_point = point
    }
  }

  def plot(audioBatch: List[Double]) {
    this.audioInfoList = audioBatch
  }

}
