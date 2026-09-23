package com.example.enterprise.common.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Thin Redis helper around {@link RedisTemplate} / {@link StringRedisTemplate}.
 * <p>
 * Only registered when a {@link RedisConnectionFactory} bean exists.
 * <p>
 * Built-in helpers for:
 * <ul>
 *   <li>穿透：{@code getOrLoad} with null placeholder</li>
 *   <li>雪崩：{@code ttlWithJitter}</li>
 *   <li>一致性：业务侧先写 DB 再 {@code delete}</li>
 * </ul>
 */
@Component
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // ---------- Object value ----------

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * Set with base TTL plus random jitter (0 ~ {@link RedisConstants#TTL_JITTER_SECONDS} seconds).
     */
    public void setWithJitter(String key, Object value, Duration baseTtl) {
        set(key, value, ttlWithJitter(baseTtl));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    public Boolean expire(String key, Duration ttl) {
        return redisTemplate.expire(key, ttl);
    }

    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * Set if absent (simple distributed lock primitive).
     */
    public Boolean setIfAbsent(String key, Object value, Duration ttl) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
    }

    /**
     * Cache-aside read with null-value protection (anti penetration).
     * <p>
     * Flow: Redis hit → return; miss → load from DB → cache (including null placeholder).
     *
     * @param key      cache key
     * @param ttl      TTL for real values (null uses {@link RedisConstants#NULL_VALUE_TTL})
     * @param loader   DB / remote loader; may return null
     * @param typeHint unused at runtime, documents expected type
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrLoad(String key, Duration ttl, Supplier<T> loader, Class<T> typeHint) {
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            if (RedisConstants.NULL_PLACEHOLDER.equals(cached)) {
                return null;
            }
            return (T) cached;
        }
        T value = loader.get();
        if (value == null) {
            redisTemplate.opsForValue().set(key, RedisConstants.NULL_PLACEHOLDER, RedisConstants.NULL_VALUE_TTL);
            return null;
        }
        setWithJitter(key, value, ttl);
        return value;
    }

    /**
     * After DB update/delete: remove cache key (cache-aside invalidation).
     */
    public void evict(String key) {
        delete(key);
    }

    // ---------- String value ----------

    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void setString(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    /**
     * Increment and ensure TTL is set on first write (e.g. login fail counter).
     */
    public long incrementWithExpire(String key, Duration ttl) {
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(key, ttl);
        }
        return count == null ? 0L : count;
    }

    // ---------- helpers ----------

    public static Duration ttlWithJitter(Duration base) {
        long jitter = ThreadLocalRandom.current().nextLong(0, RedisConstants.TTL_JITTER_SECONDS + 1);
        return base.plusSeconds(jitter);
    }
}
