package com.lagou.edu;

import com.lagou.edu.dto.ApplicationContext;
import com.lagou.edu.service.TransferService;

public class IocTest {
    public static void main(String[] args) throws Exception {
        //  初始化ioc容器
        ApplicationContext applicationContext = new ApplicationContext("beans.xml");
        TransferService transferService = (TransferService) applicationContext.getBean("transferService");
        transferService.transfer("6029621011001","6029621011000",100);

    }
}
