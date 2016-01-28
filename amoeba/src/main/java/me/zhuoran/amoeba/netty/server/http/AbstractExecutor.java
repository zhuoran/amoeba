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
package me.zhuoran.amoeba.netty.server.http;

import io.netty.buffer.Unpooled;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;


/**
 *、
 * Each HTTP interface must extend the AbstractExecutor class, and will inject the class into the spring container!
 *
 * @author zoran
 */
public abstract class AbstractExecutor implements Executor {
    public AbstractExecutor() {
    }

    public FullHttpResponse createFullHttpResponse(String responseBody) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBody.getBytes()));
        return response;
    }

    public FullHttpResponse createFullHttpResponse(byte[] responseBody) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBody));
        return response;
    }
}
