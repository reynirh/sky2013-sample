package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import scala.concurrent.Future
import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import scala.io._
import play.api.libs.concurrent.Akka
import play.api.Play.current

import controllers.Application.weather


object AsyncDemo extends Controller {


  // dæmi 5 Actor based tékk á veðrinu í Reykjavík.

  def helloAkka = Action {
    val actorname = "myactor"+ System.nanoTime()

    val myActor = Akka.system.actorOf(Props[HelloActor], name = actorname)

    val future = myActor.ask(980389)(5 seconds) // tékka á veðrinu í Reykjavík með 5 sek timeout.
    val result = (future).mapTo[List[String]]

    Logger.info("results.value: " + result.value)
    Ok("test:" +result.value)
  }


  class HelloActor extends Actor {
    def receive = {
      case woeid: Integer => {

        val weatherResponse = scala.xml.XML.load(Application.weatherUrl + woeid  )
        // hér er smá bið til að tryggja að sýnidæmið taki nokkrar sekúndur.
        Thread.sleep(5000)

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

}


