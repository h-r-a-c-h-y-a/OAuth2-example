package am.cerebrum.springoauth2.example.oath2example.service;

public interface TokenRedis {

    void add(String key, String token, int ttlSeconds);

    String get(String key);
}
