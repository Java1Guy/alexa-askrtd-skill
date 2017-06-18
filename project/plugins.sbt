

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")
addSbtPlugin("com.github.gseitz" % "sbt-protobuf" % "0.5.5")
addSbtPlugin("org.scalaxb" % "sbt-scalaxb" % "1.5.0")

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies += "com.github.os72" % "protoc-jar" % "3.2.0"
