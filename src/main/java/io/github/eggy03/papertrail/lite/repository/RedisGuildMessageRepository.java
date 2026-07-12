package io.github.eggy03.papertrail.lite.repository;

import io.github.eggy03.papertrail.lite.entity.GuildMessage;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

public final class RedisGuildMessageRepository implements GuildMessageRepository {

    private final @NonNull RedisDataSource redisDataSource;
    private final @NonNull ValueCommands<String, GuildMessage> valueCommands;
    private final long expireAfterSeconds;

    @Inject
    public RedisGuildMessageRepository(
            @NonNull RedisDataSource redisDataSource,
            @ConfigProperty(name = "redis.message.retention.days") long expireAfterDays
    ) {
        this.redisDataSource = redisDataSource;
        this.valueCommands = redisDataSource.value(String.class, GuildMessage.class);
        this.expireAfterSeconds = Duration.ofDays(expireAfterDays).getSeconds();
    }

    @Override
    public void put(@NonNull GuildMessage guildMessage) {
        valueCommands.setex(guildMessage.messageId(), expireAfterSeconds, guildMessage);
    }

    @Contract(pure = true)
    @Override
    public @Nullable GuildMessage get(@NonNull String messageId) {
        return valueCommands.get(messageId);
    }

    @Override
    public void delete(@NonNull String messageId) {
        redisDataSource.key(String.class).del(messageId);
    }
}
