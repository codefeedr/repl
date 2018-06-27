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
