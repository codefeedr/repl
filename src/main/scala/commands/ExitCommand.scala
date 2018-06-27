import scala.util.{Success, Try}

object ExitCommand extends Command with Parser {
  val regex = """(?i)^(?:exit|quit)$"""

  override def parse(expr: String): Option[Command] = matches(expr) match {
    case true => Option(ExitCommand)
    case _ => None
  }

  override def apply(env: ReplEnv, v2: String): (ReplEnv, Try[String]) = {
    System.exit(0)
    (env, Success("exit"))
  }
}
