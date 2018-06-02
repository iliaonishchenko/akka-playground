package com.example.futures.becnh

import java.util.concurrent.TimeUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import org.openjdk.jmh.annotations.{Benchmark, OutputTimeUnit, Scope, State}
import org.openjdk.jmh.infra.Blackhole

import scala.concurrent.{Await, Future}

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.SECONDS)
class FuturesBenchmark {
  val map: Map[String, Int] = (1 to 10000).map(i => s"%i" -> i).toMap

  def withoutFuture(bh: Blackhole) = {
    bh.consume(map)
  }

  @Benchmark
  def withFutureSuccessgul(bh: Blackhole) = {
    val fResult = Future.successful(map)
    val result = Await.result(fResult, 1.second)

    bh.consume(result)
  }

  @Benchmark
  def withFuture(bh: Blackhole) = {
    val fResult = Future(map)
    val result = Await.result(fResult, 1.second)

    bh.consume(result)
  }
}
