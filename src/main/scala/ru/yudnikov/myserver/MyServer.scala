package ru.yudnikov.myserver

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import ru.yudnikov.myserver.engine.{UserManager, UserModel}

import scala.io.StdIn

/**
  * Created by Don on 01.06.2017.
  */
object MyServer extends App {
  
  var sessions: Map[UserModel, UUID] = Map()
  
  new UserModel("oleg", "123")
  
  private val appName = "MyServer"
  private val port = 8080
  
  implicit val actorSystem = ActorSystem(appName)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = actorSystem.dispatcher
  
  val route =
    get {
      pathSingleSlash {
        redirect(Uri(s"welcome"), StatusCodes.SeeOther)
      } ~
        path(JavaUUID) {
          id => complete(s"Let's buy something @ $id")
        } ~
        path(JavaUUID / "orders") {
          id => complete(s"orders @ $id")
        } ~
        path("vendor") {
          complete("Let's sell something...")
        } ~
        path("runner") {
          complete("Let's deliver something...")
        } ~
        path("welcome") {
          complete((new AuthView).response)
        } ~
        path("recovery") {
          complete("recovering password...")
        }
    } ~
      post {
        pathSingleSlash {
          sys.error("Posted nothing")
        } ~
          path("welcome") {
            formFields('login, 'password) {
              (l, p) =>
                UserManager.authenticate(l, p) match {
                  case Some(um: UserModel) =>
                    sessions = sessions + (um -> UUID.randomUUID())
                    redirect(Uri(sessions(um).toString), StatusCodes.Found)
                  case _ =>
                    redirect(Uri(s"recovery"), StatusCodes.SeeOther)
                }
            }
          }
      }
  
  val binding = Http().bindAndHandle(route, "localhost", port)
  println(s"$appName online at http://localhost:$port/" + "\n" + "press Enter to quit...")
  StdIn.readLine()
  binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())
  
}
