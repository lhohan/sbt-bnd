/**
 * User: hanlho
 * DateTime: 19/01/14 15:41
 */

package eu.lhoest.sbt.plugin.bnd

import sbt._
import sbt.std._
import aQute.bnd.build
import scala.collection.JavaConversions._
import aQute.bnd.build.Workspace
import org.joda.time._

object Bnd
{
    
    def bndProject(sbtProject: ResolvedProject, logger: Logger) = {
        val projId = sbtProject.id
        log(logger, s"executing bndProject for '${sbtProject.id}'")
        Workspace.getProject(sbtProject.base)
    }

    def bndDependencies(sbtProject: ResolvedProject, logger: Logger) = {
        val projId = sbtProject.id
        log(logger, s"executing bndDependencies for '${projId}'") 
        val p = bndProject(sbtProject, logger) 
        p.getDependson.foreach(x => log(logger, s"bnd ==> $projId depends on bnd project ${x.getName()}"))
        p.getBuildpath.map(_.getFile).toSeq
      }

    def bndBuild(sbtProject: ResolvedProject, logger: Logger, bndSrcOutput: String, scalaBinVersion: String) = {
        val projId = sbtProject.id
        log(logger, s"executing bndBuild for '${projId}'")
        // bnd looks in the bnd dir for its files, so quick hack: copy compiled to this dir
        sbt.IO.copyDirectory(sbtProject.base / "target" / ("scala-"+scalaBinVersion) / "classes", file(bndSrcOutput), true, true)
        // do the build
        val p = bndProject(sbtProject, logger)
        val files = p.build
        //Report errors and warnings to the sbt output
        p.getWarnings().foreach {
          w => log(logger, s"warning: $w")
        }
        p.getErrors().foreach {
          e => log(logger, s"error: $e")
        }
        //Fail the build if there are build errors
        if (!p.getErrors().isEmpty) {
          throw new Exception("Bnd build failure. Check build logs for error messages")
        }
        if(files.size == 1){
           files.foreach(f => log(logger, s"Generated bundle: $f")) 
           files(0)
        } else {
            error(s"Expected one bundle to be generated but got ${files.size}!")
        }
    }

    // read the first line of bnd file, if it contains the Bundle Version that's what we will use
    // supports ${tstamp} replacement
    def getBndVersion(oldVersion: String) : String = {
        try {
           val dt = new DateTime()
           val seconds = dt.toString("yyyyMMddHHmmss")
           val firstBndLine = IO.readLines(file("bnd.bnd")).head.trim
           if(firstBndLine.contains("Bundle-Version:")){
              firstBndLine.drop("Bundle-Version:".size).trim.replace("${tstamp}", seconds)  
           } else {
              oldVersion
           }
        } catch {
            case _ : Exception => oldVersion 
        }
    }

    def log(logger: Logger, msg : String) = {
        logger.info(s"sbtbnd::$msg")
    }

}

object BndSettings {
  var srcOutput = "bin"
}
