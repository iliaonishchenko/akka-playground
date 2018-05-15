package com.example.linkchecker

import java.util.concurrent.Executor

import com.ning.http.client.AsyncHttpClient

import scala.concurrent.{ExecutionContext, Future, Promise}

object Example {

  val client = new AsyncHttpClient()
  def get(url: String): String = {
    val response = client.prepareGet(url).execute().get()
    if(response.getStatusCode < 400){
      response.getResponseBodyExcerpt(131072)
    } else throw new RuntimeException(response.getStatusCode.toString)
  }
}

object WebClient {
  private val client = new AsyncHttpClient()
  def get(url: String)(implicit ec: Executor): Future[String] = {
    val f = client.prepareGet(url).execute
    val p = Promise[String]()

    f.addListener(new Runnable {
      def run = {
        val response = f.get()
        if (response.getStatusCode < 400) response.getResponseBodyExcerpt(131072)
        else p.future.failed
      }
    }, ec)
    p.future
  }
}

object Example3 {
  val A_TAG = "(?i)<a ([^>]+)>.+?</a>".r
  val HREF_ATTR = """\s*(?i)href\s*=\s*(?:"([^"]*)"|'([^']*)'|([^'">\s]+))""".r

  def findLinks(body: String): Iterator[String] = {
    for {
      anchor <- A_TAG.findAllMatchIn(body)
      HREF_ATTR(dquot, quot, bare) <- anchor.subgroups
    } yield {
      if (dquot != null) dquot
      else if (quot != null) quot
      else bare
    }
  }
}
