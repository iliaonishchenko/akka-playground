package com.example.linkchecker.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ReceiveTimeout}
import akka.util.Timeout
import com.example.linkchecker.actors.Protocol.{Abort, Check, Done, Result}

import scala.concurrent.duration._

class Controller extends Actor with ActorLogging {

  import context.dispatcher

  var cache = Set.empty[String]
  var children = Set.empty[ActorRef]

  context.system.scheduler.scheduleOnce(10.seconds, self, Timeout)

  override def receive: Receive = {
    case Check(url, depth) =>
      log.debug("{} checking {}", depth, url)
      if (!cache(url) && depth > 0) {
        children += context.actorOf(Props(new Getter(url, depth - 1)))
      }
      cache += url
    case Done =>
      children -= sender
      if (children.isEmpty) context.parent ! Result(cache)
    case Timeout => children.foreach(_ ! Abort)
  }
}
