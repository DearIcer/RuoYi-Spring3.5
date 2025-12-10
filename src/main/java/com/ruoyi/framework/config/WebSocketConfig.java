package com.ruoyi.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.ruoyi.framework.websocket.ClientWebSocketHandler;
import com.ruoyi.framework.websocket.ClientWebSocketInterceptor;

/**
 * WebSocket配置
 * 
 * @author ruoyi
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer
{
    @Autowired
    private ClientWebSocketHandler clientWebSocketHandler;

    @Autowired
    private ClientWebSocketInterceptor clientWebSocketInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        // 客户端WebSocket端点，允许跨域
        registry.addHandler(clientWebSocketHandler, "/ws/client")
                .addInterceptors(clientWebSocketInterceptor)
                .setAllowedOrigins("*");
    }
}
