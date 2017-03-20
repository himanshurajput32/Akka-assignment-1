import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationDouble

/**
  * Created by knoldus on 20/3/17.
  */
case class Seat(seats: Int)

class BookMyShow extends Actor {
  //var totalSeats=50
  var availableSeats = 50

  override def receive = {
    case Seat(seats) => {
      if (availableSeats >= seats) {
        availableSeats = availableSeats - seats
        sender() ! "seat Booked"
        println(availableSeats)
      }
      else if (availableSeats == 0) {
        sender() ! "HouseFull"
      }
      else {
        sender() ! "Seats Not available"
      }

    }

  }
}


object BookMyShow extends App {
  implicit val timeout = Timeout(1000 seconds)
  val system = ActorSystem("BookMyshow")
  val props = Props[BookMyShow]
  val ref = system.actorOf(props)
  val list = List(Seat(10), Seat(15), Seat(15), Seat(13), Seat(8))
  val res = list.map(x => ref ? x)
  res.map(x => println(x))


}