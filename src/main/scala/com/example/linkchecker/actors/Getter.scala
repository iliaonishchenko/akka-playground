package com.example.linkchecker.actors

import java.util.concurrent.Executor

import akka.actor.Actor
import com.example.linkchecker.actors.Protocol.{Abort, Check, Done}
import com.example.linkchecker.{Example3, WebClient}

import scala.concurrent.ExecutionContext

class Getter(url: String, depth: Int) extends Actor {

  implicit val exec =
    context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  import akka.pattern.pipe

  WebClient.get(url).pipeTo(self)

  override def receive: Receive = {
    case body: String =>
      for (link <- Example3.findLinks(body))
        context.parent ! Check(link, depth)
      stop()
    case Abort => stop()
    case _: Exception => stop()
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
}
