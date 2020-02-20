package com.ipaynow.utils

import java.io.FileInputStream
import java.util
import java.util.Properties



object LoadPropertiesUtil extends Serializable{

  def loadConfig():util.HashMap[String,String]={
    val configMap = new util.HashMap[String,String]()
    val props = new Properties();

//    val path = Thread.currentThread().getContextClassLoader.getResource("config.properties").getPath
    props.load(new FileInputStream("config.properties"))
    props.keySet().toArray().foreach { x =>
      configMap.put(x.toString,props.getProperty(x.toString()))
    }
    configMap
  }

  def main(args: Array[String]): Unit = {
   val redisHost = loadConfig().get("redisHost").toString
    print("redisHost::::"+ redisHost)
  }

}
