package io.github.eggy03.papertrail.lite.service.handlers.guild.auditlog;

import io.github.eggy03.papertrail.lite.configuration.PaperTrailConfig;
import io.github.eggy03.papertrail.lite.service.EmbedSendingService;
import io.github.eggy03.papertrail.lite.utils.guild.auditlog.SoundboardUtils;
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
public final class SoundboardActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull PaperTrailConfig paperTrailConfig;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public SoundboardActionTypeHandler(@NonNull PaperTrailConfig paperTrailConfig, @NonNull EmbedSendingService embedSendingService) {
        this.paperTrailConfig = paperTrailConfig;
        this.embedSendingService = embedSendingService;
    }

    @Override
    public void onSoundboardSoundCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Soundboard Sound Create Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Sound Item Added By: " + mentionableExecutor));
        eb.setColor(paperTrailConfig.embedColor().successColor());

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "user_id", "sound_id", "id", "guild_id", "available" -> {
                    // skip
                }
                case "volume" ->
                        eb.addField(MarkdownUtil.underline("Volume"), "╰┈➤" + SoundboardUtils.resolveVolumePercentage(newValue), false);

                case "emoji_name" -> eb.addField(MarkdownUtil.underline("Related Emoji"), "╰┈➤" + newValue, false);

                case "emoji_id" -> eb.addField(MarkdownUtil.underline("Related Emoji ID"), "╰┈➤" + newValue, false);

                case "name" -> eb.addField(MarkdownUtil.underline("Sound Item Name"), "╰┈➤" + newValue, false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Soundboard Sound Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().soundboardEvent().logChannel());
    }

    @Override
    public void onSoundboardSoundUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Soundboard Sound Update Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Sound Item Updated By: " + mentionableExecutor));
        eb.setColor(paperTrailConfig.embedColor().warningColor());

        ale.getChanges().forEach((changeKey, changeValue) -> {
            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "user_id", "sound_id", "id", "guild_id", "available" -> {
                    // skip
                }
                case "volume" -> {
                    eb.addField(MarkdownUtil.underline("Old Volume"), "╰┈➤" + SoundboardUtils.resolveVolumePercentage(oldValue), true);
                    eb.addField(MarkdownUtil.underline("New Volume"), "╰┈➤" + SoundboardUtils.resolveVolumePercentage(newValue), true);
                    eb.addBlankField(true);
                }
                case "emoji_name" -> {
                    eb.addField(MarkdownUtil.underline("Old Related Emoji"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Related Emoji"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "emoji_id" -> {
                    eb.addField(MarkdownUtil.underline("Old Related Emoji ID"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Related Emoji ID"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                case "name" -> {
                    eb.addField(MarkdownUtil.underline("Old Sound Item Name"), "╰┈➤" + oldValue, true);
                    eb.addField(MarkdownUtil.underline("New Sound Item Name"), "╰┈➤" + newValue, true);
                    eb.addBlankField(true);
                }
                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Soundboard Sound Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().soundboardEvent().logChannel());
    }

    @Override
    public void onSoundboardSoundDelete(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Soundboard Sound Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Sound Item Deleted By: " + mentionableExecutor));
        eb.setColor(paperTrailConfig.embedColor().destructiveColor());

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "user_id", "sound_id", "id", "guild_id", "available" -> {
                    // skip
                }
                case "volume" ->
                        eb.addField(MarkdownUtil.underline("Volume"), "╰┈➤" + SoundboardUtils.resolveVolumePercentage(oldValue), false);

                case "emoji_name" -> eb.addField(MarkdownUtil.underline("Related Emoji"), "╰┈➤" + oldValue, false);

                case "emoji_id" -> eb.addField(MarkdownUtil.underline("Related Emoji ID"), "╰┈➤" + oldValue, false);

                case "name" -> eb.addField(MarkdownUtil.underline("Sound Item Name"), "╰┈➤" + oldValue, false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Soundboard Sound Delete: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().soundboardEvent().logChannel());
    }
}
