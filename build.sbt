// Controlling parameters, edit here
val jvmVersion = "1.7"
val javaSrcVersion = "1.8"
val scalacVersion = "2.10.4"
val orgPackage = "com.imrenagi"
val releaseVersion = "1.0"

lazy val commonSettings = Seq(
  organization := orgPackage,
  version := releaseVersion,
  // set the Scala version used for the project.  2.11 isn't supported with Spark yet
  scalaVersion := scalacVersion
)

// Warnings we always want active in Scala compiles
//val scalacMustHaveWarnings = Seq(
//  "-deprecation", // warning and location for usages of deprecated APIs
//  "-feature", // warning and location for usages of features that should be imported explicitly
//  "-unchecked", // additional warnings where generated code depends on assumptions
//  //  "-Xlint:_", // All Xlint warnings, activates most -Y cases as well
//  "-Xlint:-delayedinit-select", // Allow this one separately, off for now
//  "-Ywarn-dead-code", // These are -Y versions only so need explicit listing
//  //  "-Ywarn-numeric-widen",
//  "-Ywarn-value-discard",
//  "-Xfatal-warnings"
//)
//
//val scalacAllWarnings = scalacMustHaveWarnings ++ Seq(
//  "-Ywarn-unused",  // Unused options are in -Y only
//  "-Ywarn-unused-import"
//)


//organization in ThisBuild := orgPackage
//
//version in ThisBuild := releaseVersion
//
//scalaVersion in ThisBuild := scalacVersion
//
//scalacOptions in ThisBuild ++= Seq(
//  "-target:jvm-" + jvmVersion,
//  "-encoding", "UTF-8"
//)
//
//crossPaths := false
//autoScalaLibrary := false
//
////scalacOptions in ThisBuild ++= scalacMustHaveWarnings
//
//javacOptions in ThisBuild ++= Seq("-source", javaSrcVersion, "-target", jvmVersion)

lazy val extraResolvers = Seq()
lazy val commonExtraResolvers = extraResolvers ++ Seq(  "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
  "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
  "Confluent"                        at "http://packages.confluent.io/maven/",
  "Twitter Repository"               at "http://maven.twttr.com",
  Resolver.bintrayRepo("websudos", "oss-releases"))
lazy val dataIngestionExtraResolvers = extraResolvers ++ Seq()
lazy val batchProcessingExtraResolvers = commonExtraResolvers ++ Seq()
lazy val realStreamingProcessingExtraResolvers = commonExtraResolvers ++ Seq()

lazy val common = (project in file("common"))
.settings(commonSettings: _*)
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


lazy val batch_processing_setting = Seq(
  name := "batch_processing",
  mainClass in Compile := Some("com.imrenagi.analytics.BatchProcessingApp"),
  resolvers ++= batchProcessingExtraResolvers,
  libraryDependencies ++= dependencies.BatchProcessing,
  fork in run := true
)

lazy val batch_processing = (project in file("batch_processing"))
.settings(
  batch_processing_setting : _*
  ).dependsOn(common)

lazy val streaming_processing_setting = Seq (
  name := "streaming_processing",
  mainClass in Compile := Some("ProjectStreaming"),
  resolvers ++= realStreamingProcessingExtraResolvers,
  libraryDependencies ++= dependencies.StreamingProcessing,
  dependencyOverrides ++= Set(
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
  ),
  fork in run := true
)

lazy val streaming_processing = (project in file("streaming_processing"))
  .settings(
    streaming_processing_setting : _*
  ).dependsOn(common)



