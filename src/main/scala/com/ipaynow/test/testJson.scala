package com.ipaynow.test

import com.alibaba.fastjson.JSON

object testJson {
  def main(args: Array[String]): Unit = {
         val str = "{\"name\":\"jeemy\",\"age\":25,\"phone\":\"18810919225\"}"
         val jsonObject = JSON.parseObject(str)
         val name = jsonObject.get("name").toString
         println(name)
         val jsonkey = jsonObject.keySet()
         val iter = jsonkey.iterator
         while(iter.hasNext){
           val instance = iter.next()
           val value = jsonObject.get(instance).toString
           println(s"key:$instance,value:$value")
         }
  }
}
