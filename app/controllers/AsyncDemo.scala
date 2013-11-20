package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import scala.concurrent._
import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.json._
import scala.io._
import play.api.libs.concurrent.Akka
import play.api.Play.current

import controllers.Application.weather
import java.text.SimpleDateFormat
import java.util.Date
import akka.routing.Broadcast
import akka.io.Udp.Received


object AsyncDemo extends Controller {

  // dæmi 5 Actor based tékk á veðrinu í Reykjavík.

  def helloAkka = Action {
    val actorname = "myactor"+ System.nanoTime()
    val myActor = Akka.system.actorOf(Props[WeatherActor], name = actorname)
    val future = myActor.ask(980389)(5 seconds) // tékka á veðrinu í Reykjavík með 5 sek timeout.
    val result = (future).mapTo[List[String]]
    Logger.info("results.value: " + result.value)
    Ok("non blocking result: " +result.value)
  }


  class WeatherActor extends Actor {
    def receive = {
      case woeid: Integer => {
        val weatherResponse = scala.xml.XML.load(Application.weatherUrl + woeid  )
        // hér er smá bið til að tryggja að sýnidæmið taki nokkrar sekúndur.
        Thread.sleep(3000)
        val desc: String = (weatherResponse \\  "description"  ).text
        val city: String = (weatherResponse \\ "location" \\"@city").text
        val temp: String = (weatherResponse \\ "condition" \\"@temp").text
          // niðurstaðan skrifast í logg hér - en gæti skrifast út t.d.
        Logger.info("read "+ city+ ":"+ temp)
        sender ! weather (desc,city,temp, woeid)
      }
      //case none => (NoContent)
    }
  }

  def comet = Action {

    val events = Enumerator(
      """<script>console.log('reynir 1')</script>""",
      """<script>console.log('reynir 2')</script>""",
      """<script>console.log('reynir 3')</script>"""
    )
    Ok.stream(events >>> Enumerator.eof).as(HTML)
  }
}




