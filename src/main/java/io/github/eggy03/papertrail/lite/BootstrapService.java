package io.github.eggy03.papertrail.lite;

import io.github.eggy03.papertrail.lite.configuration.PaperTrailConfig;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Slf4j
@ApplicationScoped
@Startup
public final class BootstrapService {

    private final @NonNull PaperTrailConfig paperTrailConfig;
    private final @NonNull Instance<ListenerAdapter> listeners;
    private final @NonNull JDA jda;

    @Inject
    public BootstrapService(@NonNull Instance<ListenerAdapter> listeners, @NonNull PaperTrailConfig paperTrailConfig) {
        this.listeners = listeners;
        this.paperTrailConfig = paperTrailConfig;
        this.jda = constructJDA();
    }

    @NonNull
    JDA constructJDA() {

        JDABuilder builder = JDABuilder.createDefault(paperTrailConfig.general().appDiscordToken());

        builder.enableIntents(GatewayIntent.SCHEDULED_EVENTS,
                GatewayIntent.AUTO_MODERATION_EXECUTION,
                GatewayIntent.AUTO_MODERATION_CONFIGURATION,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EXPRESSIONS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MODERATION
        );

        //cache all members
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        // cache all activities
        builder.enableCache(CacheFlag.ACTIVITY,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.EMOJI,
                CacheFlag.STICKER,
                CacheFlag.ONLINE_STATUS,
                CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.ROLE_TAGS,
                CacheFlag.SCHEDULED_EVENTS
        );
        // chunk all guilds
        builder.setChunkingFilter(ChunkingFilter.ALL);

        // set status
        builder.setStatus(OnlineStatus.ONLINE);

        // set activity
        builder.setActivity(Activity.customStatus(paperTrailConfig.general().appActivity()));

        // add listeners
        listeners.forEach(listener -> {
            log.debug("Registering Listener: {}", listener.getClass().getSimpleName());
            builder.addEventListeners(listener);
        });

        // build shard manager and login
        return builder.build();
    }

    @Produces
    @ApplicationScoped
    @NonNull
    JDA getJda() {
        return jda;
    }

    @PreDestroy
    void shutdown() {
        jda.shutdown();
    }
}
