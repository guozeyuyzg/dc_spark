package com.ipaynow.test

object testTrim {
  def main(args: Array[String]): Unit = {
      var a = " 00:00:00"
      if (a.startsWith(" ")) a =a.trim else print("false")
    print(a)
  }

}
