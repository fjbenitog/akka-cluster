package com.javi.cluster.bootstrap.server.sharding

import akka.actor.{ActorLogging, Props, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.PersistentActor

import scala.concurrent.duration._
sealed trait Payload
case object Increment extends Payload
case object Decrement extends Payload
final case class Get(counterId: Long)
final case class EntityEnvelope(id: Long, payload: Payload)
final case class Count(id: Long, value: Int)

case object Stop
final case class CounterChanged(delta: Int)

object Counter {
  def props = Props(new Counter)
}

class Counter extends PersistentActor with ActorLogging {

  var count = 0

  override def persistenceId: String = "Counter-" + self.path.name

  lazy val entityId: String = self.path.name

  override def preStart(): Unit = {
    log.info("Added Counter with Id: {}",entityId)
    context.setReceiveTimeout(120 seconds)
  }

  override def postStop(): Unit = {
    log.info("Removed Counter with Id: {}",entityId)
  }

  def updateState(event: CounterChanged): Unit =
    count += event.delta

  override def receiveRecover: Receive = {
    case evt: CounterChanged => updateState(evt)
  }

  override def receiveCommand: Receive = {
    case Increment      => persist(CounterChanged(+1))(updateState)
    case Decrement      => persist(CounterChanged(-1))(updateState)
    case Get(_)         => sender() ! Count(entityId.toLong, count)
    case ReceiveTimeout => context.parent ! Passivate(stopMessage = Stop)
    case Stop           => context.stop(self)
  }
}
