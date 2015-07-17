package mylord
package novel

abstract class Novel(title: String)

case class Scenario(title: String, background: String, scene: List[Scene]) extends Novel(title)

abstract class Scene(background: String, characters: List[Character])

case class Monologue(background: String, characters: List[Character], words: List[String])
  extends Scene(background, characters)

case class Line(background: String, characters: List[Character], words: List[String], name: String)
  extends Scene(background, characters)

case class Character(image: String, width: Int, height: Int)
