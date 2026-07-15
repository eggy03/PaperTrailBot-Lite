package io.github.eggy03.papertrail.lite.configuration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import lombok.NonNull;

@ConfigMapping(prefix = "papertrail")
public interface PaperTrailConfig {

    General general();
    EmbedColor embedColor();
    Guild guild();

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

    interface EmbedColor {

        @WithName("success.color.integer")
        int successColor();

        @WithName("warning.color.integer")
        int warningColor();

        @WithName("destructive.color.integer")
        int destructiveColor();
    }

    interface Guild {

        @NonNull AuditLog auditLog();
        @NonNull Message message();

        interface AuditLog {

            // Global Audit Log Channel
            @WithName("global.log.channel")
            @NonNull
            String globalLogChannel();

            // Audit Log Channel Overrides
            @WithName("automod.action.type.log.channel")
            @NonNull
            String automodActionTypeLogChannel();

            @WithName("channel.action.type.log.channel")
            @NonNull
            String channelActionTypeLogChannel();

            @WithName("channel.override.action.type.log.channel")
            @NonNull
            String channelOverrideActionTypeLogChannel();

            @WithName("emoji.action.type.log.channel")
            @NonNull
            String emojiActionTypeLogChannel();

            @WithName("guild.update.action.type.log.channel")
            @NonNull
            String guildUpdateActionTypeLogChannel();

            @WithName("home.settings.action.type.log.channel")
            @NonNull
            String homeSettingsActionTypeLogChannel();

            @WithName("integration.action.type.log.channel")
            @NonNull
            String integrationActionTypeLogChannel();

            @WithName("invite.action.type.log.channel")
            @NonNull
            String inviteActionTypeLogChannel();

            @WithName("member.update.action.type.log.channel")
            @NonNull
            String memberUpdateActionTypeLogChannel();

            @WithName("message.action.type.log.channel")
            @NonNull
            String messageActionTypeLogChannel();

            @WithName("message.pin.action.type.log.channel")
            @NonNull
            String messagePinActionTypeLogChannel();

            @WithName("mod.action.type.log.channel")
            @NonNull
            String modActionActionTypeLogChannel();

            @WithName("onboarding.action.type.log.channel")
            @NonNull
            String onboardingActionTypeLogChannel();

            @WithName("onboarding.prompt.action.type.log.channel")
            @NonNull
            String onboardingPromptActionTypeLogChannel();

            @WithName("role.action.type.log.channel")
            @NonNull
            String roleActionTypeLogChannel();

            @WithName("scheduled.event.action.type.log.channel")
            @NonNull
            String scheduledEventActionTypeLogChannel();

            @WithName("soundboard.action.type.log.channel")
            @NonNull
            String soundboardActionTypeLogChannel();

            @WithName("stage.instance.action.type.log.channel")
            @NonNull
            String stageInstanceActionTypeLogChannel();

            @WithName("sticker.action.type.log.channel")
            @NonNull
            String stickerActionTypeLogChannel();

            @WithName("thread.action.type.log.channel")
            @NonNull
            String threadActionTypeLogChannel();

            @WithName("unknown.action.type.log.channel")
            @NonNull
            String unknownActionTypeLogChannel();

            @WithName("voice.channel.status.action.type.log.channel")
            @NonNull
            String voiceChannelStatusActionTypeLogChannel();

            @WithName("webhook.action.type.log.channel")
            @NonNull
            String webhookActionTypeLogChannel();
        }

        interface Message {
            @WithName("global.log.channel")
            @NonNull
            String globalLogChannel();

            @WithName("retention.days")
            long guildMessageRetentionDays();
        }

        // guild
        @WithName("boost.event.log.channel")
        @NonNull
        String boostEventLogChannel();

        @WithName("member.event.log.channel")
        @NonNull
        String memberEventLogChannel();

        @WithName("poll.event.log.channel")
        @NonNull
        String pollEventLogChannel();

        @WithName("security.incident.event.log.channel")
        @NonNull
        String securityIncidentEventLogChannel();

        @WithName("voice.event.log.channel")
        @NonNull
        String voiceEventLogChannel();
    }
}