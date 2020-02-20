package com.ipaynow.test

import java.io.FileInputStream
import java.util.Properties

object test_properties {

  def main(args: Array[String]): Unit = {
    val props = new Properties();
    val path = Thread.currentThread().getContextClassLoader.getResource("config.properties").getPath
    props.load(new FileInputStream(path))
    props.keySet().toArray().foreach { x =>
      println(x + "\t一 " + props.getProperty(x.toString()))
    }

  }

}
