//package com.ipaynow
//
//import com.alibaba.fastjson.JSON
//import com.ipaynow.utils.AESUtil
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.apache.log4j.Logger
//import org.apache.spark.SparkConf
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.streaming.dstream.DStream
//import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
//import org.apache.spark.streaming.kafka010.KafkaUtils
//import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
//import org.apache.spark.streaming.{Seconds, StreamingContext}
//
//
//object TransOrder {
//  def main(args: Array[String]): Unit = {
//    val logger = Logger.getLogger(this.getClass);
//    val conf = new SparkConf().setAppName("AnalyzeTransOrder").setMaster("local[2]")
//    conf.set("es.index.auto.create", "true")
//    conf.set("es.nodes", "staging-bigdata02")
//    conf.set("es.port", "9200")
//    conf.set("spark.streaming.kafka.consumer.poll.ms", "600000")
//
//    val ssc = new StreamingContext(conf, Seconds(1))
//    val spark = SparkSession.builder().config(conf).getOrCreate()
//    spark.sparkContext.setLogLevel("WARN");
//
//    //initial kafka params
//    val kafkaParams = Map[String, Object](
//      "bootstrap.servers" -> "bigdata13:9092,bigdata14:9092,bigdata15:9092",
//      "key.deserializer" -> classOf[StringDeserializer],
//      "value.deserializer" -> classOf[StringDeserializer],
//      "group.id" -> "dc_spark_test",
//      //      "auto.offset.reset" -> "earliest"
//      "enable.auto.commit" -> (true: java.lang.Boolean)
//    )
//
//    //kafka topic
//    val topic = Set("topic_trans")
//    //fetch data from kafka
//    val stream = KafkaUtils.createDirectStream[String, String](
//      ssc,
//      PreferConsistent,
//      Subscribe[String, String](topic, kafkaParams)
//    )
//
//    //    stream.map(record => (record.key, record.value))
//
//    //calculate process logic
//    //    val kafkaValue: DStream[String] = stream.flatMap(line => Some(line.value()))
//    //    println("kafkaValue:" + kafkaValue)
//
//    //    val lines = stream.map(_.value)
//    //    val words = lines.flatMap(_.split(" "))
//    //    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
//    //    wordCounts.print()
//
////    stream.map(record => (record.key, record.value))
////
////
////    val kafkaValue: DStream[String] = stream.flatMap(line => Some(line.value()))
//
//    //    val jsonKafkaValue =  JSON.parseObject()
//    //    println("kafkaValue:" + )
//    //将lines字符串转换成json对象
//    stream.foreachRDD(kafkaRDD => {
//      //      val offsetRanges = kafkaRDD.asInstanceOf[HasOffsetRanges].offsetRanges
//      if (!kafkaRDD.isEmpty()) {
//          kafkaRDD.foreachPartition(iterator => {
//            while (iterator.hasNext) {
//              val next = iterator.next()
//              val dataJson = JSON.parseObject(next.value())
//              val data = AESUtil.decrypt("c9997ab525a76a09", dataJson.get("data").toString)
//              val id = dataJson.get("id").toString()
//              val length = dataJson.get("length").toString()
//              val msg = dataJson.get("msg").toString()
//              val rdStr = dataJson.get("rdStr").toString()
//              val source = dataJson.get("source").toString()
//              val time = dataJson.get("time").toString()
//              println(data)
//            }
//          })
//
////        kafkaRDD.foreach(line => {
////          println("line:" + line)
////          //          println("key=" + line._1 + "  value=" + line._2)
////        })
////        val lines = kafkaRDD.map(_.value())
////        val result = lines.map(line => {
////          var ss = JSON.parseObject(line)
////          println("ss:" + ss)
////        })
//        //        println("lines:" + lines)
//
//        //        val aaa = lines.map(line => {
//        //          //          println("line:" + line)
//        //
//        //          var result = JSON.parseObject(line)
//        //          result
//        //        })
//      }
//
//
//
//      //      if (kafkaRDD.isEmpty()) {
//      //        println("kafkaRDD is empty")
//      //      }
//      //      if (!kafkaRDD.isEmpty()) {
//      //        //获取当前批次的RDD的偏移量
//      //
//      //
//      //        val lines = kafkaRDD.map(_.value())
//      //        println("lines:" + lines)
//      //        //        val words =
//      //        val logBeanRDD = lines.map(line => {
//      //          try {
//      //            println("line:" + line)
//      ////            val transOrder = JSON.parseObject(line)
//      //          } catch {
//      //            case e: JSONException => {
//      //              //logger记录
//      //              logger.error("json解析错误！line:" + line, e)
//      //            }
//      //          }
//      //        })
//      //      }
//      //提交当前批次的偏移量，偏移量最后写入kafka
//      //      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
//    })
//
//
//
//    //    //如果使用SparkStream和Kafka直连方式整合，生成的kafkaDStream必须调用foreachRDD
//    //    kafkaDStream.foreachRDD(kafkaRDD => {
//    //      if (!kafkaRDD.isEmpty()) {
//    //        //获取当前批次的RDD的偏移量
//    //        val offsetRanges = kafkaRDD.asInstanceOf[HasOffsetRanges].offsetRanges
//    //
//    //        //拿出kafka中的数据
//    //        val lines = kafkaRDD.map(_.value())
//    //        //将lines字符串转换成json对象
//    //        val logBeanRDD = lines.map(line => {
//    //          var logBean: LogBean = null
//    //          try {
//    //            logBean = JSON.parseObject(line, classOf[LogBean])
//    //          } catch {
//    //            case e: JSONException => {
//    //              //logger记录
//    //              logger.error("json解析错误！line:" + line, e)
//    //            }
//    //          }
//    //          logBean
//    //        })
//    //
//    //        //过滤
//    //        val filteredRDD = logBeanRDD.filter(_ != null)
//    //
//    //        //将RDD转化成DataFrame,因为RDD中装的是case class
//    //        import spark.implicits._
//    //
//    //        val df = filteredRDD.toDF()
//    //
//    //        df.show()
//    //        //将数据写到hdfs中:hdfs://hd1:9000/360
//    //        df.repartition(1).write.mode(SaveMode.Append).parquet(args(0))
//    //
//    //        //提交当前批次的偏移量，偏移量最后写入kafka
//    //        kafkaDStream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
//    //      }
//    //    })
//
//    //    val lines = stream.map(_._2)
//    //    val words = stream.flatMap(_.split(" ")).map(x=>(x,1))
//
//    //    words.reduceByKey(_ + _).print()
//    //    kafkaValue.map(_.split(":"))
//
//
//    //entry
//    ssc.start()
//    ssc.awaitTermination()
//
//    //    es.hosts=staging-bigdata02;staging-bigdata03;staging-bigdata04
//  }
//
//}
