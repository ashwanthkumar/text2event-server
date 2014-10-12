package in.ashwanthkumar

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.joestelmach.natty._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.logging.Logger
import com.twitter.util.{Await, Future}
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpRequest, HttpResponse}

import scala.collection.JavaConversions._
import scala.util.Properties

object Server {
  def main(args: Array[String]) {
    val port = Properties.envOrElse("PORT", "8080").toInt
    println("Starting on port: " + port)

    val server = Http.serve(":" + port, new DateParser)
    Await.ready(server)
  }
}

class DateParser extends Service[HttpRequest, HttpResponse] {
  private[this] val log = Logger(classOf[DateParser])
  private[this] val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)
  val writer = mapper.writer

  def apply(request: HttpRequest): Future[HttpResponse] = {
    request.getMethod match {
      case HttpMethod.POST =>
        val inputContent = Request(request).getContentString()
        parseForDate(inputContent)
      case HttpMethod.GET =>
        val inputContent = Request(request).getParam("q", "")
        parseForDate(inputContent)
      case _ =>
        val response = Response()
        response.setStatusCode(404)
        Future(response)
    }
  }

  def parseForDate(inputContent: String): Future[HttpResponse] = {
    val contentToReturn = inputContent.split("\n").map(_.replaceAll("\\p{Punct}", "")).flatMap(parse)
    val response = Response()
    response.setStatusCode(200)
    response.setContentTypeJson()
    response.setContentString(writer.writeValueAsString(Map("result" -> contentToReturn)))
    Future(response)
  }

  def parse(content: String) = {
    val parser = new Parser()
    log.info(s"trying to parse $content")
    val groups = parser.parse(content)
    groups.filter(_.getDates.headOption.isDefined).map { group =>
      val date = group.getDates.head
      val location = group.getParseLocations.get("parse").head.getText
      Map("date" -> date, "text" -> location)
    }
  }

}
