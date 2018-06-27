import java.sql.DriverManager
import java.util.Properties

import org.apache.calcite.adapter.java.ReflectiveSchema
import org.apache.calcite.jdbc.CalciteConnection

import scala.util.{Success, Try}

object ConnectCommand extends Command with Parser {

  val regex = """(?i)^connect"""

  override def apply(x: ReplEnv, input: String): (ReplEnv, Try[String]) = {
    Class.forName("org.apache.calcite.jdbc.Driver")

    val info = new Properties()
    info.setProperty("lex", "JAVA")

    val connection =
      DriverManager.getConnection("jdbc:calcite:", info);

    val calciteConnection = connection.unwrap(classOf[CalciteConnection])

    val rootSchema = calciteConnection.getRootSchema

    val schema = new ReflectiveSchema(new HrSchema)
    rootSchema.add("hr", schema)
    (x.copy(calciteConnection, schema), Success("Connected"))
  }

  override def parse(expr: String): Option[Command] = matches(expr) match {
    case true => Option(ConnectCommand)
    case _ => None
  }
}
