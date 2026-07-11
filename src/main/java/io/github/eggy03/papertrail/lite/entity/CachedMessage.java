package io.github.eggy03.papertrail.lite.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.NonNull;

@RegisterForReflection
public record CachedMessage(
        @NonNull String messageId,
        @NonNull String messageContent,
        @NonNull String authorId
) {}