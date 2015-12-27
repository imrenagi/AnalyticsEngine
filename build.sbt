// Controlling parameters, edit here
val jvmVersion = "1.8"
val javaSrcVersion = "1.8"
val scalacVersion = "2.11.7"
val orgPackage = "com.imrenagi"
val releaseVersion = "1.0"

// Warnings we always want active in Scala compiles
val scalacMustHaveWarnings = Seq(
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  //  "-Xlint:_", // All Xlint warnings, activates most -Y cases as well
  "-Xlint:-delayedinit-select", // Allow this one separately, off for now
  "-Ywarn-dead-code", // These are -Y versions only so need explicit listing
  //  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfatal-warnings"
)

val scalacAllWarnings = scalacMustHaveWarnings ++ Seq(
  "-Ywarn-unused",  // Unused options are in -Y only
  "-Ywarn-unused-import"
)

organization in ThisBuild := orgPackage

version in ThisBuild := releaseVersion

scalaVersion in ThisBuild := scalacVersion

scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-" + jvmVersion,
  "-encoding", "UTF-8"
)

scalacOptions in ThisBuild ++= scalacMustHaveWarnings

javacOptions in ThisBuild ++= Seq("-source", javaSrcVersion, "-target", jvmVersion)

lazy val extraResolvers = Seq()
lazy val commonExtraResolvers = extraResolvers ++ Seq(  "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
  "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
  "Twitter Repository"               at "http://maven.twttr.com",
  Resolver.bintrayRepo("websudos", "oss-releases"))
lazy val dataIngestionExtraResolvers = extraResolvers ++ Seq()

lazy val common = (project in file("common"))
.settings(
    name := "common",
    resolvers ++= commonExtraResolvers,
    libraryDependencies ++= dependencies.Common
  )

lazy val data_ingestion_setting = Seq(
  name := "data_ingestion",
  mainClass in Compile := Some("com.imrenagi.analytics.Hello"),
  resolvers ++= dataIngestionExtraResolvers,
  libraryDependencies ++= dependencies.DataIngestion,
  fork in run := true
)

lazy val data_ingestion = (project in file("data_ingestion"))
  .settings(
    data_ingestion_setting : _*
  ).dependsOn(common)
