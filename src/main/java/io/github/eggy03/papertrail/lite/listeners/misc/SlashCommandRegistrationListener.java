package io.github.eggy03.papertrail.lite.listeners.misc;

import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/*
 * Registers all the slash commands places throughout the code on startup
 */
@Singleton
public final class SlashCommandRegistrationListener extends ListenerAdapter {

    @Override
    public void onReady(@NonNull ReadyEvent event) {
        setAuditLogCommands(event.getJDA());
    }

    private void setAuditLogCommands(@NonNull JDA jda) {

        CommandData serverStats = Commands
                .slash("stats", "Provides Server Statistics")
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);

        CommandData debug = Commands
                .slash("debug", "Provides standard debug info for troubleshooting")
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED);

        jda.updateCommands()
                .addCommands(serverStats, debug)
                .queue();
    }
}
