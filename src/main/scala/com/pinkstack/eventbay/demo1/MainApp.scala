package com.pinkstack.eventbay.demo1

object MainApp extends App {
  def simpleSum(x: Int, y: Int): Int = x + y

  println(simpleSum(10, 32))

  val simpleSumII: (Int, Int) => Int = (x, y) => x + y
  println(simpleSumII(10, 32))

  def summing(f: Int => Int)(x: Int, y: Int): Int = f(x) + f(y)

  val identity: Int => Int = x => x
  val sum: (Int, Int) => Int = summing(identity)
  val increment: Int => Int = sum.curried(1)

  println(sum(10, 32))
  println(increment(41))
}
