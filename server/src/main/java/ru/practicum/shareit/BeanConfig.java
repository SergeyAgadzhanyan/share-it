package ru.practicum.shareit;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class BeanConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DbServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }
}
