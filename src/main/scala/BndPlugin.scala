/**
 * User: hanlho
 * DateTime: 19/01/14 15:41
 */

import sbt._

object BndPlugin extends Plugin {

  val bndSetting = settingKey[String]("An hello setting.")

  val bndTask = taskKey[Unit]("say hello")


  // a group of settings ready to be added to a Project
  // to automatically add them, do
  val bndSettings = Seq(
    bndSetting := "bnd?",
    bndTask := {
      println("hello " + bndSetting.value)
    }
  )
}
