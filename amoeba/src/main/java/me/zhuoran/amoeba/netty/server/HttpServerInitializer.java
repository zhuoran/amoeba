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

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import me.zhuoran.amoeba.netty.server.http.HttpRequestHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private int maxContentLength = 65536;

    public HttpServerInitializer(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("logger", new LoggingHandler(LogLevel.INFO));
        p.addLast("http-decoder", new HttpRequestDecoder());
        p.addLast("http-aggregator", new HttpObjectAggregator(this.maxContentLength));
        p.addLast("codec", new HttpServerCodec());
        p.addLast("handler", new HttpRequestHandler());
    }
}
