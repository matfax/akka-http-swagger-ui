package com.matfax.swuiakht

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}

/**
  * Contains the route to Swagger UI.
  * You may use your own index file to replace the path to the Swagger API JSON.
  * Therefore, simply download Swagger UI and copy the `index.html` to your resource folder.
  *
  * @example new SwaggerUiRoute("api", Some("index.html")).route
  *
  * @param prefixPath the URI path prefix to Swagger UI
  * @param replacedIndex ``Some`` path to your own Swagger UI index file, or ``None``
  *
  * @author Matthias Fax
  * @version 0.1.0
  */
class SwaggerUiRoute(prefixPath: String, replacedIndex: Option[String] = None) extends Directives {

  /**
    * The folder name of Swagger UI in the resource folder.
    */
  protected val SwaggerUiResourcePath = "swagger-ui"

  /**
    * Route to Swagger UI.
    */
  val route: Route = {

    val strippedPrefixPath = prefixPath.stripPrefix("/").stripSuffix("/")

    pathPrefix(strippedPrefixPath) {
      pathSingleSlash {
        val indexResource: String = replacedIndex.getOrElse(s"$SwaggerUiResourcePath/index.html").stripPrefix("/")
        getFromResource(indexResource)
      } ~
        pathEnd {
          redirect(uri = s"/$strippedPrefixPath/", redirectionType = StatusCodes.MovedPermanently)
        } ~
        getFromResourceDirectory(SwaggerUiResourcePath)
    }
  }
}
