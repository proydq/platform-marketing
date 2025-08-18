package com.platform.marketing.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Map;
import java.util.HashMap;

@Component
public class TaskMonitorWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("WebSocket连接建立: " + session.getId());
        
        // 发送连接成功消息
        Map<String, Object> message = new HashMap<>();
        message.put("type", "connection");
        message.put("status", "connected");
        message.put("timestamp", System.currentTimeMillis());
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket连接关闭: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("收到WebSocket消息: " + message.getPayload());
        // 这里可以处理客户端发送的消息，比如订阅特定任务的更新
    }

    /**
     * 广播任务状态更新
     */
    public void broadcastTaskUpdate(String taskId, String taskName, String status, int progress, String message) {
        Map<String, Object> update = new HashMap<>();
        update.put("type", "task_update");
        update.put("taskId", taskId);
        update.put("taskName", taskName);
        update.put("status", status);
        update.put("progress", progress);
        update.put("message", message);
        update.put("timestamp", System.currentTimeMillis());

        broadcastMessage(update);
    }

    /**
     * 广播营销活动状态更新
     */
    public void broadcastCampaignUpdate(String campaignId, String campaignName, String status, String channel, String message) {
        Map<String, Object> update = new HashMap<>();
        update.put("type", "campaign_update");
        update.put("campaignId", campaignId);
        update.put("campaignName", campaignName);
        update.put("status", status);
        update.put("channel", channel);
        update.put("message", message);
        update.put("timestamp", System.currentTimeMillis());

        broadcastMessage(update);
    }

    /**
     * 广播系统统计数据更新
     */
    public void broadcastStatsUpdate(Map<String, Object> stats) {
        Map<String, Object> update = new HashMap<>();
        update.put("type", "stats_update");
        update.put("data", stats);
        update.put("timestamp", System.currentTimeMillis());

        broadcastMessage(update);
    }

    /**
     * 广播消息给所有连接的客户端
     */
    private void broadcastMessage(Map<String, Object> message) {
        String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            System.err.println("JSON序列化失败: " + e.getMessage());
            return;
        }

        sessions.removeIf(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(messageJson));
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                System.err.println("发送WebSocket消息失败: " + e.getMessage());
                return true;
            }
        });
    }

    /**
     * 获取当前连接数
     */
    public int getActiveConnectionCount() {
        return sessions.size();
    }
}