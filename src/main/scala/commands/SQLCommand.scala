/**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing,
  * software distributed under the License is distributed on an
  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied.  See the License for the
  * specific language governing permissions and limitations
  * under the License.
  */

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
