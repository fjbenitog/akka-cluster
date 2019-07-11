package com.javi.cluster.bootstrap.server.singleton

import akka.NotUsed
import akka.actor._
import akka.cluster.singleton._
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl._
import com.javi.cluster.bootstrap.server.pubsub.{Publisher, Task}
import com.javi.cluster.bootstrap.server.stream.AckingReceiver

import scala.concurrent.duration._
import scala.util.Random

sealed trait Execute

case object ExecuteTask   extends Execute
case object ExecuteStream extends Execute

object SchedulerActor {
  def props(topic: String) = Props(new SchedulerActor(topic))

  def start(topic: String)(implicit contextParent: ActorContext): ActorRef = {

    val singletonProps = ClusterSingletonManager.props(
      singletonProps = SchedulerActor.props(topic),
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

class SchedulerActor(topic: String) extends Actor with ActorLogging {

  import context.dispatcher

  implicit lazy val materializer: Materializer = ActorMaterializer()

  private[this] val publisher = context.actorOf(Publisher.props(topic))

  private[this] val receiver = context.actorOf(AckingReceiver.props)

  private[this]val sink = Sink.actorRefWithAck(
    receiver,
    onInitMessage = AckingReceiver.StreamInitialized,
    ackMessage = AckingReceiver.Ack,
    onCompleteMessage = AckingReceiver.StreamCompleted,
    onFailureMessage = AckingReceiver.StreamFailure)

  private[this] var counter: Int = 0
  private[this] val task = context.system.scheduler.schedule(
    0 millis,
    15 seconds,
    self,
    ExecuteTask
  )

  private[this] val stream = context.system.scheduler.schedule(
    27 seconds,
    27 seconds,
    self,
    ExecuteStream
  )

  private[this] def words: Source[String, NotUsed] =
    Source((1 to 100).map(_ => randomString))

  override def postStop() = {
    task.cancel()
    stream.cancel()
  }

  override def receive: Receive = {
    case ExecuteTask =>
      counter += 1
      publisher ! Task(counter)
    case ExecuteStream =>
      words.runWith(sink)
    case PoisonPill =>
      log.info("Terminating Scheduler Task")
      context.stop(self)

  }

  private def randomString: String =
    Random.alphanumeric.take(10).mkString

}
