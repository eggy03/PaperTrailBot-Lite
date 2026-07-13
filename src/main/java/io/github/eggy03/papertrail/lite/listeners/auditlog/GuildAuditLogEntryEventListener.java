package io.github.eggy03.papertrail.lite.listeners.auditlog;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listener responsible for receiving {@link GuildAuditLogEntryCreateEvent}
 * events from JDA and delegating them to all registered
 * {@link AbstractGuildAuditLogEntryCreateEventActionTypeHandler} CDI beans.
 *
 * <p>
 * Event handlers are resolved dynamically using
 * {@code Instance<AbstractGuildAuditLogEntryCreateEventActionTypeHandler>}, allowing multiple
 * independent handler implementations to process the same audit log event.
 * </p>
 *
 * <p>
 * Each discovered handler instance will receive the event through
 * {@link AbstractGuildAuditLogEntryCreateEventActionTypeHandler#handleActionType(GuildAuditLogEntryCreateEvent)}, where
 * it will process the event.
 * </p>
 */
@Slf4j
@Singleton
public final class GuildAuditLogEntryEventListener extends ListenerAdapter {

    private final @NonNull Instance<AbstractGuildAuditLogEntryCreateEventActionTypeHandler> handlerInstances;

    @Inject
    public GuildAuditLogEntryEventListener(@NonNull Instance<AbstractGuildAuditLogEntryCreateEventActionTypeHandler> handlerInstances) {
        this.handlerInstances = handlerInstances;
    }

    @Override
    public void onGuildAuditLogEntryCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        log.debug("Received [Event=GuildAuditLogEntryCreate, ActionType={}] for [Guild={}, ID={}]",
                event.getEntry().getType(), event.getGuild().getName(), event.getGuild().getId()
        );

        handlerInstances.forEach(handler -> handler.handleActionType(event));
    }

}