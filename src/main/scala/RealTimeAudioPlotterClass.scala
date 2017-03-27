/*
 Created by Shubham Bajpai on 7/12/16.
*/
package main.scala

import java.awt.{Color, Dimension, Graphics2D}
import java.util.{Timer, TimerTask}
import java.util.concurrent.LinkedBlockingQueue

import scala.collection.mutable.Queue
import scala.swing.{MainFrame, Panel, SimpleSwingApplication}
import scala.util.control.Breaks._


class RealTimeAudioPlotterClass(queue: LinkedBlockingQueue[Double]) extends Runnable {
  val batch_size = 960
  val initial_queue_size = 3200
  val shift = batch_size
  private val sharedQueue = queue
  private val window_maker = new DrawingClass()
  private var audioBatch = new Queue[Double]()

  def run() {
    for (i <- 1 to initial_queue_size) {
      val element = sharedQueue.take()

      if (element != null)
        audioBatch.enqueue(element)
      else
        break
    }

    windowMaker()

    val plotter_task = new PlotterTask
    val plotter_timer = new Timer
    val time_period = shift / 16
    plotter_timer.scheduleAtFixedRate(plotter_task, 0, time_period)
  }

  def windowMaker(): Unit = {
    window_maker.main(Array())
  }

  def plotter(audioBatchList: List[Double]): Unit = {
    window_maker.paintAgain(audioBatchList)
  }

  class PlotterTask extends TimerTask {
    override def run() = {
      //This should be converted to a thread
      println("System time:" + System.currentTimeMillis())
      plotter(audioBatch.toList)

      try {
        for (i <- 1 to shift) {
          val element = sharedQueue.take()
          if (element != null) {
            audioBatch.dequeue()
            audioBatch.enqueue(element)
          }
          else {
            break
          }
        }
      } catch {
        case ex: InterruptedException => println("Interrupted Exception")
      }
      println("Completed_plotting")
    }
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
    g.setColor(new Color(29, 176, 249))


    val window_height = g.getClipBounds.height.toFloat
    val window_width = g.getClipBounds.width.toFloat
    val scale = (window_width.toFloat / (audioInfoList.length))
    val mid_y = window_height / 2
    var x2 = 1
    var prev_point = 0.0

    audioInfoList foreach { point =>
      val y1 = prev_point.toInt + mid_y.toInt
      val y2 = point.toInt + mid_y.toInt
      val x1 = x2 - 1

      g.drawLine((x1 * scale).toInt, y1, (x2 * scale).toInt, y2)

      x2 = x2 + 1
      prev_point = point
    }
  }

  def plot(audioBatchList: List[Double]) {
    this.audioInfoList = audioBatchList
  }

}