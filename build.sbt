
/*
 * BASIC INFORMATION
 ********************************************************/

name := "ubirch-oidc-utils"
version := "0.8.15"
description := "OpenID Connect related authorization utils"
organization := "com.ubirch.util"
homepage := Some(url("http://ubirch.com"))
scalaVersion := "2.11.12"
scalacOptions ++= Seq(
  "-feature"
)
scmInfo := Some(ScmInfo(
  url("https://github.com/ubirch/ubirch-oidc-utils"),
  "https://github.com/ubirch/ubirch-oidc-utils.git"
))

/*
 * CREDENTIALS
 ********************************************************/

(sys.env.get("CLOUDREPO_USER"), sys.env.get("CLOUDREPO_PW")) match {
  case (Some(username), Some(password)) =>
    println("USERNAME and/or PASSWORD found.")
    credentials += Credentials("ubirch.mycloudrepo.io", "ubirch.mycloudrepo.io", username, password)
  case _ =>
    println("USERNAME and/or PASSWORD is taken from /.sbt/.credentials")
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
}

/*
 * RESOLVER
 ********************************************************/

val resolverUbirchUtils = "ubirch.mycloudrepo.io" at "https://ubirch.mycloudrepo.io/repositories/ubirch-utils-mvn"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  resolverUbirchUtils)


/*
 * PUBLISHING
 ********************************************************/

publishTo := Some(resolverUbirchUtils)
publishMavenStyle := true


/*
 * DEPENDENCIES
 ********************************************************/


// Versions
val akkaHttpV = "10.1.3"

// Groups
val akkaG = "com.typesafe.akka"
val ubirchUtilGroup = "com.ubirch.util"

//Ubirch depedencies
val ubirchIdServiceClient = "com.ubirch.id" %% "ubirch-id-service-client" % "0.6.4"
val ubirchUserRest = "com.ubirch.user" %% "ubirch-user-service-client" % "1.0.4"
val ubirchUtilCrypto = ubirchUtilGroup %% "ubirch-crypto-utils" % "0.5.3"
val ubirchUtilJson = ubirchUtilGroup %% "ubirch-json-utils" % "0.5.2"
val ubirchRedisUtils = ubirchUtilGroup %% "ubirch-redis-utils" % "0.6.1"
val ubirchUtilResponseUtil = ubirchUtilGroup %% "ubirch-response-utils" % "0.5.1"

//External dependencies
val akkaHttp = akkaG %% "akka-http" % akkaHttpV
val akkaStream = akkaG %% "akka-stream" % "2.5.11"
val json4sNative = "org.json4s" %% "json4s-native" % "3.6.0"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
val akkaHttpTestkit = akkaG %% "akka-http-testkit" % akkaHttpV

val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
val slf4j = "org.slf4j" % "slf4j-api" % "1.7.21"
val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.1.7"

val depSlf4jLogging = Seq(
  scalaLogging,
  slf4j,
  logbackClassic
)

libraryDependencies ++= Seq(
  akkaHttp,
  akkaStream,
  json4sNative,
  ubirchIdServiceClient,
  ubirchUserRest,
  ubirchUtilCrypto,
  ubirchUtilJson,
  ubirchRedisUtils,
  ubirchUtilResponseUtil,
  scalaTest % "test",
  akkaHttpTestkit % "test",
) ++ depSlf4jLogging


