import scalaz.EitherT
import scalaz.concurrent.Task

package object mylord {
  type Action[T] = EitherT[Task, String, T]
}
