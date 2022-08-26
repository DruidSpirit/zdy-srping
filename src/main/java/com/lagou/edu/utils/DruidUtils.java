package com.lagou.edu.utils;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author 应癫
 */
public class DruidUtils {

    private DruidUtils(){
    }

    private static DruidDataSource druidDataSource = new DruidDataSource();


    static {
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:63306/zdy_mybatis?useUnicode=true&serverTimezone=GMT&characterEncoding=utf8&useSSL=false");
        druidDataSource.setUsername("druiduser");
        druidDataSource.setPassword("Wy@8180369");

    }

    public static DruidDataSource getInstance() {
        return druidDataSource;
    }

}
