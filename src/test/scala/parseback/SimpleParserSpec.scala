/*
 * Copyright 2017 Daniel Spiewak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package parseback

object SimpleParserSpec extends ParsebackSpec {
  import ParseError._

  "parentheses parser" should {
    lazy val p: Parser[Int] = (
        "(" ~> p <~ ")" ^^ { (_, i) => i + 1 }
      | () ^^^ 0
    )

    "accept the empty string" in {
      p must parseOk("")(0)
    }

    "accept a pair of parens" in {
      p must parseOk("()")(1)
    }

    "reject an opening paren" in {
      p must failToParse("(")(UnexpectedTrailingCharacters(Line("(")))
    }

    "reject a closing paren" in {
      p must failToParse(")")(UnexpectedTrailingCharacters(Line(")")))
    }

    "accept arbitrary nesting" in {   // TODO scalacheck
      p must parseOk("(((())))")(4)
    }

    "reject arbitrarily unbalanced nesting" in {
      p must failToParse("())")(UnexpectedTrailingCharacters(Line("())")))
      p must failToParse("(()))")(UnexpectedTrailingCharacters(Line("(()))")))
      p must failToParse("(()")(UnexpectedTrailingCharacters(Line("(()")))
    }
  }

  "left-recursive repeater parser" should {
    lazy val p: Parser[Int] = (
        p <~ "a" ^^ { (_, i) => i + 1 }
      | "a" ^^^ 1
    )

    // TODO scalacheck
    "accept any number of repeating a's" in {
      p must parseOk("a")(1)
      p must parseOk("aaaa")(4)
      p must parseOk("aaaaaaaaaaaaaa")(14)
    }
  }

  "right-recursive repeater parser" should {
    lazy val p: Parser[Int] = (
        "a" ~> p ^^ { (_, i) => i + 1 }
      | "a" ^^^ 1
    )

    // TODO scalacheck
    "accept any number of repeating a's" in {
      p must parseOk("a")(1)
      p must parseOk("aaaa")(4)
      p must parseOk("aaaaaaaaaaaaaa")(14)
    }
  }
}
