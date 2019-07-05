package com.javi.cluster.bootstrap.server.singleton
import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.singleton._

import scala.concurrent.duration._

case object ExecuteTask

object SchedulerActor {
  def props = Props(new SchedulerActor)

  def start(implicit contextParent: ActorContext): ActorRef = {

    val singletonProps = ClusterSingletonManager.props(
      singletonProps = SchedulerActor.props,
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(contextParent.system)
    )

    contextParent.actorOf(singletonProps, name = "Scheduler-Tasks")

    contextParent.actorOf(
      ClusterSingletonProxy.props(
        contextParent.self.path.toStringWithoutAddress,
        ClusterSingletonProxySettings(contextParent.system)
      )
    )
  }
}

class SchedulerActor extends Actor with ActorLogging {

  import context.dispatcher

  var counter: Int = 0
  private[this] val task = context.system.scheduler.schedule(
    0 millis,
    15 seconds,
    self,
    ExecuteTask
  )

  override def postStop() =
    task.cancel()

  override def receive: Receive = {
    case ExecuteTask =>
      counter += 1
      log.info(s"Task executed $counter times")
    case PoisonPill =>
      log.info("Terminating Scheduler Task")

  }
}
