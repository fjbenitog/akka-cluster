package com.javi.cluster.bootstrap.server.sharding

import akka.actor._
import akka.cluster.sharding._

object CounterCoordinator {

  def props = Props(new CounterCoordinator)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case EntityEnvelope(id, payload) => (id.toString, payload)
    case msg @ Get(id)               => (id.toString, msg)
  }

  val numberOfShards = 100

  val extractShardId: ShardRegion.ExtractShardId = {
    case EntityEnvelope(id, _)       => (id % numberOfShards).toString
    case Get(id)                     => (id % numberOfShards).toString
    case ShardRegion.StartEntity(id) =>
      // StartEntity is used by remembering entities feature
      (id.toLong % numberOfShards).toString
  }

  def startCounterSharding(actorSystem: ActorSystem) =
    ClusterSharding(actorSystem).start(
      typeName = "Counter",
      entityProps = Counter.props,
      settings = ClusterShardingSettings(actorSystem),
      extractEntityId = extractEntityId,
      extractShardId = extractShardId
    )
}

class CounterCoordinator extends Actor with ActorLogging {

  import CounterCoordinator._

  private[this] val shardRegion = startCounterSharding(context.system)

  override def receive: Receive = {
    case envelope: EntityEnvelope =>
      shardRegion ! envelope
    case msg: Get =>
      shardRegion ! msg
    case Count(entityId, count) =>
      log.info(s"Count $count for Entity:$entityId")
  }
}
