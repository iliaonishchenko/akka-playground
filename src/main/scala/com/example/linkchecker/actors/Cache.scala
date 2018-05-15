package com.example.linkchecker.actors

import akka.actor.Actor
import com.example.linkchecker.WebClient
import com.example.linkchecker.actors.Protocol.{CommonResult, Get}

class Cache extends Actor {
  import akka.pattern.pipe
  import context.dispatcher

  var cache = Map.empty[String, String]

  override def receive: Receive = {
    case Get(url) =>
      if (cache.contains(url)) sender ! cache(url)
      else {
        val client = sender
        WebClient.get(url).map(body => CommonResult(client, url, body)).pipeTo(self)
      }
    case CommonResult(client, url, body) =>
      cache += url -> body
      client ! body
  }
}
