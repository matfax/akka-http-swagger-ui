package com.matfax.swuiakht

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

import scala.io.{Codec, Source}

/**
  * The specification for ``SwaggerUiRoute``.
  *
  * @author Matthias Fax
  * @version 0.1.0
  */
class SwaggerUiRouteSpec extends WordSpec with Matchers with ScalatestRouteTest {

  /**
    * The default name of the folder that contains the Swagger UI files.
    */
  val DefaultResourcePrefix = "/swagger-ui"

  /**
    * A custom URI prefix.
    */
  val PathPrefix = "/custom-prefix"

  /**
    * The file name of the Swagger UI index file.
    */
  val IndexFile = "index.html"

  /**
    * The file name of the Swagger UI JS file.
    */
  val JsFile = "swagger-ui.js"

  /**
    * A custom index file to test against.
    */
  val CustomIndex = s"/api/$IndexFile"

  /**
    * The file codec for the resource stream.
    */
  implicit val FileCodec: Codec = "UTF-8"

  /**
    * Creates a test suite for a Swagger UI route.
    *
    * @param serviceRoute the route to check
    * @param index        the index file to test against the index file of the route
    */
  def createSwaggerUiRouteTests(serviceRoute: Route, index: String): Unit = {
    "return the targeted Swagger UI index page" in {
      val fileStream = getClass.getResourceAsStream(index)
      val source = Source.fromInputStream(fileStream)
      val fileContent = try source.mkString
      finally source.close()
      Get(s"$PathPrefix/") ~> serviceRoute ~> check {
        responseAs[String] shouldEqual fileContent
      }
    }

    "return the valid Swagger JS file" in {
      val fileStream = getClass.getResourceAsStream(s"$DefaultResourcePrefix/$JsFile")
      val source = Source.fromInputStream(fileStream)
      val fileContent = try source.mkString
      finally source.close()
      Get(s"$PathPrefix/$JsFile") ~> serviceRoute ~> check {
        responseAs[String] shouldEqual fileContent
      }
    }

    "redirect to add a missing concluding slash" in {
      val fileStream = getClass.getResourceAsStream(index)
      val source = Source.fromInputStream(fileStream)
      val fileContent = try source.mkString
      finally source.close()
      Get(s"$PathPrefix") ~> serviceRoute ~> check {
        status.isRedirection() shouldEqual true
        status shouldEqual StatusCodes.MovedPermanently
        responseAs[String] shouldEqual "This and all future requests should be directed to <a href=\"" +
          PathPrefix + "/\">this URI</a>."
      }
    }
  }

  "The common Swagger UI service (URI path: leading slash)" should {

    val commonSwaggerUiRoute: Route = new SwaggerUiRoute(PathPrefix).route

    behave like createSwaggerUiRouteTests(
      serviceRoute = commonSwaggerUiRoute,
      index = s"$DefaultResourcePrefix/$IndexFile"
    )

  }

  "The custom Swagger UI service (index path: leading slash, URI path: leading slash)" should {

    val customSwaggerUiRoute: Route = new SwaggerUiRoute(PathPrefix, Some(CustomIndex)).route

    behave like createSwaggerUiRouteTests(serviceRoute = customSwaggerUiRoute, index = CustomIndex)

  }

  "The custom Swagger UI service (URI path: leading slash)" should {

    val customSwaggerUiRoute: Route = new SwaggerUiRoute(PathPrefix, Some(CustomIndex.stripPrefix("/"))).route

    behave like createSwaggerUiRouteTests(serviceRoute = customSwaggerUiRoute, index = CustomIndex)

  }

  "The common Swagger UI service" should {

    val commonSwaggerUiRoute: Route = new SwaggerUiRoute(PathPrefix.stripPrefix("/")).route

    behave like createSwaggerUiRouteTests(
      serviceRoute = commonSwaggerUiRoute,
      index = s"$DefaultResourcePrefix/$IndexFile"
    )

  }

  "The custom Swagger UI service (index path: leading slash)" should {

    val customSwaggerUiRoute: Route = new SwaggerUiRoute(PathPrefix.stripPrefix("/"), Some(CustomIndex)).route

    behave like createSwaggerUiRouteTests(serviceRoute = customSwaggerUiRoute, index = CustomIndex)

  }

  "The custom Swagger UI service" should {

    val customSwaggerUiRoute: Route =
      new SwaggerUiRoute(PathPrefix.stripPrefix("/"), Some(CustomIndex.stripPrefix("/"))).route

    behave like createSwaggerUiRouteTests(serviceRoute = customSwaggerUiRoute, index = CustomIndex)

  }

}
