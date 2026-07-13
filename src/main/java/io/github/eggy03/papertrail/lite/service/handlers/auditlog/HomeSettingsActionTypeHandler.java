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

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class HomeSettingsActionTypeHandler extends GuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull String homeSettingsActionLogChannel;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public HomeSettingsActionTypeHandler(@ConfigProperty(name = "home.settings.action.log.channel") @NonNull String homeSettingsActionLogChannel, @NonNull EmbedSendingService embedSendingService) {
        this.homeSettingsActionLogChannel = homeSettingsActionLogChannel;
        this.embedSendingService = embedSendingService;
    }

    @Override
    public void onHomeSettingsCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Server Guide Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Server Guide Created By: " + mentionableExecutor));
        eb.setColor(Color.GREEN);

        eb.addField(
                MarkdownUtil.underline("More Info"),
                MarkdownUtil.codeblock("To view the created guide, either visit the Server Guide section or the Onboarding section of your guild."),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, homeSettingsActionLogChannel);
    }

    @Override
    public void onHomeSettingsUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Server Guide Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Server Guide Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().keySet().forEach(key -> {
            switch (key) {
                case "welcome_message" ->
                        eb.addField(MarkdownUtil.underline("Welcome Message"), "╰┈➤Welcome Message has been updated", false);
                case "resource_channels" ->
                        eb.addField(MarkdownUtil.underline("Resources"), "╰┈➤Resources have been updated", false);
                case "new_member_actions" ->
                        eb.addField(MarkdownUtil.underline("New Member Join To-Do"), "╰┈➤Interactive Actions have been updated", false);
                default -> eb.addField(MarkdownUtil.underline(key), "╰┈➤" + key + " has/have been updated", false);
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, homeSettingsActionLogChannel);
    }
}
