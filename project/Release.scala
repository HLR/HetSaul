import com.typesafe.sbt.packager.NativePackagerKeys
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.ReleasePlugin.autoImport._
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.Universal

//scalastyle: off
object Release extends UniversalKeys with NativePackagerKeys {

  object Library {
    def setupRelease(project: sbt.Project): sbt.Project = project
  }

  import sbtrelease._
  // we hide the existing definition for setReleaseVersion to replace it with our own
  import sbtrelease.ReleaseStateTransformations.{ setReleaseVersion => _, _ }

  private def setVersionOnly(selectVersion: Versions => String): ReleaseStep = { st: State =>
    val vs = st.get(ReleaseKeys.versions).getOrElse(sys.error("No versions are set! Was this release part executed before inquireVersions?"))
    val selected = selectVersion(vs)

    st.log.info("Setting version to '%s'." format selected)
    val useGlobal = Project.extract(st).get(releaseUseGlobalVersion)
    val versionStr = (if (useGlobal) globalVersionString else versionString) format selected

    reapply(Seq(
      if (useGlobal) version in ThisBuild := selected
      else version := selected), st)
  }

  lazy val setReleaseVersionOnly: ReleaseStep = setVersionOnly(_._1)

  releaseVersion <<= (releaseVersionBump)( bumper=>{
    ver => Version(ver)
      .map(_.withoutQualifier)
      .map(_.bump(bumper).string).getOrElse(versionFormatError)
  })

  val showNextVersion = settingKey[String]("the future version once releaseNextVersion has been applied to it")
  val showReleaseVersion = settingKey[String]("the future version once releaseNextVersion has been applied to it")
  showReleaseVersion <<= (version, releaseVersion)((v,f)=>f(v))
  showNextVersion <<= (version, releaseNextVersion)((v,f)=>f(v))

  releaseProcess := Seq(
    checkSnapshotDependencies,
    inquireVersions,
    setReleaseVersionOnly//,
    //runTest,
    //tagRelease,
    // publishArtifacts,
    //ReleaseStep(releaseStepTask(publish in Universal)),
    //pushChanges
  )
}
