import BookMyShow.availableSeats
import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.FromConfig
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationDouble

/**
  * Created by knoldus on 20/3/17.
  */
case class Seat(seat: Int)

class BookMyShow extends Actor {

  override def receive = {
    case Seat(seat) =>
      if (availableSeats >= seat) {
        availableSeats -= seat
        println("seat booked   "+self.path)
      }
      else if (availableSeats == 0) {
        println("Housefull  "+self.path)
      }
      else {

        println("Seats not available    "+self.path)
      }

    }


}


object BookMyShow extends App {
  var availableSeats=1
  val config = ConfigFactory.parseString(
    """
      |akka.actor.deployment {
      | /poolRouter {
      |   router = round-robin-pool
      |   resizer {
      |      pressure-threshold = 0
      |      lower-bound = 2
      |      upper-bound = 5
      |      messages-per-resize = 1
      |    }
      | }
      |}
    """.stripMargin
  )
  implicit val timeout = Timeout(1000 seconds)
  val system = ActorSystem("BookMyshow", config)
  val router = system.actorOf(FromConfig.props(Props[BookMyShow]), "poolRouter")

  router ! Seat(1)
  router ! Seat(1)
  router ! Seat(1)
  router ! Seat(1)
  router ! Seat(1)
  router ! Seat(1)
  router ! Seat(1)
  router ! Seat(1)
  router ! Seat(1)


}