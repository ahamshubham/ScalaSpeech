/*
Created by Shubham Bajpai on 30/11/16.
*/
package main.scala

import java.util.concurrent.LinkedBlockingQueue

import slaves._

object Master {

  def main(args: Array[String]):Unit = {

    val numberOfQueues: Int = 2

    //Creating a list of shared queues
    val sharedQueues: Map[String,LinkedBlockingQueue[Double]] = Map(
      "listener-summer" -> new LinkedBlockingQueue(),
      "listener-plotter" -> new LinkedBlockingQueue(),
      "listener-recorder" -> new LinkedBlockingQueue()
    )

    //Creating threads
    val listenerThread = new Thread(new ListenerClass(sharedQueues("listener-summer"),sharedQueues("listener-plotter"),sharedQueues("listener-recorder")))
    val summerThread = new Thread(new SummerClass(sharedQueues("listener-summer")))
    val plotterThread = new Thread(new RealTimeAudioPlotterClass(sharedQueues("listener-plotter")))
    val recorderThread = new Thread(new RecorderClass(sharedQueues("listener-recorder")))

    //Starting threads
    listenerThread.start()
    summerThread.start()
    plotterThread.start()
    recorderThread.start()
  }

}
