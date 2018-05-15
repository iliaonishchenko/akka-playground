package com.example.linkchecker.actors

import akka.actor.{Actor, Props, ReceiveTimeout}
import com.example.linkchecker.WebClient
import com.example.linkchecker.actors.Protocol.{Failed, Get, ReceptionistResult}

import scala.concurrent.duration._

class Main extends Actor {

  val receptionist = context.actorOf(Props[Receptionist], "receptionist")

  receptionist ! Get("http://www.google.com")

  context.setReceiveTimeout(10.seconds)


  override def receive: Receive = {
    case ReceptionistResult(url, set) =>
      println(set.toVector.sorted.mkString(s"Results for '$url': \n", "\n", "\n"))
    case Failed(url) =>
      println(s"Failed to fetch $url")
    case ReceiveTimeout => context.stop(self)
  }

  override def postStop(): Unit = {
//    WebClient.shutdown()
  }

}
