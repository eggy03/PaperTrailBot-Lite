package io.github.eggy03.papertrail.lite.service.handlers.auditlog;

import io.github.eggy03.papertrail.lite.service.EmbedSendingService;
import io.github.eggy03.papertrail.lite.utils.BooleanUtils;
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
public final class OnboardingPromptActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull String onboardingPromptActionLogChannel;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public OnboardingPromptActionTypeHandler(@ConfigProperty(name = "onboarding.prompt.action.log.channel") @NonNull String onboardingPromptActionLogChannel, @NonNull EmbedSendingService embedSendingService) {
        this.onboardingPromptActionLogChannel = onboardingPromptActionLogChannel;
        this.embedSendingService = embedSendingService;
    }

    @Override
    public void onOnboardingPromptCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Onboarding Prompt Create Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Onboarding Prompt Created By: " + mentionableExecutor));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "single_select" ->
                        eb.addField(MarkdownUtil.underline("Single Selection Mode"), BooleanUtils.formatToYesOrNo(newValue), false);

                case "required" ->
                        eb.addField(MarkdownUtil.underline("Is Required"), BooleanUtils.formatToYesOrNo(newValue), false);

                case "type", "id" -> {
                    // skip
                }

                case "title" -> eb.addField(MarkdownUtil.underline("Question Title"), String.valueOf(newValue), false);

                case "options" ->
                        eb.addField(MarkdownUtil.underline("Question Options"), "Review the options in Onboarding Settings", false);

                case "in_onboarding" ->
                        eb.addField(MarkdownUtil.underline("Is a Pre-Join Question"), BooleanUtils.formatToYesOrNo(newValue), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Onboarding Prompt Create: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }

        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, onboardingPromptActionLogChannel);
    }

    @Override
    public void onOnboardingPromptUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Onboarding Prompt Update Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Onboarding Prompt Updated By: " + mentionableExecutor));
        eb.setColor(Color.YELLOW);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {
                case "single_select" -> {
                    eb.addField(MarkdownUtil.underline("Old Single Selection Mode"), BooleanUtils.formatToYesOrNo(oldValue), false);
                    eb.addField(MarkdownUtil.underline("New Single Selection Mode"), BooleanUtils.formatToYesOrNo(newValue), false);
                }

                case "required" -> {
                    eb.addField(MarkdownUtil.underline("Was Required"), BooleanUtils.formatToYesOrNo(oldValue), false);
                    eb.addField(MarkdownUtil.underline("Is Required"), BooleanUtils.formatToYesOrNo(newValue), false);
                }

                case "type", "id" -> {
                    // skip
                }

                case "title" -> {
                    eb.addField(MarkdownUtil.underline("Old Question Title"), String.valueOf(oldValue), false);
                    eb.addField(MarkdownUtil.underline("New Question Title"), String.valueOf(newValue), false);
                }

                case "options" ->
                        eb.addField(MarkdownUtil.underline("Question Options"), "Review the changed options in Onboarding Settings", false);

                case "in_onboarding" -> {
                    eb.addField(MarkdownUtil.underline("Was a Pre-Join Question"), BooleanUtils.formatToYesOrNo(oldValue), false);
                    eb.addField(MarkdownUtil.underline("Is a Pre-Join Question"), BooleanUtils.formatToYesOrNo(newValue), false);
                }

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Onboarding Prompt Update: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, onboardingPromptActionLogChannel);
    }

    @Override
    public void onOnboardingPromptDelete(@NonNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Onboarding Prompt Delete Event");

        User executor = ale.getJDA().getUserById(ale.getUserId());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Onboarding Prompt Deleted By: " + mentionableExecutor));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "single_select" ->
                        eb.addField(MarkdownUtil.underline("Single Selection Mode"), BooleanUtils.formatToYesOrNo(oldValue), false);

                case "required" ->
                        eb.addField(MarkdownUtil.underline("Was Required"), BooleanUtils.formatToYesOrNo(oldValue), false);

                case "type", "id", "options" -> {
                    // skip
                }
                case "title" -> eb.addField(MarkdownUtil.underline("Question Title"), String.valueOf(oldValue), false);

                case "in_onboarding" ->
                        eb.addField(MarkdownUtil.underline("Was a Pre-Join Question"), BooleanUtils.formatToYesOrNo(oldValue), false);

                default -> {
                    eb.addField("Unimplemented Change Key", changeKey, false);
                    log.info("Unimplemented Change Key on Onboarding Prompt Delete: {}\nOLD_VALUE: {}\nNEW_VALUE: {}", changeKey, oldValue, newValue);
                }
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedSendingService.checkAndSend(event, eb, onboardingPromptActionLogChannel);
    }
}
