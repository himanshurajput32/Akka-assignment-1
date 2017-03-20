import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask
import scala.io.Source._
import scala.concurrent.ExecutionContext.Implicits.global

case class Line(string: String)

case class StringProcessedMsg(words: Integer)

case object StartProcessFileMsg

class WordPerLineCounterActor extends Actor {
  def receive = {
    case Line(string) => {
      val wordsInLine = string.split(" ").length
      sender ! StringProcessedMsg(wordsInLine)
    }
  }
}


class WordCounterActor(filename: String) extends Actor {

  var running = false
  var totalLines = 0
  var linesProcessed = 0
  var result = 0
  var fileSender: Option[ActorRef] = None


  def receive = {
    case StartProcessFileMsg => {
      if (running) {

        println("Warning: duplicate start message received")
      } else {
        running = true
        fileSender = Some(sender)

        fromFile(filename).getLines.foreach { line =>
          context.actorOf(Props[WordPerLineCounterActor]) ! Line(line)
          totalLines += 1
        }
      }
    }
    case StringProcessedMsg(words) => {
      result += words
      linesProcessed += 1
      if (linesProcessed == totalLines) {
        fileSender.foreach(_ ! result)
      }
    }
  }
}

object AkkaAssignment1 extends App {

  val system = ActorSystem("AkkaAssignment1")
  val actor = system.actorOf(Props(classOf[WordCounterActor], "src/main/resources/abc.txt"))
  implicit val timeout = Timeout(25 seconds)
  val future = actor ? StartProcessFileMsg
  future.map { result =>
    println("Total number of words " + result)
  }
}
