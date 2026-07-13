package io.github.eggy03.papertrail.lite.service.handlers.guild;

import io.github.eggy03.papertrail.lite.utils.DurationUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentActions;
import net.dv8tion.jda.api.entities.guild.SecurityIncidentDetections;
import net.dv8tion.jda.api.events.guild.update.GenericGuildUpdateEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentActionsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentDetectionsEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;

@ApplicationScoped
@Slf4j
public final class GuildSecurityIncidentEventHandler {

    private final @NonNull String guildSecurityIncidentEventChannel;

    @Inject
    public GuildSecurityIncidentEventHandler(@ConfigProperty(name = "guild.security.incident.event.log.channel") @NonNull String guildSecurityIncidentEventChannel) {
        this.guildSecurityIncidentEventChannel = guildSecurityIncidentEventChannel;
    }

    
    private void performChecksThenBuildAndSendEmbed(@NonNull GenericGuildUpdateEvent<?> event, @NonNull EmbedBuilder embedBuilder) {

        if (guildSecurityIncidentEventChannel.equals("-1")) return;

        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(guildSecurityIncidentEventChannel);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    public void handleGuildUpdateSecurityIncidentDetections(@NonNull GuildUpdateSecurityIncidentDetectionsEvent event) {
       
        SecurityIncidentDetections oldSID = event.getOldSecurityIncidentDetections();
        SecurityIncidentDetections newSID = event.getNewSecurityIncidentDetections();

        OffsetDateTime oldDMSpamDetectedAt = oldSID.getTimeDetectedDmSpam();
        OffsetDateTime newDMSpamDetectedAt = newSID.getTimeDetectedDmSpam();

        OffsetDateTime oldRaidDetectedAt = oldSID.getTimeDetectedRaid();
        OffsetDateTime newRaidDetectedAt = newSID.getTimeDetectedRaid();

        if (oldDMSpamDetectedAt != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Security Incident Resolved");
            eb.setDescription(MarkdownUtil.quoteBlock("Incident Type: DM Spam\nStatus: Resolved"));
            eb.setColor(Color.GREEN);
            eb.addField(MarkdownUtil.underline("DM Spam Started At"), DurationUtils.isoToLocalTimeCounter(oldDMSpamDetectedAt), false);
            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            performChecksThenBuildAndSendEmbed(event, eb);
        }

        if (newDMSpamDetectedAt != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Security Incident Detected");
            eb.setDescription(MarkdownUtil.quoteBlock("Incident Type: DM Spam\nStatus: Detected"));
            eb.setColor(Color.ORANGE);
            eb.addField(MarkdownUtil.underline("DM Spam Detected At"), DurationUtils.isoToLocalTimeCounter(newDMSpamDetectedAt), false);
            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            performChecksThenBuildAndSendEmbed(event, eb);
        }

        if (oldRaidDetectedAt != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Security Incident Resolved");
            eb.setDescription(MarkdownUtil.quoteBlock("Incident Type: RAID\nStatus: Resolved"));
            eb.setColor(Color.GREEN);
            eb.addField(MarkdownUtil.underline("Raid Was Detected At"), DurationUtils.isoToLocalTimeCounter(oldRaidDetectedAt), false);
            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            performChecksThenBuildAndSendEmbed(event, eb);
        }

        if (newRaidDetectedAt != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Security Incident Detected");
            eb.setDescription(MarkdownUtil.quoteBlock("Incident Type: RAID\nStatus: Detected"));
            eb.setColor(Color.ORANGE);
            eb.addField(MarkdownUtil.underline("Raid Detected At"), DurationUtils.isoToLocalTimeCounter(newRaidDetectedAt), false);
            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            performChecksThenBuildAndSendEmbed(event, eb);
        }
    }

    public void handleGuildUpdateSecurityIncidentActions(@NonNull GuildUpdateSecurityIncidentActionsEvent event) {
       
        SecurityIncidentActions oldSIA = event.getOldSecurityIncidentActions();
        SecurityIncidentActions newSIA = event.getNewSecurityIncidentActions();

        OffsetDateTime oldDMDisabledUntil = oldSIA.getDirectMessagesDisabledUntil();
        OffsetDateTime newDMDisabledUntil = newSIA.getDirectMessagesDisabledUntil();

        OffsetDateTime oldInvitesPausedUntil = oldSIA.getInvitesDisabledUntil();
        OffsetDateTime newInvitesPausedUntil = newSIA.getInvitesDisabledUntil();

        if (oldDMDisabledUntil != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Security Action Disabled");
            eb.setDescription(MarkdownUtil.quoteBlock("Action Type: DM Pause\nStatus: Disabled"));
            eb.setColor(Color.GREEN);
            eb.addField(MarkdownUtil.underline("DMs Were Paused Until"), DurationUtils.isoToLocalTimeCounter(oldDMDisabledUntil), false);
            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            performChecksThenBuildAndSendEmbed(event, eb);
        }

        if (newDMDisabledUntil != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Security Action Enabled");
            eb.setDescription(MarkdownUtil.quoteBlock("Action Type: DM Pause\nStatus: Enabled"));
            eb.setColor(Color.ORANGE);
            eb.addField(MarkdownUtil.underline("DMs Paused Until"), DurationUtils.isoToLocalTimeCounter(newDMDisabledUntil), false);
            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            performChecksThenBuildAndSendEmbed(event, eb);
        }

        if (oldInvitesPausedUntil != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Security Action Disabled");
            eb.setDescription(MarkdownUtil.quoteBlock("Action Type: Invite Pause\nStatus: Disabled"));
            eb.setColor(Color.GREEN);
            eb.addField(MarkdownUtil.underline("Invites Were Paused Until"), DurationUtils.isoToLocalTimeCounter(oldInvitesPausedUntil), false);
            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            performChecksThenBuildAndSendEmbed(event, eb);
        }

        if (newInvitesPausedUntil != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Audit Log Entry | Security Action Enabled");
            eb.setDescription(MarkdownUtil.quoteBlock("Action Type: Invite Pause\nStatus: Enabled"));
            eb.setColor(Color.ORANGE);
            eb.addField(MarkdownUtil.underline("Invites Paused Until"), DurationUtils.isoToLocalTimeCounter(newInvitesPausedUntil), false);
            eb.setFooter(event.getGuild().getName());
            eb.setTimestamp(Instant.now());

            performChecksThenBuildAndSendEmbed(event, eb);
        }
    }
}
