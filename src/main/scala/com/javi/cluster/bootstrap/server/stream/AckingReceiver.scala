package com.javi.cluster.bootstrap.server.stream
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

object AckingReceiver {
  case object Ack

  case object StreamInitialized
  case object StreamCompleted
  final case class StreamFailure(ex: Throwable)

  def props(implicit materializer: Materializer) = Props(new AckingReceiver)
}

class AckingReceiver(implicit materializer: Materializer) extends Actor with ActorLogging{
  import AckingReceiver._

  override def preStart(): Unit = {
    log.info("Starting AckingReceiver ...")
  }

  private[this] val bufferSize = 10

  private[this] def initializeSource = Source
    .actorRef[String](bufferSize, OverflowStrategy.fail) // note: backpressure is not supported
    .map(word => word.length)
    .reduce(_ + _)
    .toMat(Sink.foreach(count => log.info("Number of letters for the Stream: {}",count)))(Keep.left)
    .run()

  private [this] var ref:Option[ActorRef] = Option.empty

  def receive: Receive = {
    case StreamInitialized =>
      log.info("Stream initialized!")
      sender() ! Ack // ack to allow the stream to proceed sending more elements
      ref = Some(initializeSource)

    case el: String =>
      log.info("Received element: {}", el)
      sender() ! Ack // ack to allow the stream to proceed sending more elements
      ref.foreach(  _ ! el)

    case StreamCompleted =>
      log.info("Stream completed!")
      ref.foreach(  _ ! akka.actor.Status.Success)

    case StreamFailure(ex) =>
      log.error(ex, "Stream failed!")
      ref.foreach(  _ ! akka.actor.Status.Failure(ex))
  }
}
