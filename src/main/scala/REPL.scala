import annotation.tailrec
import org.apache.calcite.jdbc.CalciteConnection
import org.apache.calcite.schema.impl.AbstractSchema

import scala.util.{Failure, Success, Try}

case class ReplEnv(connection: CalciteConnection,
                   schema: AbstractSchema)

trait Command extends ((ReplEnv, String) => (ReplEnv, Try[String]))

trait Parser {
  val regex : String
  def parse(expr: String): Option[Command]

  def matches(input: String): Boolean = input.matches(regex)
}

object Commands {
  val parsers = List[Parser](
    SQLCommand,
    ConnectCommand,
    //MetaCommand,
    ExitCommand
  )

  def apply(expr: String): Option[Command] =
    parsers.find((p) => p.matches(expr)) match {
      case Some(parser) => parser.parse(expr)
      case _ => None
    }
}

object REPL extends App {

  @tailrec def loop(env: ReplEnv): Unit = {

    printf("codefeedr> ")
    Console.flush()

    val input = scala.io.StdIn.readLine()
    Commands(input) match {
      case Some(cmd) => {
        val res = cmd(env, input)
        res._2 match {
          case Success(x) => println(x)
          case Failure(x) => System.err.println(x)
        }
        loop(res._1)
      }
      case None => loop(env)
    }
  }

  loop(ReplEnv(null, null))
}
