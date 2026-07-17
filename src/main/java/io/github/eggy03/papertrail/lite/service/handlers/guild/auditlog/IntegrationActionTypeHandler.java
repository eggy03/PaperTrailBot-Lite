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
public final class IntegrationActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull PaperTrailConfig paperTrailConfig;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public IntegrationActionTypeHandler(@NonNull PaperTrailConfig paperTrailConfig, @NonNull EmbedSendingService embedSendingService) {
        this.paperTrailConfig = paperTrailConfig;
        this.embedSendingService = embedSendingService;
    }

    @Override
    public void onIntegrationCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Integration Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Integration Created By: " + mentionableExecutor));
        eb.setColor(paperTrailConfig.embedColor().successColor());

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "type" -> eb.addField(MarkdownUtil.underline("Integration Type"), "╰┈➤" + newValue, false);
                case "name" -> eb.addField(MarkdownUtil.underline("Integration Name"), "╰┈➤" + newValue, false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Integration Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().integrationEvent().logChannel());
    }

    @Override
    public void onIntegrationUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {

        log.warn("Integration Update Event Detected. Implement this sometime later\n{}", event.getEntry().getChanges());


        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Integration Update Event");
        eb.setColor(paperTrailConfig.embedColor().warningColor());

        String implementationNotice = "We do not have sufficient data to log the changes in an INTEGRATION_UPDATE Event."
                .concat(" A proper implementation might happen in future releases if such an event is fired consistently.");

        eb.addField("Implementation Notice", MarkdownUtil.codeblock(implementationNotice), false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().integrationEvent().logChannel());
    }

    @Override
    public void onIntegrationDelete(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Integration Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Integration Deleted By: " + mentionableExecutor));
        eb.setColor(paperTrailConfig.embedColor().destructiveColor());

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "type" -> eb.addField(MarkdownUtil.underline("Integration Type"), "╰┈➤" + oldValue, false);
                case "name" -> eb.addField(MarkdownUtil.underline("Integration Name"), "╰┈➤" + oldValue, false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Integration Delete: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().integrationEvent().logChannel());
    }

    @Override
    public void onApplicationCommandPrivilegesUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Application Command Privilege Update");
        eb.setDescription(MarkdownUtil.quoteBlock("Application Command Privilege Updated By: " + mentionableExecutor));
        eb.setColor(paperTrailConfig.embedColor().warningColor());

        eb.addField(MarkdownUtil.underline("More Info"), MarkdownUtil.codeblock("To know more about what changes were made, visit the Integrations section"), false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().integrationEvent().logChannel());
    }
}
