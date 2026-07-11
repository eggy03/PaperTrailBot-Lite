package io.github.eggy03.papertrail.lite.handlers.auditlog;

import io.github.eggy03.papertrail.lite.listeners.auditlog.GuildAuditLogEntryCreateEventActionTypeHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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

    @Inject
    public UnknownActionTypeHandler(@ConfigProperty(name = "unknown.action.log.channel") @NonNull String unknownActionLogChannel) {
        this.unknownActionLogChannel = unknownActionLogChannel;
    }

    private void performChecksThenBuildAndSendEmbed(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull EmbedBuilder embedBuilder) {

        if (unknownActionLogChannel.equals("-1")) return;

        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(unknownActionLogChannel);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
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

        performChecksThenBuildAndSendEmbed(event, eb);
    }
}
