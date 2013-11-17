package controllers

import play.api._
import play.api.mvc._
import scala.xml._
import scala.io._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.libs.Akka
import scala.concurrent.Future
import scala.concurrent.duration._
import play.mvc.Http.Response
import play.api.libs.ws.WS
import akka.actor.{Actor, Props}
import play.cache.Cache

object Application extends Controller {



  def helloSky = Action {
    Ok(views.html.basic("Halló Ský!"))
  }



  val names: List[String] = List("Reynir þór hübner", "steFán Baxter", "EIríkur")
  def lower(str:String): String = str.capitalize

  def helloSpeakers = Action {
    val result: List[String] = names.map(lower)
    Ok(views.html.index("Halló "+  result.mkString(","), null))
  }

  def vedurFrettir = Action {
    val results: List[weather] = woeids.map(getWeatherInfo)
    Ok(views.html.index("Halló Ský 2013!!", results))
  }


  //smá map-reduce
  def vedurMaxTemp = Action {
    val results: List[weather] = (woeids.map(getWeatherInfo))
    val maxtemp: weather = results.reduceLeft(max)
    Ok(maxtemp.city + ":"+ maxtemp.temp +":"+ maxtemp.woeid)
    //Ok(views.html.index("Halló Ský 2013!!", results))
  }

  def max(s1: weather, s2: weather): weather = if (s1.temp > s2.temp) s1 else s2




  def descriptionUpdate(woeid: Integer) = Action.async {
    WS.url(weatherUrl + woeid ).get().map { response =>
      Ok((response.xml \\ "description").text)
    }
  }







  val woeids: List[Integer] = List(980027,980389,980070,980413,980266,980380,980551, 980108)
  val weatherUrl:String = "http://weather.yahooapis.com/forecastrss?u=c&w="

  /**
   * Fall sem sækir veður til veðurþjónustu yahoo eftir staðarauðkenni (WOEID).
   * Skilar tuple sem inniheldur stað og hitastig á celcius skala.
   * @param woeid
   * @return
   */
  def getWeatherInfo(woeid:Integer ): weather =
  {
    val weatherResponse = scala.xml.XML.load(weatherUrl +woeid  )
    val desc: String = (weatherResponse \\  "description"  ).text
    val city: String = (weatherResponse \\ "location" \\"@city").text
    val temp: String = (weatherResponse \\ "condition" \\"@temp").text
    Logger.info("read "+ city+ ":"+ temp)
    weather (desc,city,temp, woeid)

  }



  def vedurFrettir1 = Action {
    for (x <- woeids)
    {
      getWeatherInfo(x)
    }
    Ok(views.html.index("Halló Ský 2013!!", null))
  }


  case class weather (desc : String, city: String, temp: String, woeid: Integer)

     /*
  class fetchWeatherActor(placeId: String) extends Actor {
           def receive = {
             case msg => Logger.info("message :"+ msg)
           }
  }

  val myActor = Akka.system.actorOf(Props[fetchWeatherActor], name = "weatherActor")
       */

}