import akka.actor.{Actor, ActorSystem, Props}


val actorSystem = ActorSystem("MyActorSystem")

class MathPoetActor extends Actor {
  import context._
  def matematicianReceive: Receive = {
    case i:Int => println(s"$i + $i = ${i + i}")
    case s: String if s == "become a poet!" =>
      println("going to be a poet!")
      become(poetReceive)
    case _ => println("i don't know what to do")
  }

  def poetReceive: Receive = {
    case s: String if s == "become a matematician" =>
      println("going to be a math!")
      become(matematicianReceive)
    case s: String => println(s"$s and ${s.reverse}")
    case _ => println("i don't know what to do!!!")
  }

  override def receive: Receive = poetReceive
}


val mathPoetActor = actorSystem.actorOf(Props[MathPoetActor], "firstLittleActor")

//poet actor
mathPoetActor ! 100
mathPoetActor ! "hello"
mathPoetActor ! "become a matematician"
//math actor
mathPoetActor ! 100
mathPoetActor ! "hello"
mathPoetActor ! "become a poet!"
//poet actor
mathPoetActor ! 100
mathPoetActor ! "hello"
