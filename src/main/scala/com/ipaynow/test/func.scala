package com.ipaynow.test

object func {
  def main(args: Array[String]): Unit = {

    printStrings("amy","sun","llo")

    for(i<- 1 to 10){
      println(i+"的阶乘："+factorial(i))
    }

    println("指定参数值："+ addInt())

    println(multi(2))

    val result = add(1)
    println(result)

    val sum = result(2)
    println(sum)

    val sum1 = klhFunc(1)(2)
    println(sum1)


    val a = new StringBuilder
     a+='c'
     a++="helloc"
     a+='d'
     println("a:"+a)


    var arr = new Array[String](3)
    var arr1 = Array("ni","hao","man")
    println(arr1(0))

    for(x<-arr1){
      println(x)
    }

    for(i<-0 to arr1.length-1){
      println(arr1(i))
    }

    val arr2 = Array("wo","hen","hao")

    import Array._
    val arr3 = concat(arr1,arr2)
    for(x<-arr3){
      println(x)
    }

  }

    def printStrings(args:String* ) = {
        var i = 0
        for(arg <- args){
          println("this is "+i+" message! "+arg)
          i+=1
        }
    }

     def factorial(n:Int):BigInt={
        if(n<=1)
          1
       else
          n*factorial(n-1)
     }

     def addInt(a:Int=6,b:Int= 7):Int={
       var sum = 0
       sum = a+b
       sum
     }

     def apply(f: Int => String,n:Int)={

     }

     def multi(param:Int):Int = {

       def innerMulti(parmam1:Int,param2:Int):Int ={
          parmam1* param2
       }
       innerMulti(param,2)
     }

    def klhFunc (x:Int)(y:Int) = x+y

    def add(x:Int)=(y:Int)=>x+y

}
