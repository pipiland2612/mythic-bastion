
import org.scalatest.funsuite.AnyFunSuite
import stage.Stage
import utils.Tools

class ParserTest extends AnyFunSuite :

  test("add should handle correctly when parsing")
    Tools.parser("enemies/Boss01.json", "enemies/Boss01.png", 2)

  test("add should parse corrent stage")
    val stage: Stage = Tools.loadStage("stages/Stage01.json")
    println(stage)

