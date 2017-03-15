/*
Created by Shubham Bajpai on 30/11/16.
*/
package main.scala

import java.util.concurrent.LinkedBlockingQueue
import main.scala.slaves._

object Master {

  def main(args: Array[String]):Unit = {

    val numberOfQueues: Int = 2

    //Creating a list of shared queues
    val sharedQueue: List[LinkedBlockingQueue[Double]] = List.fill(numberOfQueues)(new LinkedBlockingQueue())

    //Creating threads
    val listenerThread = new Thread(new ListenerClass(sharedQueue(0),sharedQueue(1)))
    val summerThread = new Thread(new SummerClass(sharedQueue(0)))
    val plotterThread = new Thread(new RealTimeAudioPlotterClass(sharedQueue(1)))

    //Starting threads
    listenerThread.start()
    summerThread.start()
    plotterThread.start()
  }

}
