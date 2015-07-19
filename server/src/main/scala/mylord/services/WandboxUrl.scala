package mylord
package services

abstract class WandboxUrl extends Url("wandbox") {
  val list = gen("list.json")
  val compile = gen("compile.json")
}
