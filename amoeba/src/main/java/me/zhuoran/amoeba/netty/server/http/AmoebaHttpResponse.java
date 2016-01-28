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

import io.netty.handler.codec.http.HttpResponseStatus;


public final class AmoebaHttpResponse {
    private HttpResponseStatus status;
    private String result;

    public AmoebaHttpResponse(String buffer, HttpResponseStatus status) {
        this.result = buffer;
        if(buffer == null || buffer.isEmpty()) {
            this.result = new String("No content!");
        }

        this.status = status;
    }

    public HttpResponseStatus getStatus() {
        return this.status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public String getBuffer() {
        return this.result;
    }

    public void setBuffer(String buffer) {
        this.result = buffer;
    }
}
