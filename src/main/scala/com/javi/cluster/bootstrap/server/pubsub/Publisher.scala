package com.javi.cluster.bootstrap.server.pubsub
import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

case class Task(id: Int)

object Publisher {
  def props(topic: String) = Props(new Publisher(topic))
}

class Publisher(topic: String) extends Actor with ActorLogging {

  val mediator = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case task @ Task(id) =>
      log.info(s"Publish Task with id:$id")
      mediator ! Publish(topic, task)
  }
}
