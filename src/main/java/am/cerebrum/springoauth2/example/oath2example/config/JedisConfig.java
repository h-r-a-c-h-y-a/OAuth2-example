package am.cerebrum.springoauth2.example.oath2example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private Integer redisPort;

    @Bean
    JedisPool jedisPool() throws Exception {

        JedisPoolConfig config = new JedisPoolConfig();

        config.setMinIdle(-1);
        config.setMaxIdle(100);
        config.setMaxTotal(100);
        config.setTimeBetweenEvictionRunsMillis(-1);
        config.setMinEvictableIdleTimeMillis(-1);
        config.setNumTestsPerEvictionRun(0);
        config.setSoftMinEvictableIdleTimeMillis(-1);

        config.setMaxWaitMillis(120000);
        config.setBlockWhenExhausted(true);
        config.setFairness(false);
        config.setLifo(false);

        config.setTestOnCreate(false);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);


        return new JedisPool(config, redisHost, redisPort, 50000, null);

    }
}
