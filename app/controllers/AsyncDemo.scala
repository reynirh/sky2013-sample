package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import scala.concurrent.Future
import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import scala.io._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import controllers.Application.weather


/**
  * Created with IntelliJ IDEA.
 * User: reynir
 * Date: 17/11/13
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
object AsyncDemo extends Controller {



  def helloAkka = Action {
    val actorname = "myactor"+ System.nanoTime()

    val myActor = Akka.system.actorOf(Props[HelloActor], name = actorname)

    val future = myActor.ask(980389)(5 seconds) // tékka á veðrinu í Reykjavík með 5 sek timeout.
    val result = (future).mapTo[List[String]]

    Logger.info("results.value: " + result.value)
    Ok("test:" +result.value)
  }


  val weatherUrl:String = "http://weather.yahooapis.com/forecastrss?u=c&w="

  class HelloActor extends Actor {
    def receive = {
      case woeid: Integer => {
      Logger.info("woeid: "+ woeid)
      val weatherResponse = scala.xml.XML.load(weatherUrl + woeid  )
      val desc: String = (weatherResponse \\  "description"  ).text
      val city: String = (weatherResponse \\ "location" \\"@city").text
      val temp: String = (weatherResponse \\ "condition" \\"@temp").text
      Logger.info("read "+ city+ ":"+ temp)
      sender ! weather (desc,city,temp, woeid)
      }
      case none => NoContent

    }
  }


}


