package ru.yudnikov.myserver.engine

import java.util.UUID

/**
  * Created by Don on 01.06.2017.
  */
trait Manager[+M <: Model] {
  
  private[this] var models: Map[UUID, M] = Map()
  
  def update(model: Model): Unit = models = models + (model.id -> model.asInstanceOf[M])
  def get(id: UUID): Option[M] = models.get(id)
  def list: List[M] = models.values.toList
  
}

abstract class Model(val manager: Manager[Model]) {
  
  val id: UUID
  manager.update(this)
  
}

case class Sha256(string: String) {
  
  import java.security.MessageDigest
  import java.math.BigInteger
  
  private val md = MessageDigest.getInstance("SHA-256").digest(string.getBytes("UTF-8"))
  private val value = String.format("%064x", new BigInteger(1, md))
  
  override def toString: String = value
  
}

class UserModel(val login: String, val password: Sha256, val id: UUID = UUID.randomUUID()) extends Model(UserManager) {

  def this(login: String, password: String) = this(login, Sha256(password))
  
}

object UserManager extends Manager[UserModel] {
  
  def get(login: String): Option[UserModel] = list.filter(um => um.login == login) match {
    case um :: Nil => Some(um)
    case _ => None
  }
  
  def authenticate(login: String, password: String): Option[UserModel] = {
    get(login) match {
      case Some(um: UserModel) if um.password == Sha256(password) => Some(um)
      case _ => None
    }
  }
  
}

case class OrderModel(buyer: UserModel, amount: Int, id: UUID = UUID.randomUUID()) extends  Model(OrderManager)

object OrderManager extends Manager[OrderModel]

