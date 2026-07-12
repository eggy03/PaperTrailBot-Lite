package io.github.eggy03.papertrail.lite.repository;

import io.github.eggy03.papertrail.lite.entity.CachedMessage;

public interface MessageRepository {

    void put (CachedMessage cachedMessage);

    CachedMessage get (String messageId);

    void delete (String messageId);
}
