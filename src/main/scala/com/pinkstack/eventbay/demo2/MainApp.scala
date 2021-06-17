package com.pinkstack.eventbay.demo2

import java.time.LocalDateTime

object MainApp extends App {
  val family = List("Oto", "Martina", "Rudi", "Tinkara")
  val dobs = List(1987, 1988, 2018, 2016)

  type DOB = Int
  type Name = String

  case class User(dob: DOB, name: Name)

  val users: List[User] = dobs.zip(family).map(t => User(t._1, t._2))


  implicit val userOrdering: Ordering[User] = (x: User, y: User) => x.dob.compare(y.dob)

  class UserAge(val user: User) {
    val age: Int = LocalDateTime.now.getYear - user.dob
  }

  implicit def ageForUser(user: User): UserAge = new UserAge(user)

  val displayNice: User => String = user => s"${user.name} ${user.age}"

  // Finding oldest,...
  val oldest: Option[User] = List(User(2000, "")).maxByOption(_.age)

  oldest match {
    case Some(user) if user.age >= 30 =>
      println(s"I'm the oldest ${user} (over 30)")
    case None =>
      println("Nobody is oldest")
  }

}
