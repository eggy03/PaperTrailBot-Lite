package io.github.eggy03.papertrail.lite.service.handlers.auditlog;

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
public final class ModActionActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull String modActionActionLogChannel;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public ModActionActionTypeHandler(@ConfigProperty(name = "mod.action.action.log.channel") @NonNull String modActionActionLogChannel, @NonNull EmbedSendingService embedSendingService) {
        this.modActionActionLogChannel = modActionActionLogChannel;
        this.embedSendingService = embedSendingService;
    }

    @Override
    public void onKick(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User moderator = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableModerator = (moderator != null ? moderator.getAsMention() : ale.getUserId());
        String reasonForKick = ale.getReason() == null ? "No Reason Provided" : ale.getReason();

        // A REST Action is required here because kicked members are not cached
        event.getJDA().retrieveUserById(ale.getTargetId()).queue(kickedUser -> {

            String mentionableKickedUser = kickedUser != null ? kickedUser.getAsMention() : ale.getTargetId();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Kick Event");
            eb.setDescription(MarkdownUtil.quoteBlock("A Member Has Been Kicked By: " + mentionableModerator));
            eb.setColor(Color.ORANGE);

            eb.addField(MarkdownUtil.underline("Kicked Member"), "╰┈➤" + mentionableKickedUser, false);
            eb.addField(MarkdownUtil.underline("Reason"), "╰┈➤" + reasonForKick, false);

            eb.setFooter("Audit Log Entry ID: " + ale.getId());
            eb.setTimestamp(ale.getTimeCreated());

            embedSendingService.checkAndSend(event, eb, modActionActionLogChannel);

        });
    }

    @Override
    public void onBan(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User moderator = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableModerator = (moderator != null ? moderator.getAsMention() : ale.getUserId());
        String reasonForBan = ale.getReason() == null ? "No Reason Provided" : ale.getReason();

        // A REST Action is required here because banned members are not cached
        event.getJDA().retrieveUserById(ale.getTargetId()).queue(bannedUser -> {

            String mentionableBannedUser = bannedUser != null ? bannedUser.getAsMention() : ale.getTargetId();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Ban Event");
            eb.setDescription(MarkdownUtil.quoteBlock("A Member Has Been Banned By: " + mentionableModerator));
            eb.setColor(Color.RED);

            eb.addField(MarkdownUtil.underline("Banned Member"), "╰┈➤" + mentionableBannedUser, false);
            eb.addField(MarkdownUtil.underline("Ban Reason"), "╰┈➤" + reasonForBan, false);

            eb.setFooter("Audit Log Entry ID: " + ale.getId());
            eb.setTimestamp(ale.getTimeCreated());

            embedSendingService.checkAndSend(event, eb, modActionActionLogChannel);
        });
    }

    @Override
    public void onUnban(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User moderator = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableModerator = (moderator != null ? moderator.getAsMention() : ale.getUserId());

        event.getJDA().retrieveUserById(ale.getTargetId()).queue(unbannedUser -> {

            String mentionableUnbannedUser = unbannedUser != null ? unbannedUser.getAsMention() : ale.getTargetId();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Member Unban Event");
            eb.setDescription(MarkdownUtil.quoteBlock("A Member Has Been Un-Banned By: " + mentionableModerator));
            eb.setColor(Color.GREEN);

            eb.addField(MarkdownUtil.underline("Un-banned User"), "╰┈➤" + mentionableUnbannedUser, false);

            eb.setFooter("Audit Log Entry ID: " + ale.getId());
            eb.setTimestamp(ale.getTimeCreated());

            embedSendingService.checkAndSend(event, eb, modActionActionLogChannel);
        });
    }

    @Override
    public void onBotAdd(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        User target = ale.getJDA().getUserById(ale.getTargetIdLong());
        String mentionableTarget = (target != null ? target.getAsMention() : ale.getTargetId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Bot Add Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Bot Added By: " + mentionableExecutor + "\nBot ID: " + ale.getTargetId()));
        eb.setColor(Color.CYAN);

        eb.addField(MarkdownUtil.underline("Bot Added"), "╰┈➤" + mentionableTarget, false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, modActionActionLogChannel);
    }

    @Override
    public void onPrune(@NonNull GuildAuditLogEntryCreateEvent event) {
        // I have never seen a prune event trigger yet
        log.warn("Prune Event Detected\n{}", event.getEntry().getChanges());


        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Prune Event");
        eb.setColor(Color.LIGHT_GRAY);

        String implementationNotice = "We do not have sufficient payload data to log the changes in a PRUNE Event."
                .concat(" A proper implementation might happen in future releases");

        eb.addField("Implementation Notice", MarkdownUtil.codeblock(implementationNotice), false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, modActionActionLogChannel);
    }
}
