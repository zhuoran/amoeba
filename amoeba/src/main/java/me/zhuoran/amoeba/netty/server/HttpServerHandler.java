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


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.zhuoran.amoeba.netty.server.http.HttpRequestHandler;
import me.zhuoran.amoeba.netty.server.http.AmoebaHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class HttpServerHandler extends SimpleChannelInboundHandler<Object> {


    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    public HttpServerHandler() {
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public abstract FullHttpResponse getHttpResponse(AmoebaHttpRequest var1);

    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        AmoebaHttpRequest request = null;
        if(msg instanceof HttpRequest) {
            HttpRequest httpContent = (HttpRequest)msg;
            if(!httpContent.getDecoderResult().isSuccess()) {
                sendHttpResponse(ctx, httpContent, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return;
            }

            if(httpContent.getMethod() != HttpMethod.GET && httpContent.getMethod() != HttpMethod.POST) {
                sendHttpResponse(ctx, httpContent, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
                return;
            }

            if(HttpHeaders.is100ContinueExpected(httpContent)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }

            String uri = HttpRequestHandler.sanitizeUri(httpContent.getUri());
            if(!HttpServer.executorNameList.contains(uri)) {
                DefaultFullHttpResponse response1 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
                sendHttpResponse(ctx, httpContent, response1);
                return;
            }

            request = new AmoebaHttpRequest(httpContent, ctx.channel().id().asLongText());
        }

        if(msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent)msg;
            ByteBuf content = httpContent.content();
            request.setHttpContent(content);
            request.setContent(content.toString(CharsetUtil.UTF_8));
            if(msg instanceof LastHttpContent) {
                FullHttpResponse response = this.getHttpResponse(request);
                this.writeResponse(request, response, ctx);
            }
        }

    }

    private boolean writeResponse(AmoebaHttpRequest request, FullHttpResponse response, ChannelHandlerContext ctx) {
        boolean keepAlive = HttpHeaders.isKeepAlive(request.getHttpRequest());
        if(keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, Integer.valueOf(response.content().readableBytes()));
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        ctx.writeAndFlush(response);
        return keepAlive;
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, FullHttpResponse res) {
        if(res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, (long)res.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if(!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error("Server Exception ：" + ctx.channel().remoteAddress() + " " + cause.getMessage(), cause);
        ctx.close();
    }

}
