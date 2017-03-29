package main.scala.slaves

import java.io.{File, PrintWriter}
import java.util.concurrent.LinkedBlockingQueue

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/*
 Created by Shubham Bajpai on 27/3/17.
*/

class RecorderClass(input_queue: LinkedBlockingQueue[Double]) extends Runnable {

  val worker_result_queue: LinkedBlockingQueue[Future[Double]] = new LinkedBlockingQueue()

  val recording_file_path = "tmp/recordings/recording_" + System.currentTimeMillis() + ".txt"
  val recording_file = new File(recording_file_path)
  recording_file.getParentFile().mkdirs()
  val data_writer = new PrintWriter(recording_file, "UTF-8")

  def run() = {

    val controllerThread = new Thread(new Runnable {
      override def run(): Unit = controller()
    })

    val combinerThread = new Thread(new Runnable {
      override def run(): Unit = combiner()
    })

    controllerThread.start()
    combinerThread.start()
  }

  def controller(): Unit = {
    while (true) {
      val element = input_queue.take()
      if (element != null) {
        worker_result_queue.put(as_it_is(element))
      }
    }
  }

  def as_it_is(x: Double): Future[Double] = {
    Future(x)
  }

  def combiner(): Unit = {
    val num_of_elements = 10
    while (true) {
      var i = 1
      var result: String = ""
      while (i <= num_of_elements) {
        val element = worker_result_queue.take()
        if (element != null) {
          i = i + 1
          result = result + Await.result(element, Duration(1000000, "millis")) + " "
        }
      }
      data_writer.print(result)
    }
  }

}
