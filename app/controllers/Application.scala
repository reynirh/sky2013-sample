package controllers

import play.api._
import play.api.mvc._
import scala.xml._
import scala.io._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.concurrent.duration._
import play.mvc.Http.Response
import play.api.libs.ws.WS

object Application extends Controller {

  // dæmi 1

  def helloSky = Action {
    Ok(views.html.basic("Halló Ský"))
  }








  // dæmi 2

  def vedurFrettir = Action {
    val results: List[weather] = woeids.map(getWeatherInfo)
    Ok(views.html.index("Halló Ský 2013!!", results))
  }








  // dæmi 3

  def descriptionUpdate(woeid: Integer) = Action.async {
    WS.url(weatherUrl + woeid ).get().map { response =>
      Ok ((response.xml \\ "description").text)
    }
  }





  // auðkenni nokkra staða á Íslandi.
  val woeids: List[Integer] = List(980027,980389,980070,980413,980266,980380,980551,980108)
  // url fyrir veðurþjónustu
  val weatherUrl:String = "http://weather.yahooapis.com/forecastrss?u=c&w="

  /**
   * Weather er case class sem getur haldið utan um nokkur
   * valinkunn eigindi af veðurlýsingum yahoo.com
   */
  case class weather (desc : String,
                      city: String,
                      temp: String,
                      woeid: Integer)

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

  // dæmi 4
  // aðgerð sem skilar því veðurobjecti sem er með hærra hitastig.
  def max(s1: weather, s2: weather): weather = if (s1.temp > s2.temp) s1 else s2

  // aðgerð sem finnur heitasta staðinní listanum.
  def vedurMaxTemp = Action {
    val results: List[weather] = (woeids.map(getWeatherInfo))
    val maxtemp: weather = results.reduceLeft(max)
    Ok(maxtemp.city + ":"+ maxtemp.temp +":"+ maxtemp.woeid)
  }


  // aðgerð sem sýnir alternative leið til að loopa í gegnum lista
  def vedurFrettir1 = Action {
    for (x <- woeids)
    {
      getWeatherInfo(x)
    }
    Ok(views.html.index("Halló Ský 2013!!", null))
  }


  // dæmi 1.b:
  // listi með nöfnum
  val names: List[String] = List("reynir", "stefán", "Eiríkur")
  // function til að laga höfuðstafi í streng
  def cap(str:String): String = str.capitalize
  // action sem sýnir list.mkString í virkni.
  def helloSpeakers = Action {
    val result: List[String] = names.map(cap)
    Ok(views.html.index("Halló "+  result.mkString(", "), null))
  }


}