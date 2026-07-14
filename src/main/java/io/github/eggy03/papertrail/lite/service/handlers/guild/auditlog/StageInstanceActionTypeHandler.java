package io.github.eggy03.papertrail.lite.service.handlers.guild.auditlog;

import io.github.eggy03.papertrail.lite.configuration.PaperTrailConfig;
import io.github.eggy03.papertrail.lite.service.EmbedSendingService;
import io.github.eggy03.papertrail.lite.utils.guild.auditlog.StageUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class StageInstanceActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull PaperTrailConfig paperTrailConfig;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public StageInstanceActionTypeHandler(@NonNull PaperTrailConfig paperTrailConfig, @NonNull EmbedSendingService embedSendingService) {
        this.paperTrailConfig = paperTrailConfig;
        this.embedSendingService = embedSendingService;
    }

    @Override
    public void onStageInstanceCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Stage Instance Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Stage Instance Created By: " + mentionableExecutor));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "topic" -> eb.addField(MarkdownUtil.underline("Stage Topic"), "╰┈➤" + newValue, false);

                case "privacy_level" ->
                        eb.addField(MarkdownUtil.underline("Stage Privacy"), "╰┈➤" + StageUtils.resolveStagePrivacyLevel(newValue), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Stage Instance Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.auditLog().stageInstanceActionLogChannel());
    }

    @Override
    public void onStageInstanceUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Stage Instance Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Stage Instance Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "topic" -> {
                    eb.addField(MarkdownUtil.underline("Old Stage Topic"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Stage Topic"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "privacy_level" -> {
                    eb.addField(MarkdownUtil.underline("Old Stage Privacy"), "╰┈➤" + StageUtils.resolveStagePrivacyLevel(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Stage Privacy"), "╰┈➤" + StageUtils.resolveStagePrivacyLevel(newValue), true);
                    eb.addBlankField(true);
                }

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Stage Instance Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.auditLog().stageInstanceActionLogChannel());
    }

    @Override
    public void onStageInstanceDelete(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Stage Instance Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Stage Instance Deleted By: " + mentionableExecutor));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "topic" -> eb.addField(MarkdownUtil.underline("Stage Topic"), "╰┈➤" + oldValue, false);

                case "privacy_level" ->
                        eb.addField(MarkdownUtil.underline("Stage Privacy"), "╰┈➤" + StageUtils.resolveStagePrivacyLevel(oldValue), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Stage Instance Delete: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });
        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.auditLog().stageInstanceActionLogChannel());
    }
}
