package com.platform.marketing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.marketing.websocket.TaskMonitorWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private TaskMonitorWebSocketHandler taskMonitorWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(taskMonitorWebSocketHandler, "/ws/task-monitor")
                .setAllowedOrigins("*"); // 生产环境中应该设置具体的域名
    }
}