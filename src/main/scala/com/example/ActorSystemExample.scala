package com.example

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._



object ActorSystemExample extends App {

  val actorSystem = ActorSystem("ExampleActorSystem")

  implicit val timeout = Timeout(10.seconds)

  val sumActor = actorSystem.actorOf(Props[SumActor])
  val fibActor = actorSystem.actorOf(Props[FibActor])
  println(sumActor.path)

  val fut = (fibActor ? 10).mapTo[Int]

  println(Await.result(fut, 10.seconds))


  class SumActor extends Actor {
    var sum = 0
    override def receive: Receive = {
      case x: Int =>
        sum = sum + x
        println(s"curr sum is: $sum")
      case _ =>
        println("Some strange situation")

    }
  }

  class FibActor extends Actor {
    def fib(i: Int): Int = {
      if (i == 1 || i == 2) 1
      else fib(i-2) + fib(i-1)
    }

    override def receive: Receive = {
      case x: Int =>
        sender ! fib(x)
      case _ => println("I do not know")
    }

  }
}
