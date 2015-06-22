name := "bodhi"

organization := "sbtdocker"

version := "0.1.0"

enablePlugins(DockerPlugin)

// Make docker depend on the assembly task, which generates a fat jar file
docker <<= (docker dependsOn assembly)

dockerfile in docker := {
  val jarFile = (outputPath in assembly).value
  val appDirPath = "/app"
  val jarTargetPath = s"$appDirPath/${jarFile.name}"
  val artifact = (outputPath in assembly).value

  new Dockerfile {
    from("ubuntu:14.04")

    stageFile(file("jdk-8u45-linux-x64.tar.gz"), "jdk.tar.gz")
    add("jdk.tar.gz", "/usr/java")
    env("JAVA_HOME", "/usr/java/jdk1.8.0_45/bin")
    env("PATH", "$JAVA_HOME:$PATH")

    add(jarFile, jarTargetPath)

    stageFile(file("greet.sh"), "greet.sh")
    add("greet.sh", "/app/greet.sh")
    runRaw("chmod a+x /app/greet.sh")

    workDir(appDirPath)
    entryPoint("/app/greet.sh")
  }
}

buildOptions in docker := BuildOptions(cache = false)