package io.github.eggy03.papertrail.lite.configuration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import lombok.NonNull;

@ConfigMapping(prefix = "papertrail")
public interface PaperTrailConfig {

    General general();

    AuditLog auditLog();

    MessageLog messageLog();

    interface General {

        @WithName("app.name")
        @NonNull
        String appName();

        @WithName("app.version")
        @NonNull
        String appVersion();

        @WithName("app.activity")
        @NonNull
        String appActivity();

        @WithName("app.discord.token")
        @NonNull
        String appDiscordToken();
    }

    interface AuditLog {
        // Global Audit Log Channel
        @WithName("global.audit.log.channel")
        @NonNull
        String globalAuditLogChannel();

        // Audit Log Channel Overrides
        @WithName("automod.action.log.channel")
        @NonNull
        String automodActionLogChannel();

        @WithName("channel.action.log.channel")
        @NonNull
        String channelActionLogChannel();

        @WithName("channel.override.action.log.channel")
        @NonNull
        String channelOverrideActionLogChannel();

        @WithName("emoji.action.log.channel")
        @NonNull
        String emojiActionLogChannel();

        @WithName("guild.update.action.log.channel")
        @NonNull
        String guildUpdateActionLogChannel();

        @WithName("home.settings.action.log.channel")
        @NonNull
        String homeSettingsActionLogChannel();

        @WithName("integration.action.log.channel")
        @NonNull
        String integrationActionLogChannel();

        @WithName("invite.action.log.channel")
        @NonNull
        String inviteActionLogChannel();

        @WithName("member.update.action.log.channel")
        @NonNull
        String memberUpdateActionLogChannel();

        @WithName("message.action.log.channel")
        @NonNull
        String messageActionLogChannel();

        @WithName("message.pin.action.log.channel")
        @NonNull
        String messagePinActionLogChannel();

        @WithName("mod.action.action.log.channel")
        @NonNull
        String modActionActionLogChannel();

        @WithName("onboarding.action.log.channel")
        @NonNull
        String onboardingActionLogChannel();

        @WithName("onboarding.prompt.action.log.channel")
        @NonNull
        String onboardingPromptActionLogChannel();

        @WithName("role.action.log.channel")
        @NonNull
        String roleActionLogChannel();

        @WithName("scheduled.event.action.log.channel")
        @NonNull
        String scheduledEventActionLogChannel();

        @WithName("soundboard.action.log.channel")
        @NonNull
        String soundboardActionLogChannel();

        @WithName("stage.instance.action.log.channel")
        @NonNull
        String stageInstanceActionLogChannel();

        @WithName("sticker.action.log.channel")
        @NonNull
        String stickerActionLogChannel();

        @WithName("thread.action.log.channel")
        @NonNull
        String threadActionLogChannel();

        @WithName("unknown.action.log.channel")
        @NonNull
        String unknownActionLogChannel();

        @WithName("voice.channel.status.action.log.channel")
        @NonNull
        String voiceChannelStatusActionLogChannel();

        @WithName("webhook.action.log.channel")
        @NonNull
        String webhookActionLogChannel();

        // guild
        @WithName("guild.boost.event.log.channel")
        @NonNull
        String guildBoostEventLogChannel();

        @WithName("guild.member.event.log.channel")
        @NonNull
        String guildMemberEventLogChannel();

        @WithName("guild.poll.event.log.channel")
        @NonNull
        String guildPollEventLogChannel();

        @WithName("guild.security.incident.event.log.channel")
        @NonNull
        String guildSecurityIncidentEventLogChannel();

        @WithName("guild.voice.event.log.channel")
        @NonNull
        String guildVoiceEventLogChannel();
    }

    interface MessageLog {
        @WithName("global.message.log.channel")
        @NonNull
        String globalMessageLogChannel();

        @WithName("guild.message.retention.days")
        long guildMessageRetentionDays();
    }
}