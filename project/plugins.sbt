resolvers += Resolver.bintrayRepo("kamon-io", "sbt-plugins")

addSbtPlugin("io.spray"         % "sbt-revolver"        % "0.9.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.18")
addSbtPlugin("com.tapad"        % "sbt-docker-compose"  % "1.0.34")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.9")

// Code quality
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")
