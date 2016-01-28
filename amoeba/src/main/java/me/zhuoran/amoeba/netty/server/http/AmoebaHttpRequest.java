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

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 *
 * This is a HttpRequest wrapper
 *
 */
public class AmoebaHttpRequest {

    private HttpRequest httpRequest;
    private String method;
    private String uri;
    private Map<String, List<String>> params;
    private String content;
    private ByteBuf httpContent;

    public AmoebaHttpRequest(HttpRequest request, String channelId) {
        if(request != null && channelId != null) {
            this.httpRequest = request;
            this.uri = request.getUri();
            this.method = request.getMethod().toString();
            if(this.getMethod().equals("GET")) {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
                this.params = queryStringDecoder.parameters();
            }

        } else {
            throw new IllegalArgumentException();
        }
    }

    public HttpRequest getHttpRequest() {
        return this.httpRequest;
    }

    public String getContent() {
        return this.content;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, List<String>> getParams() {
        return this.params;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ByteBuf getHttpContent() {
        return this.httpContent;
    }

    public void setHttpContent(ByteBuf httpContent) {
        this.httpContent = httpContent;
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.content == null?0:this.content.hashCode());
        result1 = 31 * result1 + (this.method == null?0:this.method.hashCode());
        result1 = 31 * result1 + (this.params == null?0:this.params.hashCode());
        result1 = 31 * result1 + (this.uri == null?0:this.uri.hashCode());
        return result1;
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj == null) {
            return false;
        } else if(this.getClass() != obj.getClass()) {
            return false;
        } else {
            AmoebaHttpRequest other = (AmoebaHttpRequest)obj;
            if(this.content == null) {
                if(other.content != null) {
                    return false;
                }
            } else if(!this.content.equals(other.content)) {
                return false;
            }

            if(this.method == null) {
                if(other.method != null) {
                    return false;
                }
            } else if(!this.method.equals(other.method)) {
                return false;
            }

            if(this.params == null) {
                if(other.params != null) {
                    return false;
                }
            } else if(!this.params.equals(other.params)) {
                return false;
            }

            if(this.uri == null) {
                if(other.uri != null) {
                    return false;
                }
            } else if(!this.uri.equals(other.uri)) {
                return false;
            }

            return true;
        }
    }

    public String toString() {
        return "AmoebaHttpRequest [method=" + this.method + ", uri=" + this.uri + ", params=" + this.params + ", content=" + this.content + "]";
    }

}
