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
    from("java")

    add(jarFile, jarTargetPath)

    stageFile(file("greet.sh"), "greet.sh")
    add("greet.sh", "/app/greet.sh")
    run("chmod", "a+x", "/app/greet.sh")

    workDir(appDirPath)
    entryPoint("/app/greet.sh")
  }
}

buildOptions in docker := BuildOptions(cache = false)