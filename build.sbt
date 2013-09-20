import AssemblyKeys._
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.linux.LinuxPackageMapping

name := "sbt-debian"

version := "1.0"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "com.typesafe"   % "config"           % "1.0.2",
  "ch.qos.logback" % "logback-classic"  % "1.0.6")

def filterOut(name: String): Boolean = {
  ! (name.endsWith("logback.xml") ||
     name.endsWith("application.conf"))
}

mappings in (Compile,packageBin) ~= {
  (ms: Seq[(File,String)]) =>
    ms filter { case (file, toPath) => filterOut(toPath) }
}

//ASSEMBLY
seq(assemblySettings:_*)

assembleArtifact in packageScala := true

jarName in packageDependency := name.value + "-deps.jar"

//DEBIAN
seq(packagerSettings:_*)

val debArchitecture = SettingKey[String]("deb-architecture", "The architecture for which this deb package is aimed for.")

debArchitecture := "all"

com.typesafe.sbt.packager.debian.Keys.name in Debian := name.value

com.typesafe.sbt.packager.debian.Keys.version in Debian <<= (version, debArchitecture) apply { (pkgv, debArch) => pkgv + "_" + debArch }

com.typesafe.sbt.packager.debian.Keys.maintainer in Debian:= "Your Name <your.name@whatever.com>"

com.typesafe.sbt.packager.debian.Keys.packageSummary := "Test sbt debian"

com.typesafe.sbt.packager.debian.Keys.packageDescription in Debian:= "Test stb-native-packager to build deb package from a sbt project"

com.typesafe.sbt.packager.debian.Keys.debianPackageDependencies in Debian ++= Seq("java2-runtime")

com.typesafe.sbt.packager.debian.Keys.debianPackageRecommends in Debian ++= Seq("git")

com.typesafe.sbt.packager.debian.Keys.linuxPackageMappings in Debian <+= (baseDirectory, name) map {
  (deb, projname) =>  (packageMapping(
    (deb / "debian/changelog") -> ("/usr/share/doc/" + projname + "/changelog.gz"))
    withUser "root" withGroup "root" withPerms "0644" gzipped) asDocs()
}

com.typesafe.sbt.packager.debian.Keys.linuxPackageMappings in Debian <+= (baseDirectory, name) map {
  (deb, projname) =>  (packageMapping(
    (deb / "debian/config/application.conf") -> ("/usr/local/" + projname + "/config/application.conf"),
    (deb / "debian/config/logback.xml")      -> ("/usr/local/" + projname + "/config/logback.xml"),
    (deb / "debian/copyright")               -> ("/usr/share/doc/" + projname + "/copyright"),
    (deb / "debian/copyright")               -> "/DEBIAN/copyright")
    withUser "root" withGroup "root" withPerms "0644")
}

com.typesafe.sbt.packager.debian.Keys.linuxPackageMappings in Debian <+= (baseDirectory, name) map {
  (deb, projname) => (packageMapping(
    (deb / "debian/bin/helloDebian.sh")                       -> ("/usr/local/" + projname + "/bin/helloDebian.sh"),
    (deb / "debian/bin/helloDebianCLIParameters.sh")          -> ("/usr/local/" + projname + "/bin/helloDebianCLIParameters.sh"),
    (deb / "debian/bin/helloDebianExternalConfig.sh")         -> ("/usr/local/" + projname + "/bin/helloDebianExternalConfig.sh"),
    (deb / ("target/scala-2.10/"+ projname +"-deps.jar"))     -> ("/usr/local/" + projname + "/sbt-debian-deps.jar"),
    (deb / ("target/scala-2.10/"+ projname +"_2.10-1.0.jar")) -> ("/usr/local/" + projname + "/sbt-debian_2.10-1.0.jar"))
    withUser "root" withGroup "root" withPerms "0755")
}
//;reload;clean;package;assembly-package-dependency;debian:package-bin
