/*
Created by Shubham Bajpai on 30/11/16.
*/

package main.scala.slaves

import java.util.concurrent.LinkedBlockingQueue
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

class SummerClass(queue: LinkedBlockingQueue[Double]) extends Runnable {
  val batch_size = 3200
  private val sharedQueue = queue

  def run() {
    while (true) {
      try {
        var input_data_list_buffer = new ListBuffer[Double]()
        var element = 0.0
        for (i <- 1 to batch_size) {
          element = sharedQueue.take()
          if (element != null)
            input_data_list_buffer += element
          else
            break
        }
        val resultFromProcessor = summer(input_data_list_buffer.toList)
      } catch {
        case ex: InterruptedException => println("Interrupted Exception")
      }
    }
  }

  //Processor
  def summer(input_data_list: List[Double]): Double = {
    return input_data_list.foldLeft(0.00)(_ + _)
  }

}