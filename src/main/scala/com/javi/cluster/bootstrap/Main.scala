package com.javi.cluster.bootstrap
import cats.effect._
import cats.implicits._
import com.javi.cluster.bootstrap.server.ClusterServer
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Main extends IOApp {

  val logger = LoggerFactory.getLogger(Main.getClass)

  def run(args: List[String]): IO[ExitCode] = {

    val config = ConfigFactory.parseString(s"""
        akka.remote.netty.tcp.port=${args.head}
        """).withFallback(ConfigFactory.load())

    val server = for {
      _      <- IO(logger.debug("Starting Server ..."))
      server <- ClusterServer.start(config)
      _      <- IO(logger.debug("Started Server ..."))
    } yield server

    server.as(ExitCode.Success)
  }
}
