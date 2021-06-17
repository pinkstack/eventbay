package com.pinkstack.eventbay.mk1

object Example1 {

  object ForDemo {
    def sumSimple(x: Int, y: Int): Int = x + y

    println(sumSimple(40, 2))


    val sumSimpleII: (Int, Int) => Int = (x, y) => x + y
    println(sumSimpleII(40, 2))


    def sumC(x: Int*) = x.fold(0)(_ + _)

    println(sumC(10, 32))

    def summing(f: Int => Int)(x: Int, y: Int): Int = f(x) + f(y)

    val identityF: Int => Int = x => x

    val sum: (Int, Int) => Int = summing(identity)
    val increment: Int => Int = sum.curried(1)

    println(sum(10, 32))
    println(increment(41))

    assert {
      sumSimple(40, 2) == sumSimpleII(40, 2)
    }
  }

  object HowToDefineFunction {

    def add(x: Int, y: Int): Int = x + y

    val add2: (Int, Int) => Int = (x, y) => x + y

    def add3(xs: Int*): Int = xs.fold(0)(_ + _)

    def add4(x: Int)(y: Int) = x + y

    def add5(f: Int => Int)(x: Int, y: Int): Int = f(x) + f(y)

    val sum: (Int, Int) => Int = add5(identity)
    val increment: Int => Int = sum.curried(1)

  }

  def run: Unit = {
    import HowToDefineFunction._
    println {
      add(10, 32)
    }

    println {
      add2(32, 10)
    }

    println {
      add3(32, 10, 0)
    }

    println {
      add4(10)(10)
    }

    println {
      add5(f => f * 2)(5, 6)
    }

    println(increment(41))

  }
}
