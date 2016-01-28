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
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import me.zhuoran.amoeba.netty.server.HttpServer;
import me.zhuoran.amoeba.netty.server.HttpServerHandler;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 *
 * The handler distribute request to each uri's executor and make response.
 * Response only support string or byte array
 */
public class HttpRequestHandler extends HttpServerHandler {

    public static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
    public static final String CONTENT_TYPE_JSON = "text/json; charset=UTF-8";
    public static final String CONTENT_TYPE_STREAM = "application/octet-stream";
    public static final String CONTENT_TYPE_XML = "text/xml; charset=UTF-8";
    public static final String CONTENT_TYPE_TEXT = "text/plain; charset=UTF-8";

    @Override
    public FullHttpResponse getHttpResponse(AmoebaHttpRequest request) {
        DefaultFullHttpResponse response = null;
        HttpRequest req = request.getHttpRequest();

        try {
            String service = sanitizeUri(req.getUri());
            Executor commond = (Executor) HttpServer.ctx.getBean(service);
            Object result = commond.execute(request);
            if (result == null) {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
            }

            if (result instanceof String) {
                String content = (String) result;
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(content.getBytes()));
                response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
            } else {
                if (!(result instanceof byte[])) {
                    if (result instanceof FullHttpResponse) {
                        return (FullHttpResponse) result;
                    }

                    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
                }

                byte[] content = (byte[])result;
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
                response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/octet-stream");
            }
        } catch (NoSuchBeanDefinitionException exception) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        } catch (Throwable t) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            t.printStackTrace();
        }

        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, Integer.valueOf(response.content().readableBytes()));
        return response;
    }


    /**
     * sanitize url
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    public static String sanitizeUri(String uri) throws URISyntaxException {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException var4) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException var3) {
                throw new Error();
            }
        }

        if (!uri.startsWith("/")) {
            return null;
        } else {
            URI uriObject = new URI(uri);

            for (uri = uriObject.getPath(); uri != null && uri.startsWith("/"); uri = uri.substring(1)) {
            }

            return uri.toLowerCase();
        }
    }


}
