//package main.java;
//
//private[spark] class SparkDeploySchedulerBackend(
//                                                  scheduler: TaskSchedulerImpl,
//                                                  sc: SparkContext,
//                                                  masters: Array[String])
//  extends CoarseGrainedSchedulerBackend(scheduler, sc.env.actorSystem)
//    with AppClientListener
//    with Logging {
//
//  var client: AppClient = null  //注：Application与Master的接口
//
//  val maxCores = conf.getOption("spark.cores.max").map(_.toInt) //注：获得每个executor最多的CPU core数目
//  override def start() {
//    super.start()
//
//    // The endpoint for executors to talk to us
//    val driverUrl = "akka.tcp://%s@%s:%s/user/%s".format(
//      SparkEnv.driverActorSystemName,
//      conf.get("spark.driver.host"),
//      conf.get("spark.driver.port"),
//      CoarseGrainedSchedulerBackend.ACTOR_NAME)
//    //注：现在executor还没有申请，因此关于executor的所有信息都是未知的。
//    //这些参数将会在org.apache.spark.deploy.worker.ExecutorRunner启动ExecutorBackend的时候替换这些参数
//    val args = Seq(driverUrl, "{{EXECUTOR_ID}}", "{{HOSTNAME}}", "{{CORES}}", "{{WORKER_URL}}")
//    //注：设置executor运行时需要的环境变量
//    val extraJavaOpts = sc.conf.getOption("spark.executor.extraJavaOptions")
//      .map(Utils.splitCommandString).getOrElse(Seq.empty)
//    val classPathEntries = sc.conf.getOption("spark.executor.extraClassPath").toSeq.flatMap { cp =>
//      cp.split(java.io.File.pathSeparator)
//    }
//    val libraryPathEntries =
//      sc.conf.getOption("spark.executor.extraLibraryPath").toSeq.flatMap { cp =>
//        cp.split(java.io.File.pathSeparator)
//      }
//
//    // Start executors with a few necessary configs for registering with the scheduler
//    val sparkJavaOpts = Utils.sparkJavaOpts(conf, SparkConf.isExecutorStartupConf)
//    val javaOpts = sparkJavaOpts ++ extraJavaOpts
//    //注：在Worker上通过org.apache.spark.deploy.worker.ExecutorRunner启动
//    // org.apache.spark.executor.CoarseGrainedExecutorBackend，这里准备启动它需要的参数
//    val command = Command("org.apache.spark.executor.CoarseGrainedExecutorBackend",
//      args, sc.executorEnvs, classPathEntries, libraryPathEntries, javaOpts)
//    //注：org.apache.spark.deploy.ApplicationDescription包含了所有注册这个Application的所有信息。
//    val appDesc = new ApplicationDescription(sc.appName, maxCores, sc.executorMemory, command,
//      sc.ui.appUIAddress, sc.eventLogger.map(_.logDir))
//
//    client = new AppClient(sc.env.actorSystem, masters, appDesc, this, conf)
//    client.start()
//    //注：在Master返回注册Application成功的消息后，AppClient会回调本class的connected，完成了Application的注册。
//    waitForRegistration()
//  }