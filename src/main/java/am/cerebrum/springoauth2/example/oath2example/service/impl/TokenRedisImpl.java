package am.cerebrum.springoauth2.example.oath2example.service.impl;

import am.cerebrum.springoauth2.example.oath2example.service.TokenRedis;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class TokenRedisImpl implements TokenRedis {

    private final JedisPool jedisPool;

    public TokenRedisImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void add(String key, String token, int ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, token);
            jedis.expire(key, ttlSeconds);
        }
    }

    @Override
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }
}
