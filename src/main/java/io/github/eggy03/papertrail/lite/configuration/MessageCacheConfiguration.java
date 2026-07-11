package io.github.eggy03.papertrail.lite.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.eggy03.papertrail.lite.entity.CachedMessage;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

@ApplicationScoped
public final class MessageCacheConfiguration {

    private final @NonNull Cache<String, CachedMessage> cache;

    public MessageCacheConfiguration(
            @ConfigProperty(name = "max.cached.messages") @NonNull Long maxCachedMessages,
            @ConfigProperty(name = "max.cached.message.retention.days") @NonNull Long expireAfterWrite) {

        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofDays(expireAfterWrite))
                .maximumSize(maxCachedMessages)
                .build();
    }

    public void put (@NonNull CachedMessage cachedMessage) {
        cache.put(cachedMessage.messageId(), cachedMessage);
    }

    public @Nullable CachedMessage get (@NonNull String messageId) {
        return cache.getIfPresent(messageId);
    }

    public void delete (@NonNull String messageId) {
        cache.invalidate(messageId);
    }
}
