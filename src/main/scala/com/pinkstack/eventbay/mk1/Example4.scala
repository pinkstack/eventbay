package com.pinkstack.eventbay.mk1

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Example4 {

  def answerToEverything(): Future[Int] = Future {
    Thread.sleep(3.seconds.toMillis)
    42
  }

  def run1(): Unit = {
    println("🚀 Started doing something!")
    println(s"Done with stuff: ${answerToEverything()}")

  }

  def run2(): Unit = {
    println("Oook, second attempt...")
    val result = Await.result(answerToEverything(), 5.seconds)
    println(s"Done $result")

  }

  def run(): Unit = {
    run2()
  }
}
