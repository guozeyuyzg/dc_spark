name := "dc_spark"

version := "0.1"

scalaVersion := "2.11.12"
//scalaVersion := "2.11.8"
//scalaVersion := "2.10.5"

javacOptions ++= Seq("-encoding", "UTF-8")

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.0" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.2.0" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.2.0"
//libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0"
//libraryDependencies += "org.apache.kafka" %% "kafka" % "0.10.2.0"
libraryDependencies += "com.alibaba" % "fastjson" % "1.2.12"
libraryDependencies += "org.elasticsearch" %% "elasticsearch-spark-20" % "6.3.2"
libraryDependencies += "redis.clients" % "jedis" % "2.9.0"
//libraryDependencies += "org.elasticsearch" % "elasticsearch-hadoop" % "6.3.0"
//libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.1.2"
//libraryDependencies += "org.apache.hbase" % "hbase" % "1.1.2" pomOnly()
//libraryDependencies += "org.apache.hbase" % "hbase-mapreduce" % "2.1.0"
libraryDependencies ++= Seq(
//  "org.apache.hadoop" % "hadoop-common" % "2.6.0",
//  "org.apache.hadoop" % "hadoop-mapred" % "0.22.0"
//  "org.apache.hbase" % "hbase-server" % "1.2.1",
//  "org.apache.hbase" % "hbase-common" % "1.2.1",
//  "org.apache.hbase" % "hbase-client" % "1.2.1"
)

//assemblyMergeStrategy in assembly := {
//  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
//  case PathList("javax", "inject", xs @ _*)         => MergeStrategy.first
//  case PathList("org", "glassfish", xs @ _*)         => MergeStrategy.first
//  case PathList("org", "apache", xs @ _*)         => MergeStrategy.first
//  case PathList("org", "aopalliance", xs @ _*)         => MergeStrategy.first
//  case PathList(ps @ _*) if ps.last endsWith "UnusedStubClass.class" => MergeStrategy.first
//
//  case x =>
//    val oldStrategy = (assemblyMergeStrategy in assembly).value
//    oldStrategy(x)
//}


//resolvers += "Java.net Maven2 Repository" at "https://mvnrepository.com/artifact/org.apache.spark/spark-core"

