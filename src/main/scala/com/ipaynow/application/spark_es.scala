package com.ipaynow.application

import java.text.SimpleDateFormat
import java.util.Date

import com.ipaynow.utils.{AESUtil, LoadPropertiesUtil, RedisClient}
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark.rdd.EsSpark

object spark_es {

  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger(this.getClass)
    //    val configMap = LoadPropertiesUtil.loadConfig()
        val conf = new SparkConf().setAppName("spark_es").setMaster("local[2]")
//    val conf = new SparkConf().setAppName("spark_es").setMaster("yarn-client")
    conf.set("es.index.auto.create", "true")
    conf.set("es.nodes", "staging-bigdata02")
    conf.set("es.port", "9200")
    conf.set("spark.streaming.kafka.consumer.poll.ms", "30000")
    val ssc = new StreamingContext(conf, Seconds(3))

    //初始化kafka配置
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "staging-bigdata02:9092,staging-bigdata03:9092,staging-bigdata04:9092",
      //      "bootstrap.servers" -> "bigdata13:9092,bigdata14:9092,bigdata15:9092",
      "group.id" -> "dc_spark_test",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    //    val topic = Set("topic_trans") //参数传入
    val topic = Set("test_trans") //参数传入
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topic, kafkaParams)
    )
    val trans_secretkey = "c9997ab525a76a09"
    //    val trans_secretkey = configMap.get("trans_secretkey")
    stream.foreachRDD(KafKaRdd => {
      println("begin clean message！")
      println(KafKaRdd)
      val offsetRanges = KafKaRdd.asInstanceOf[HasOffsetRanges].offsetRanges //使用kafka异步提交偏移量
      try {
        if (!KafKaRdd.isEmpty()) {
          val cleanRdd = KafKaRdd.map(m => {
            val jedis = RedisClient.pool.getResource
            val fieldMap = jedis.hgetAll("dc.consumer.config.app.mapping.10000") //获取字段映射map
            val data = m.value()
            val dataJson = JSON.parseObject(data)
            val dataString = AESUtil.decrypt(trans_secretkey, dataJson.get("data").toString)
            val dataEntryObject = JSON.parseObject(dataString)
            import scala.collection.JavaConverters._
            val myScalaMap = fieldMap.asScala //将java map 转换为scala map
            val esJsonObject = transformMessage(dataEntryObject, myScalaMap)
            val stringMessage = esJsonObject.toJSONString
            println("开始写ES：" + stringMessage)
            RedisClient.pool.returnResource(jedis)
            stringMessage
          }
          )

          EsSpark.saveJsonToEs(cleanRdd, ("trans_order_all_{createTimeFormat}/query"),
            Map("es.mapping.id" -> "trans_id", "es.write.operation" -> "upsert"))
        }
      } catch {
        case e: Exception => logger.error("something is wrong!" + e.getMessage)
      }
      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges) //提交偏移量
    }
    )
    ssc.start()
    ssc.awaitTermination()
  }

  //判断消息是否为今日信息
  def judgeDate(message: String): Boolean = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val JsonObject = JSON.parseObject(message)
    val createTime = dateFormat.parse(JsonObject.get("createTime").toString.substring(0, 10)).getTime
    val now: Date = new Date()
    val todayZeroTime = dateFormat.parse(dateFormat.format(now)).getTime
    if (createTime >= todayZeroTime)
      true
    else
      false
  }

//  def toIndexName(source: String):String={
//    val array01 = source.split("/")
//    ""
//  }

  /**
    * 拼接新的esJsonObject
    *
    * @param dataEntryObject 消息实体
    * @param fieldMap        映射字段集合
    * @throws java.lang.Exception
    * @return
    */
  @throws(classOf[Exception])
  def transformMessage(dataEntryObject: JSONObject, fieldMap: collection.mutable.Map[String, String]):JSONObject = {

    val esJsonObject = new JSONObject()
    //遍历Map，将esJsonObject填充值
    val keySet = fieldMap.keys
    val key_iter = keySet.iterator //遍历,迭代map;
    while (key_iter.hasNext) {
      val key = key_iter.next()
      if (dataEntryObject.containsKey(key) && dataEntryObject.get(key) != null && !"".equals(dataEntryObject.get(key)))
        esJsonObject.put(fieldMap.get(key).get, dataEntryObject.get(key))
    }
    val createTime = dataEntryObject.get("createTime").toString
    val createTimeFormat = createTime.substring(0, 10).replaceAll("-", "")
    esJsonObject.put("createTimeFormat",createTimeFormat)    //添加日期格式化字段
//    对ch_settle_date 日期格式做滤空
    if (esJsonObject.containsKey("ch_settle_date")) {
      var ch_settle_date = esJsonObject.get("ch_settle_date").toString
      if (ch_settle_date.startsWith(" ")) ch_settle_date = ch_settle_date.trim
      esJsonObject.put("ch_settle_date", ch_settle_date)
    }
    esJsonObject
  }
}
