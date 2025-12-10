package com.ruoyi.framework.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.project.client.domain.ClientLoginUser;
import com.ruoyi.project.client.service.ClientLoginService;

/**
 * 客户端WebSocket处理器
 * 
 * @author ruoyi
 */
@Component
public class ClientWebSocketHandler extends TextWebSocketHandler
{
    private static final Logger log = LoggerFactory.getLogger(ClientWebSocketHandler.class);

    /**
     * 用户名 -> WebSocket会话 的映射
     * 用于根据用户名查找会话，实现踢人推送
     */
    private static final Map<String, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();

    /**
     * 会话ID -> 用户名 的映射
     * 用于在连接关闭时清理USER_SESSIONS
     */
    private static final Map<String, String> SESSION_USERS = new ConcurrentHashMap<>();

    @Autowired
    private ClientLoginService clientLoginService;

    /**
     * 连接建立后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        String token = (String) session.getAttributes().get("token");
        if (token != null)
        {
            // 验证token并获取用户信息
            ClientLoginUser loginUser = clientLoginService.getLoginUserByToken(token);
            if (loginUser != null)
            {
                String username = loginUser.getUsername();
                
                // 如果该用户已有WebSocket连接，先关闭旧连接
                WebSocketSession oldSession = USER_SESSIONS.get(username);
                if (oldSession != null && oldSession.isOpen())
                {
                    try
                    {
                        // 发送踢出消息给旧连接
                        sendKickOutMessage(oldSession, "您的账号在其他设备登录");
                        oldSession.close(CloseStatus.NORMAL);
                    }
                    catch (IOException e)
                    {
                        log.error("关闭旧WebSocket连接失败", e);
                    }
                }
                
                // 保存新连接
                USER_SESSIONS.put(username, session);
                SESSION_USERS.put(session.getId(), username);
                
                log.info("客户端WebSocket连接建立，用户: {}, 会话ID: {}", username, session.getId());
                
                // 发送连接成功消息
                sendMessage(session, "connected", "WebSocket连接成功");
            }
            else
            {
                log.warn("WebSocket连接失败，无效的token");
                session.close(CloseStatus.NOT_ACCEPTABLE);
            }
        }
        else
        {
            log.warn("WebSocket连接失败，缺少token");
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * 收到消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception
    {
        String payload = message.getPayload();
        log.debug("收到WebSocket消息: {}", payload);
        
        // 处理心跳消息
        if ("ping".equals(payload))
        {
            sendMessage(session, "pong", null);
        }
    }

    /**
     * 连接关闭后
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception
    {
        String username = SESSION_USERS.remove(session.getId());
        if (username != null)
        {
            // 只有当前会话是该用户的最新会话时才移除
            WebSocketSession currentSession = USER_SESSIONS.get(username);
            if (currentSession != null && currentSession.getId().equals(session.getId()))
            {
                USER_SESSIONS.remove(username);
            }
            log.info("客户端WebSocket连接关闭，用户: {}, 状态: {}", username, status);
        }
    }

    /**
     * 传输错误
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception
    {
        log.error("WebSocket传输错误，会话ID: {}", session.getId(), exception);
        if (session.isOpen())
        {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    /**
     * 向指定用户发送踢出通知
     * 
     * @param username 用户名
     * @param reason 踢出原因
     */
    public void sendKickOutNotification(String username, String reason)
    {
        WebSocketSession session = USER_SESSIONS.get(username);
        if (session != null && session.isOpen())
        {
            try
            {
                sendKickOutMessage(session, reason);
                log.info("已向用户 {} 发送踢出通知", username);
                // 发送完踢出消息后主动关闭连接
                session.close(CloseStatus.NORMAL);
                log.info("已关闭用户 {} 的WebSocket连接", username);
            }
            catch (IOException e)
            {
                log.error("发送踢出通知失败，用户: {}", username, e);
            }
        }
    }

    /**
     * 发送踢出消息
     */
    private void sendKickOutMessage(WebSocketSession session, String reason) throws IOException
    {
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("type", "kickout");
        data.put("message", reason);
        data.put("timestamp", System.currentTimeMillis());
        session.sendMessage(new TextMessage(JSON.toJSONString(data)));
    }

    /**
     * 发送消息
     */
    private void sendMessage(WebSocketSession session, String type, String message) throws IOException
    {
        if (session.isOpen())
        {
            Map<String, Object> data = new ConcurrentHashMap<>();
            data.put("type", type);
            if (message != null)
            {
                data.put("message", message);
            }
            data.put("timestamp", System.currentTimeMillis());
            session.sendMessage(new TextMessage(JSON.toJSONString(data)));
        }
    }

    /**
     * 获取在线用户数
     */
    public int getOnlineCount()
    {
        return USER_SESSIONS.size();
    }

    /**
     * 检查用户是否在线（有WebSocket连接）
     */
    public boolean isOnline(String username)
    {
        WebSocketSession session = USER_SESSIONS.get(username);
        return session != null && session.isOpen();
    }
}
