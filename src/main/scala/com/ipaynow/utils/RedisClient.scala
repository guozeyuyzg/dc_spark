package com.ipaynow.utils

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.JedisPool

object RedisClient extends Serializable {
//  val configMap = LoadPropertiesUtil.loadConfig()
//  val redisHost = configMap.get("redisHost")
//  val redisPort = configMap.get("redisPort").toInt
//  val redisPasswd = configMap.get("redisPasswd")
//  val redisTimeout = configMap.get("redisTimeout").toInt
  val redisHost = "192.168.133.109"
  val redisPort = 6379
  val redisPasswd = "1payn0w123"
  val redisTimeout = 30000
  lazy val pool = new JedisPool(new GenericObjectPoolConfig(), redisHost, redisPort,redisTimeout,redisPasswd)

  lazy val hook = new Thread {
    override def run = {
      println("Execute hook thread: " + this)
      pool.destroy()
    }
  }
  sys.addShutdownHook(hook.run)
  def main(args: Array[String]): Unit = {
    val dbIndex = 0
    val jedis = RedisClient.pool.getResource
    val fieldMap =jedis.hgetAll("dc.consumer.config.app.mapping.10000")
    val keySet = fieldMap.keySet()
    val key_iter = keySet.iterator  //遍历,迭代map;
    while (key_iter.hasNext){
      val key = key_iter.next
      println(key + ":" + fieldMap.get(key))
    }
    RedisClient.pool.returnResource(jedis)
  }
}
