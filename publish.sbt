publishTo := {
  val artifactory = "https://kate.rnd.hq.mendix.net/artifactory/"
  val v = version.value
  val (name, url) = {
    if (v.trim.endsWith("SNAPSHOT")) {
      ("libs-snapshot", artifactory + "libs-snapshot-local/")
    } else {
      ("libs-release", artifactory + "libs-release-local/")
    }
  }
  Some(Resolver.url(name, new URL(url))(Resolver.ivyStylePatterns))
}

publishMavenStyle := false
