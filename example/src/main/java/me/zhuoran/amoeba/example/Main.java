package me.zhuoran.amoeba.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import me.zhuoran.amoeba.netty.server.HttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Demo Main
 */
@Configuration
public class Main {


    private static final int DEFAULT_PORT = 9999;


    public static void main(String[] args) throws Exception {


        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SpringApplication.run(Main.class, args);

        new HttpServer(DEFAULT_PORT, ctx).run(100, 65000);

    }


}
