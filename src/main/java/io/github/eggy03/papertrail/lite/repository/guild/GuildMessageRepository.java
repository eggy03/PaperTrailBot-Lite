package io.github.eggy03.papertrail.lite.repository.guild;

import io.github.eggy03.papertrail.lite.entity.guild.GuildMessage;

public interface GuildMessageRepository {

    void put (GuildMessage guildMessage);

    GuildMessage get (String messageId);

    void delete (String messageId);
}
