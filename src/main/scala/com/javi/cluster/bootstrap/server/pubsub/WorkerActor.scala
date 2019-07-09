package com.javi.cluster.bootstrap.server.pubsub
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import com.javi.cluster.bootstrap.server.sharding._

import scala.util.Random

object WorkerActor {
  def props(topic: String, counterCoordinator: ActorRef) =
    Props(new WorkerActor(topic, counterCoordinator))
}

class WorkerActor(topic: String, counterCoordinator: ActorRef) extends Actor with ActorLogging {

  private[this] val mediator = DistributedPubSub(context.system).mediator

  // subscribe to the topic named "content"
  override def preStart(): Unit =
    mediator ! Subscribe(topic, self)

  def receive = {
    case Task(id) =>
      val entityId = generateEntityId
      val payload  = generatePayload
      log.info("Executing Task({}): {} for entityId: {}", id, payload, entityId)
      counterCoordinator ! EntityEnvelope(entityId, payload)
      counterCoordinator ! Get(entityId)
    case SubscribeAck(Subscribe(`topic`, None, `self`)) =>
      log.info("Subscribed to start executing Tasks ...")
  }

  private[this] def generateEntityId =
    (Random.nextFloat() * 10).toInt

  private[this] def generatePayload: Payload = if (Random.nextBoolean()) Increment else Decrement
}
