package io.github.eggy03.papertrail.lite.service.handlers.guild.auditlog;

import io.github.eggy03.papertrail.lite.configuration.PaperTrailConfig;
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

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class EmojiActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull PaperTrailConfig paperTrailConfig;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public EmojiActionTypeHandler(@NonNull PaperTrailConfig paperTrailConfig, @NonNull EmbedSendingService embedSendingService) {
        this.paperTrailConfig = paperTrailConfig;
        this.embedSendingService = embedSendingService;
    }

    @Override
    public void onEmojiCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Emoji Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Emoji Created By: " + mentionableExecutor));
        eb.setColor(paperTrailConfig.embedColor().successColor());

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

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().auditLog().emojiActionTypeLogChannel());
    }

    @Override
    public void onEmojiUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());


        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Emoji Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Emoji Updated By: " + mentionableExecutor));
        eb.setColor(paperTrailConfig.embedColor().warningColor());

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

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().auditLog().emojiActionTypeLogChannel());
    }

    @Override
    public void onEmojiDelete(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Emoji Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Emoji Deleted By: " + mentionableExecutor));
        eb.setColor(paperTrailConfig.embedColor().destructiveColor());

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

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().auditLog().emojiActionTypeLogChannel());
    }
}
