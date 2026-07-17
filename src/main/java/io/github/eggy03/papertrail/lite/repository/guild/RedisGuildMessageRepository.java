package io.github.eggy03.papertrail.lite.repository.guild;

import io.github.eggy03.papertrail.lite.configuration.PaperTrailConfig;
import io.github.eggy03.papertrail.lite.entity.guild.GuildMessage;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

@ApplicationScoped
public final class RedisGuildMessageRepository implements GuildMessageRepository {

    private final @NonNull KeyCommands<String> keyCommands;
    private final @NonNull ValueCommands<String, GuildMessage> valueCommands;
    private final @NonNull Duration retentionDuration;

    private static final @NonNull String KEY_PREFIX = "ptrail-lite:";

    @Inject
    public RedisGuildMessageRepository(@NonNull RedisDataSource redisDataSource, @NonNull PaperTrailConfig paperTrailConfig) {
        this.keyCommands = redisDataSource.key(String.class);
        this.valueCommands = redisDataSource.value(String.class, GuildMessage.class);
        this.retentionDuration = Duration.ofDays(paperTrailConfig.guild().messageEvent().retentionDays());
    }

    @Override
    public void put(@NonNull GuildMessage guildMessage) {
        valueCommands.setex(
                KEY_PREFIX + guildMessage.messageId(),
                retentionDuration.getSeconds(),
                guildMessage
        );
    }

    @Contract(pure = true)
    @Override
    public @Nullable GuildMessage get(@NonNull String messageId) {
        return valueCommands.get(KEY_PREFIX + messageId);
    }

    @Override
    public void delete(@NonNull String messageId) {
        keyCommands.del(KEY_PREFIX + messageId);
    }
}
