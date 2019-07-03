package com.javi.cluster.bootstrap
import cats.effect._
import cats.implicits._
import com.javi.cluster.bootstrap.server.Backend
import org.slf4j.LoggerFactory

object Main extends IOApp  {

  val logger = LoggerFactory.getLogger(Main.getClass)

  def run(args: List[String]): IO[ExitCode] = {

    val server = for {
      _      <- IO(logger.debug("Starting Server ..."))
      server <- Backend.start
      _      <- IO(logger.debug("Started Server ..."))
    } yield server

    server.as(ExitCode.Success)
  }
}
