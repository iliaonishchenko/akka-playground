package com.example.linkchecker.actors

import akka.actor.ActorRef

object Protocol {

  case object Done
  case object Abort
  case class Check(link: String, depth: Int)
  case class Result(cache: Set[String])
  case class CommonResult(actor: ActorRef, url: String, body: String)
  case class Get(link: String)
  case class Failed(str: String)
  case class ReceptionistResult(url: String, links: Set[String])

}
