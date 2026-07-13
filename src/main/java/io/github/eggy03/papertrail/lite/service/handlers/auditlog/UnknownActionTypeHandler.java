package io.github.eggy03.papertrail.lite.service.handlers.auditlog;

import io.github.eggy03.papertrail.lite.listeners.auditlog.GuildAuditLogEntryCreateEventActionTypeHandler;
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

// for events not defined by JDA
@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class UnknownActionTypeHandler extends GuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull String unknownActionLogChannel;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public UnknownActionTypeHandler(@ConfigProperty(name = "unknown.action.log.channel") @NonNull String unknownActionLogChannel, @NonNull EmbedSendingService embedSendingService) {
        this.unknownActionLogChannel = unknownActionLogChannel;
        this.embedSendingService = embedSendingService;
    }

    @Override
    public void onUnknownActionType(@NonNull GuildAuditLogEntryCreateEvent event) {


        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = executor != null ? executor.getAsMention() : ale.getUserId();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Generic Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Executor: " + mentionableExecutor));
        eb.setColor(Color.LIGHT_GRAY);

        eb.addField(MarkdownUtil.underline("Action Type"), MarkdownUtil.codeblock(ale.getType().toString()), true);
        eb.addField(MarkdownUtil.underline("Target Type"), MarkdownUtil.codeblock(ale.getTargetType().toString()), true);

        ale.getChanges().forEach((changeKey, changeValue) ->
                eb.addField(
                        MarkdownUtil.underline(changeKey),
                        MarkdownUtil.codeblock("OLD VALUE\n" + changeValue.getOldValue() + "\nNEW VALUE\n" + changeValue.getNewValue()),
                        false
                )
        );

        embedSendingService.checkAndSend(event, eb, unknownActionLogChannel);
    }
}
