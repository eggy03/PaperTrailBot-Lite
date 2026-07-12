package io.github.eggy03.papertrail.lite.repository;

import io.github.eggy03.papertrail.lite.entity.CachedMessage;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

public final class RedisMessageRepository implements MessageRepository {

    private final @NonNull RedisDataSource redisDataSource;
    private final @NonNull ValueCommands<String, CachedMessage> valueCommands;
    private final long expireAfterSeconds;

    @Inject
    public RedisMessageRepository(
            @NonNull RedisDataSource redisDataSource,
            @ConfigProperty(name = "redis.message.retention.days") long expireAfterDays
    ) {
        this.redisDataSource = redisDataSource;
        this.valueCommands = redisDataSource.value(String.class, CachedMessage.class);
        this.expireAfterSeconds = Duration.ofDays(expireAfterDays).getSeconds();
    }

    @Override
    public void put(@NonNull CachedMessage cachedMessage) {
        valueCommands.setex(cachedMessage.messageId(), expireAfterSeconds, cachedMessage);
    }

    @Contract(pure = true)
    @Override
    public @Nullable CachedMessage get(@NonNull String messageId) {
        return valueCommands.get(messageId);
    }

    @Override
    public void delete(@NonNull String messageId) {
        redisDataSource.key(String.class).del(messageId);
    }
}
