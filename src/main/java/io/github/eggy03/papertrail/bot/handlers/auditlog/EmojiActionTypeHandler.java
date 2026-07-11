package io.github.eggy03.papertrail.bot.handlers.auditlog;

import io.github.eggy03.papertrail.bot.listeners.auditlog.GuildAuditLogEntryCreateEventActionTypeHandler;

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

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class EmojiActionTypeHandler extends GuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull String emojiActionLogChannel;

    @Inject
    public EmojiActionTypeHandler(@ConfigProperty(name = "emoji.action.log.channel") @NonNull String emojiActionLogChannel) {
        this.emojiActionLogChannel = emojiActionLogChannel;
    }

    private void performChecksThenBuildAndSendEmbed(@NonNull GuildAuditLogEntryCreateEvent event, @NonNull EmbedBuilder embedBuilder) {

        if (emojiActionLogChannel.equals("DISABLE")) return;

        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }
        
        TextChannel sendingChannel = event.getGuild().getTextChannelById(emojiActionLogChannel);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    @Override
    public void onEmojiCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
       
        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Emoji Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Emoji Created By: " + mentionableExecutor));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            if (changeKey.equals("name")) {
                eb.addField(MarkdownUtil.underline("Emoji Name"), "╰┈➤" + newValue, false);
                eb.addField(MarkdownUtil.underline("Emoji"), "╰┈➤" + "<:" + newValue + ":" + ale.getTargetId() + ">", false); // ale's TargetID retrieves the ID of the created emoji
            } else {
                eb.addField("Unimplemented Change Key", changeKey, false);
                log.info("Unimplemented Change Key on Emoji Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb);
    }

    @Override
    public void onEmojiUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
       
        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());


        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Emoji Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Emoji Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            if (changeKey.equals("name")) {
                eb.addField(MarkdownUtil.underline("Emoji Name Updated"), "╰┈➤" + "From " + oldValue + " to " + newValue, false);
                eb.addField(MarkdownUtil.underline("Target Emoji"), "╰┈➤" + "<:" + newValue + ":" + ale.getTargetId() + ">", false);
            } else {
                eb.addField("Unimplemented Change Key", changeKey, false);
                log.info("Unimplemented Change Key on Emoji Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb);
    }

    @Override
    public void onEmojiDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
       
        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Emoji Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Emoji Deleted By: " + mentionableExecutor));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            if (changeKey.equals("name")) {
                eb.addField(MarkdownUtil.underline("Emoji Name"), "╰┈➤" + oldValue, false);
            } else {
                eb.addField("Unimplemented Change Key", changeKey, false);
                log.info("Unimplemented Change Key on Emoji Delete: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
            }
        });
        eb.addField("Deleted Emoji ID", "╰┈➤" + ale.getTargetId(), false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        performChecksThenBuildAndSendEmbed(event, eb);
    }
}
