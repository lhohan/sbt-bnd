/**
 * User: hanlho
 * DateTime: 19/01/14 15:41
 */

package eu.lhoest.sbt.plugin.bnd

import sbt._
import sbt.Keys._
import aQute.bnd.build
import aQute.bnd.build
import scala.collection.JavaConversions._
import aQute.bnd.build.Workspace

object BndPlugin extends Plugin
{

	object BndKeys {

    val bndSrcOutput: SettingKey[String] =
      SettingKey[String](
        prefixed("SrcOutput"),
        "Source (output) location for bnd."
      )
  

    val bndProject: TaskKey[build.Project] =
      TaskKey[build.Project](
        prefixed("Project"),
        "The bnd project reference."
      )  

    val bndBuild: TaskKey[File] =
      TaskKey[File](
        prefixed("Build"),
        "Build the project based on the settings defined by bnd."
      )  

    val bndDependencies: TaskKey[Seq[File]] =
      TaskKey[Seq[File]](
        prefixed("Dependencies"),
        "Determine dependencies defined by bnd."
      )  

    private def prefixed(key: String) = s"bnd$key"
  }

  import BndKeys._

  def defaultBndSettings = Seq(
    helloText := "bnd!",
    sayHello := {
        println("hello " + helloText.value)
      },
    bndProject := Bnd.bndProject(thisProject.value, streams.value.log),
    bndBuild := Bnd.bndBuild(thisProject.value, streams.value.log, bndSrcOutput.value, scalaBinaryVersion.value),   
    bndDependencies := Bnd.bndDependencies(thisProject.value, streams.value.log),
    bndSrcOutput := BndSettings.srcOutput,
    cleanFiles <+= baseDirectory { base => base / BndSettings.srcOutput },
    cleanFiles <+= baseDirectory { base => base / "generated"},
    bndBuild <<= bndBuild.dependsOn(compile in Compile), // could add test in Test as well so no bundle ever can get built with failing tests
    unmanagedJars in Compile := bndDependencies.value.classpath
  )

  def bndSettings = defaultBndSettings  ++ Seq (
    packagedArtifact in (Compile, packageBin) <<= (artifact in (Compile, packageBin), bundle).identityMap,
    artifact in (Compile, packageBin) ~= (_.copy(`type` = "bundle")),
    version := Bnd.getBndVersion(version.value)
  )

  val bundle = {
    bndBuild
  }

}
