package io.github.eggy03.papertrail.lite.service.handlers.auditlog;

import io.github.eggy03.papertrail.lite.listeners.auditlog.AbstractGuildAuditLogEntryCreateEventActionTypeHandler;
import io.github.eggy03.papertrail.lite.service.EmbedSendingService;
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
public final class MessageActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull String messageActionLogChannel;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public MessageActionTypeHandler(@ConfigProperty(name = "message.action.log.channel") @NonNull String messageActionLogChannel, @NonNull EmbedSendingService embedSendingService) {
        this.messageActionLogChannel = messageActionLogChannel;
        this.embedSendingService = embedSendingService;
    }

    // for the following four events, audit log does not expose much information
    // a custom implementation to log edits and deletes of messages is found in GuildMessageEventListener
    @Override
    public void onMessageCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
        // do nothing (these never trigger)
    }

    @Override
    public void onMessageUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
        // do nothing (these never trigger)
    }

    @Override
    public void onMessageDelete(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = executor != null ? executor.getAsMention() : ale.getUserId();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Message Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Executor: " + mentionableExecutor));
        eb.setColor(Color.LIGHT_GRAY);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, messageActionLogChannel);
    }

    @Override
    public void onMessageBulkDelete(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = executor != null ? executor.getAsMention() : ale.getUserId();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Bulk Message Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Executor: " + mentionableExecutor));
        eb.setColor(Color.LIGHT_GRAY);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, messageActionLogChannel);
    }
}
