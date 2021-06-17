package com.pinkstack.eventbay.mk1

object Example3 {
  sealed trait Shape {
    def x: Int

    def y: Int
  }

  final case class Circle(x: Int, y: Int, r: Int) extends Shape

  final case class Square(x: Int, y: Int, w: Int, h: Int) extends Shape

  def run(): Unit = {
    val shapes: List[Shape] = List(
      Circle(0, 0, 10),
      Square(10, 10, 20, 30),
      Circle(0, 0, 100)
    )

    def shapesPrinter(shapes: List[Shape]): Unit =
      shapes.foreach {
        case Circle(_, _, r) if r > 10 =>
          println("HUUUUUGE! circle!")
        case Circle(x, y, _) => println(s"⭕️ $x,$y")
        // case square: Square => println(s"🔲 ${square.x}, ${square.y}")
        // case _ => println("(its)")
      }

    shapesPrinter(shapes)
  }
}
