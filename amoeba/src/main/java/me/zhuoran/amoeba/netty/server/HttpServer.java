/*
 * Copyright (c) 2012 Zhuoran Wang <zoran.wang@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.zhuoran.amoeba.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.LinkedList;
import java.util.List;

import me.zhuoran.amoeba.netty.server.http.AbstractExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * An HTTP server that sends back the content of the received HTTP request in a pretty plaintext form.
 */

public class HttpServer {
    
    private final static Logger logger = LoggerFactory.getLogger("amoeba");

    private final int                port;

    public static ApplicationContext ctx       = null;

    public static List<String>       executorNameList = new LinkedList<String>();

    public HttpServer(){
        port = 8081;
    }

    public HttpServer(final int port){
        this.port = port;
    }
    
    public HttpServer(final int port,ApplicationContext ctx){
        this.port = port;
        HttpServer.ctx = ctx;
        String[] executorNames = ctx.getBeanNamesForType(AbstractExecutor.class);
        for (String beanName : executorNames) {
            executorNameList.add(beanName.toLowerCase());
            logger.debug("Spring loaded bean name " +  beanName.toLowerCase());
        }
    }

    public void run(int eventLoopThreads, int maxContentLength) throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();// bossGroup线程池用来接受客户端的连接请求
        EventLoopGroup workerGroup = new NioEventLoopGroup(eventLoopThreads);// //workerGroup线程池用来处理boss线程池里面的连接的数据
        try {
            //ServerBootstrap是一个启动NIO服务的辅助启动类。你可以在这个服务中直接使用Channel，但是这会是一个复杂的处理过程，在很多情况下并不需要。
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);// 最大排队数量
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO)).childHandler(new HttpServerInitializer(maxContentLength));

            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
