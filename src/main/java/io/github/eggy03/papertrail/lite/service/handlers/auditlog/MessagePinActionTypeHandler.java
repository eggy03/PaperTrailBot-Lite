package io.github.eggy03.papertrail.lite.service.handlers.auditlog;

import io.github.eggy03.papertrail.lite.listeners.auditlog.AbstractGuildAuditLogEntryCreateEventActionTypeHandler;
import io.github.eggy03.papertrail.lite.service.EmbedSendingService;
import io.github.eggy03.papertrail.lite.utils.auditlog.MessageUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class MessagePinActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull String messagePinActionLogChannel;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public MessagePinActionTypeHandler(@ConfigProperty(name = "message.pin.action.log.channel") @NonNull String messagePinActionLogChannel, @NonNull EmbedSendingService embedSendingService) {
        this.messagePinActionLogChannel = messagePinActionLogChannel;
        this.embedSendingService = embedSendingService;
    }

    @Override
    public void onMessagePin(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Message Pin Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User target = ale.getJDA().getUserById(ale.getTargetId());
        String mentionableTarget = (target != null ? target.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Message Pinned By: " + mentionableExecutor + "\nMessage Author: " + mentionableTarget));
        eb.setColor(Color.PINK);

        eb.addField(
                MarkdownUtil.underline("Pinned Message Jump URL"),
                MessageUtils.resolveMessageJumpUrlFromId(ale.getOptionByName("channel_id"), ale.getOptionByName("message_id"), event),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, messagePinActionLogChannel);
    }

    @Override
    public void onMessageUnpin(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Message Unpin Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User target = ale.getJDA().getUserById(ale.getTargetId());
        String mentionableTarget = (target != null ? target.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Message Un-Pinned By: " + mentionableExecutor + "\nMessage Author: " + mentionableTarget));
        eb.setColor(Color.MAGENTA);

        eb.addField(
                MarkdownUtil.underline("Un-Pinned Message Jump URL"),
                MessageUtils.resolveMessageJumpUrlFromId(ale.getOptionByName("channel_id"), ale.getOptionByName("message_id"), event),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, messagePinActionLogChannel);
    }
}
