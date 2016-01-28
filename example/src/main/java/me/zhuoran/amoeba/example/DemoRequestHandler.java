package me.zhuoran.amoeba.example;

import io.netty.buffer.ByteBuf;
import me.zhuoran.amoeba.netty.server.http.AbstractExecutor;
import me.zhuoran.amoeba.netty.server.http.AmoebaHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by zoran on 16/1/28.
 */

/**
 * you can use http://hostip:port/demo request to this handler!
 * "demo" is service(bean) name for spring container
 */
@Service("demo")
public class DemoRequestHandler  extends AbstractExecutor
{


    private String parseAmoebaHttpRequest(AmoebaHttpRequest request) throws IllegalArgumentException {
        String requestBody = "";
        ByteBuf buffer = null;
        try{
            if (request.getHttpContent() != null) {
                buffer =request.getHttpContent();
                byte[] input = new byte[buffer.capacity()];
                for (int i = 0; i < buffer.capacity(); i ++) {
                    input[i] = buffer.getByte(i);
                }
                requestBody = new String(input);
            }
        }catch(Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
        return requestBody;
    }


    @Override
    public Object execute(AmoebaHttpRequest request) {

        try {
            String requestBody = parseAmoebaHttpRequest(request);
            if (StringUtils.isEmpty(requestBody)) {
                return null;
            }else{
                return "This is a response string from amoeba demo!";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
