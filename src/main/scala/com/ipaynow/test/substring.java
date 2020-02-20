package com.ipaynow.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class substring {
    public static void main(String[] args) throws Exception{
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String createTime = "2019-07-03 18:33:55";
        String a = createTime.substring(0,10);
        System.out.println(a);
        Long b = dateFormat.parse(a).getTime();
        System.out.println(b);

        Date date = new Date();
        Long time = dateFormat.parse(dateFormat.format(date)).getTime();
        System.out.println(time);
    }
}
