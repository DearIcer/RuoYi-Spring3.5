package com.ruoyi.framework.websocket;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import com.ruoyi.common.utils.StringUtils;

/**
 * 客户端WebSocket握手拦截器
 * 
 * @author ruoyi
 */
@Component
public class ClientWebSocketInterceptor implements HandshakeInterceptor
{
    private static final Logger log = LoggerFactory.getLogger(ClientWebSocketInterceptor.class);

    /**
     * 握手前处理
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception
    {
        if (request instanceof ServletServerHttpRequest)
        {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            // 从URL参数获取token
            String token = servletRequest.getServletRequest().getParameter("token");
            if (StringUtils.isNotEmpty(token))
            {
                attributes.put("token", token);
                log.debug("WebSocket握手，token: {}", token.substring(0, Math.min(20, token.length())) + "...");
                return true;
            }
            log.warn("WebSocket握手失败，缺少token参数");
            return false;
        }
        return false;
    }

    /**
     * 握手后处理
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception)
    {
        // 握手完成后的处理（可选）
    }
}
