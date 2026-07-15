package io.github.eggy03.papertrail.lite.service.handlers.guild;

import io.github.eggy03.papertrail.lite.configuration.PaperTrailConfig;
import io.github.eggy03.papertrail.lite.service.EmbedSendingService;
import io.github.eggy03.papertrail.lite.utils.BooleanUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.time.Instant;

@ApplicationScoped
@Slf4j
public final class GuildPollEventHandler {

    private final @NonNull PaperTrailConfig paperTrailConfig;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public GuildPollEventHandler(@NonNull PaperTrailConfig paperTrailConfig, @NonNull EmbedSendingService embedSendingService) {
        this.paperTrailConfig = paperTrailConfig;
        this.embedSendingService = embedSendingService;
    }

    public void handlePollCreationEvent(@NonNull MessageReceivedEvent event) {
        // check if message has a poll
        MessagePoll messagePoll = event.getMessage().getPoll();
        if (messagePoll == null)
            return;


        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Poll Creation Event");
        eb.setColor(paperTrailConfig.embedColor().successColor());
        eb.setDescription(MarkdownUtil.quoteBlock("Poll Created By: " + event.getAuthor().getAsMention() + "\nTarget Channel: " + event.getChannel().getAsMention()));

        eb.addField(MarkdownUtil.underline("Question"), messagePoll.getQuestion().getText(), false);
        eb.addField(MarkdownUtil.underline("Answers"), getMessagePollAnswers(messagePoll), false);
        eb.addField(MarkdownUtil.underline("Poll Expiry Time"), getPollExpiryTime(messagePoll), false);
        eb.addField(MarkdownUtil.underline("Accepts Multiple Answers"), BooleanUtils.formatToYesOrNo(messagePoll.isMultiAnswer()), false);

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().pollEventLogChannel());

    }

    @NonNull
    private String getMessagePollAnswers(@NonNull MessagePoll messagePoll) {

        StringBuilder answers = new StringBuilder();
        messagePoll.getAnswers().forEach(answer -> answers
                .append("*Answer: * ")
                .append(answer.getText())
                .append(" *Emoji: * ")
                .append(answer.getEmoji() == null ? "N/A" : answer.getEmoji().getFormatted())
                .append(System.lineSeparator())
        );

        return answers.toString().trim();
    }

    @NonNull
    private String getPollExpiryTime(@NonNull MessagePoll messagePoll) {
        return messagePoll.getTimeExpiresAt() != null ?
                TimeFormat.DATE_TIME_LONG.format(messagePoll.getTimeExpiresAt()) :
                "Never Expires";
    }
}
