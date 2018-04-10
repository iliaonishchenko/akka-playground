import akka.actor.{ActorSystem, FSM, Props, Stash}
import akka.pattern.{after, ask, pipe}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

// Finite State Machine example

import actorSystem.dispatcher

val actorSystem = ActorSystem("MyActorSystem")

def getToken(): Future[String] = {
  after(100.millis, actorSystem.scheduler)(Future.successful("token"))
}

sealed trait Message
case class Inc(i: Int) extends Message
case object Get extends Message
case object UpdateToken extends Message
case object DoUpdate

case class SetToken(token: String)
case class Count(i: Int, token: String)
case class Data(i: Int, token: Option[String])

sealed trait State
case object Initializing extends State
case object Ready extends State


class Counter extends FSM[State, Data] with Stash {

  startWith(Initializing, Data(0, None))

  onTransition {
    case _ => unstashAll()
  }

  val UPDATE_TOKEN = "UPDATE_TOKEN"

  onTransition {
    case _ -> Initializing =>
      self ! DoUpdate
      setTimer(UPDATE_TOKEN, DoUpdate, 1.minute, repeat = true)
    case Initializing -> _ =>
      cancelTimer(UPDATE_TOKEN)
  }

  when(Initializing) {
    case Event(DoUpdate, _) =>
      getToken().map(SetToken).pipeTo(self)
      stay()
    case Event(SetToken(token), data) =>
      goto(Ready) using data.copy(token = Some(token))
    case _ =>
      stash()
      stay()
  }

  when(Ready) {
    case Event(_, Data(_, None)) =>
      stash()
      goto(Initializing)
    case Event(Inc(i), data @ Data(cnt, _)) =>
      stay using data.copy(cnt + i)
    case Event(Get, Data(cnt, Some(token))) =>
      sender() ! Count (cnt, token)
      stay()
    case Event(UpdateToken, _) =>
      goto(Initializing)
  }

  onTermination {
    case StopEvent(reason, state, data) =>
      println(s"onTermination: $reason, $state, $data")
  }

  initialize()
}


val counter = actorSystem.actorOf(Props[Counter], "defaultCounter")

counter ! Inc(5)
counter ! Inc(1)
counter ! Inc(1)
counter ! Inc(1)

implicit val timeout: Timeout = 1.second

val result = (counter ? Get).mapTo[Count]

println(s"Result: ${Await.result(result, 1.minute)}")

Await.result(actorSystem.terminate(), 2.minute)













