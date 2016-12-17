# Swagger UI for Akka-HTTP
[![](https://jitpack.io/v/matfax/akka-http-swagger-ui.svg)](https://jitpack.io/#matfax/akka-http-swagger-ui)

If you are using [Swagger-Akka-Http](https://github.com/swagger-akka-http/swagger-akka-http), this library lets you easily add a route to Swagger UI without adding all the files to your repository.

## Getting Started

1. Add it to your dependencies
2. Add the ``SwaggerUiRoute`` to your global route

```Scala
val routes: Route =
    CorsDirectives.cors()(
        new SwaggerDocService(system).routes ~
        new SwaggerUiRoute("api").route
```

That's it! Swagger UI can now be reached via the context path */api*.

## Customize the Index File

If you want to set a custom path to your JSON, or modify the layout of Swagger UI, you may specify your own index file.
Simply download Swagger UI and extract the *index.html* (other files are not necessary). You may now modify the index and adjust the parameters as you prefer.

Then, provide the resource path to you index file as follows:

```Scala
new SwaggerUiRoute("api", "/api/index.html").route
```
