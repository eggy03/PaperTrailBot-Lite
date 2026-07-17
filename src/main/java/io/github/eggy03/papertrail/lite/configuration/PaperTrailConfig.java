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

        AutomodEvent automodEvent();
        ChannelEvent channelEvent();
        EmojiEvent emojiEvent();
        GuildUpdateEvent guildUpdateEvent();
        HomeSettingsEvent homeSettingsEvent();
        IntegrationEvent integrationEvent();
        InviteEvent inviteEvent();
        MemberEvent memberEvent();
        ModActionEvent modActionEvent();
        OnboardingEvent onboardingEvent();
        RoleEvent roleEvent();
        ScheduledEventEvent scheduledEventEvent();
        StageInstanceEvent stageInstanceEvent();
        SoundboardEvent soundboardEvent();
        StickerEvent stickerEvent();
        ThreadEvent threadEvent();
        UnknownEvent unknownEvent();
        VoiceEvent voiceEvent();
        WebhookEvent webhookEvent();
        BoostEvent boostEvent();
        PollEvent pollEvent();
        SecurityIncidentEvent securityIncidentEvent();
        MessageEvent messageEvent();

        interface AutomodEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface ChannelEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface EmojiEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface GuildUpdateEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface HomeSettingsEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface IntegrationEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface InviteEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface MemberEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface ModActionEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface OnboardingEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface RoleEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface ScheduledEventEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface SoundboardEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface StageInstanceEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface StickerEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface ThreadEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface UnknownEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface VoiceEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface WebhookEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface BoostEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface PollEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface SecurityIncidentEvent {
            @WithName("log.channel")
            @NonNull String logChannel();
        }

        interface MessageEvent {

            @WithName("log.channel")
            @NonNull String logChannel();

            @WithName("retention.days")
            long retentionDays();
        }
    }
}