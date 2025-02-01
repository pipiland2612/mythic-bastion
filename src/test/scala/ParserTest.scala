
import org.scalatest.funsuite.AnyFunSuite
import utils.Tools

class ParserTest extends AnyFunSuite {

  test("add should handle correctly when parsing")
    Tools.parser("enemies/Monster01.json", "enemies/Monster01.png")
    val testCases: Vector[(Int, Int)] = Vector(

    )
    for test <- testCases do
      println(test._1)
}
