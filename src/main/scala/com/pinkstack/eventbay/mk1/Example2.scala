package com.pinkstack.eventbay.mk1

import java.time.LocalDateTime

object Example2 {
  object Collections {

    val family = List("Martina", "Rudi", "Oto", "Tinkara")
    val dobs = List(1988, 2018, 1987, 2016)

    case class User(dob: Int, name: String)

    val familyDOBs = dobs.zip(family).map(t => User(t._1, t._2))
  }

  def run(): Unit = {
    import Collections._

    println {
      family.map(_.toUpperCase).sorted
    }

    implicit val userOrdering: Ordering[User] = (x: User, y: User) => x.dob.compare(y.dob)


    class UserAge(val user: User) {
      val age: Int = LocalDateTime.now.getYear - user.dob
    }

    implicit def ageForUser(user: User): UserAge = new UserAge(user)

    // def displayNice(user: User): String = s"${user.name} ${user.age}"
    val displayNice: User => String = user => s"${user.name} ${user.age}"


    println {
      (familyDOBs ++ List(User(2021, "Frida"))).sorted.map(displayNice).mkString(", ")
    }

    val oldest: Option[User] = List[User](User(1987, "Oto")).maxByOption(_.age)
    println(oldest.map(_.name.toUpperCase + " Brglez"))

    oldest match {
      case Some(user) if user.age >= 30 => println(s"👋 ${user.name}")
      // MatchError (predef)
      case None => println("Nobody is the oldest.")
    }
  }
}



