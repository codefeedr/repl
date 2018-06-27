import scala.util.{Failure, Success, Try}


object SQLCommand extends Parser with Command {
  val regex = """(?i)^select.*$"""

  override def parse(expr: String): Option[Command] = matches(expr) match {
    case true => Option(SQLCommand)
    case _ => None
  }

  override def apply(env: ReplEnv, input: String): (ReplEnv, Try[String]) = {
    if (env.connection == null) {
      return (env, Failure(new Exception("Not connected")))
    }

    try {
      val ts = System.currentTimeMillis()
      val statement = env.connection.createStatement
      val rs = statement.executeQuery(input)
      var counter = 0

      if (rs.next) {
        do {
          counter += 1
          println(rs.getInt("id") + "," + rs.getString("name"))
        } while (rs.next)
      }
      statement.close

      (env, Success(f"$counter%d rows, ${System.currentTimeMillis() - ts}%d ms"))
    } catch {
      case e: Exception => (env, Failure(e))
    }
  }
}
