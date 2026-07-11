package io.github.eggy03.papertrail.bot.entity;

import lombok.NonNull;

public record CachedMessage(
        @NonNull String messageId,
        @NonNull String messageContent,
        @NonNull String authorId
) {
}
