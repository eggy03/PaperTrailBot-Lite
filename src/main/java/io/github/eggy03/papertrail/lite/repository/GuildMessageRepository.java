package io.github.eggy03.papertrail.lite.repository;

import io.github.eggy03.papertrail.lite.entity.GuildMessage;

public interface GuildMessageRepository {

    void put (GuildMessage guildMessage);

    GuildMessage get (String messageId);

    void delete (String messageId);
}
