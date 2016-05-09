name := "play-requestfilter"

version := "0.1.0"

organization := "com.github.kitefusion"

scalaVersion := "2.11.8"

useGpg := true

pomExtra := (
	<url>http://github.com/Kitefusion</url>
	<licenses>
		<license>
			<name>BSD-style</name>
			<url>http://www.opensource.org/licenses/bsd-license.php</url>
			<distribution>repo</distribution>
		</license>
	</licenses>
	<scm>
		<url>git@github.com:kitefusion/play-requestfilter.git</url>
		<connection>scm:git@github.com:kitefusion/play-requestfilter.git</connection>
	</scm>
	<developers>
		<developer>
			<id>mborchardt</id>
			<name>Marcel Borchardt</name>
			<url>http://github.com/Kitefusion</url>
		</developer>
	</developers>)

scalacOptions ++= Seq(
	"-deprecation",
	"-feature",
	"-encoding", "Utf8"
)

libraryDependencies ++= Seq(
	specs2 % Test,
	"com.typesafe.play" %% "play" % "2.5.3",
	"org.slf4j" % "slf4j-simple" % "1.7.2" % Test
)

publishMavenStyle := true

publishTo := {
	val nexus = "https://oss.sonatype.org/"
	if (isSnapshot.value)
		Some("snapshots" at nexus + "content/repositories/snapshots")
	else
		Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
