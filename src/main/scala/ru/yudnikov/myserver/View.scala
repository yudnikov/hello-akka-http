package ru.yudnikov.myserver

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, ResponseEntity}

import scala.xml.Node

/**
  * Created by Don on 01.06.2017.
  */
trait View extends {
  
  val node: Node
  
  override def toString: String = {
    node.toString()
  }
  
  def entity: ResponseEntity = HttpEntity(ContentTypes.`text/html(UTF-8)`, toString)
  
  def response: HttpResponse = HttpResponse(entity = entity)
  
}

class AuthView extends View {
  
  override val node: Node =
    <html lang="en">
      <head>
        <meta charset="UTF-8"/>
        <title>Title</title>
      </head>
      <body>
        <section id="auth">
          <form method="post">
            <input type="text" name="login"/>
            <input type="password" name="password"/>
            <input type="submit"/>
          </form>
        </section>
      </body>
    </html>
  
}

class IndexView extends View {
  
  override val node: Node =
    <html lang="en">
      <head>
        <meta charset="UTF-8"/>
        <title>Title</title>
      </head>
      <body>
        <section id="content">
          <a href="/buyer">I'm buyer</a>
          <a href="/vendor">I'm vendor</a>
          <a href="/runner">I'm runner</a>
        </section>
      </body>
    </html>
  
}

object IndexView {
  
  def response: HttpResponse = (new IndexView).response
  
}