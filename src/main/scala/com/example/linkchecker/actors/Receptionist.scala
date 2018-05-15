package com.example.linkchecker.actors

import akka.actor.{Actor, ActorRef, Props}
import com.example.linkchecker.actors.Protocol._

class Receptionist extends Actor {

  var reqNo = 0

  def runNext(queue: Vector[Job]): Receive = {
    reqNo += 1
    if (queue.isEmpty) waiting
    else {
      val controller = context.actorOf(Props[Controller], s"c$reqNo")
      controller ! Check(queue.head.url, 2)
      running(queue)
    }
  }

  def enqueueJob(queue: Vector[Job], job: Job): Receive = {
    if (queue.size > 3) {
      sender() ! Failed(job.url)
      running(queue)
    } else running(queue :+ job)
  }

  override def receive: Receive = waiting

  val waiting: Receive = {
    case Get(url) => context.become(runNext(Vector(Job(sender(), url))))
  }

  def running(queue: Vector[Job]): Receive = {
    case Result(links) =>
      val job = queue.head
      job.client ! ReceptionistResult(job.url, links)
      context.stop(sender())
      context.become(runNext(queue.tail))
    case Get(url) =>
      context.become(enqueueJob(queue, Job(sender(), url)))
  }

  case class Job(client: ActorRef, url: String)

}
