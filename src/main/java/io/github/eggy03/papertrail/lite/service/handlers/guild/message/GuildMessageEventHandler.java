package io.github.eggy03.papertrail.lite.service.handlers.guild.message;

import com.google.common.base.Splitter;
import io.github.eggy03.papertrail.lite.configuration.PaperTrailConfig;
import io.github.eggy03.papertrail.lite.entity.guild.GuildMessage;
import io.github.eggy03.papertrail.lite.repository.guild.GuildMessageRepository;
import io.github.eggy03.papertrail.lite.service.EmbedSendingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
@Slf4j
public final class GuildMessageEventHandler {

    private final @NonNull PaperTrailConfig paperTrailConfig;
    private final @NonNull EmbedSendingService embedSendingService;
    private final @NonNull GuildMessageRepository repository;

    @Inject
    public GuildMessageEventHandler(
            @NonNull PaperTrailConfig paperTrailConfig,
            @NonNull EmbedSendingService embedSendingService,
            @NonNull GuildMessageRepository repository
    ) {
        this.paperTrailConfig = paperTrailConfig;
        this.embedSendingService = embedSendingService;
        this.repository = repository;
    }

    public void handleMessageReceivedEvent(@NonNull MessageReceivedEvent event) {

        if (paperTrailConfig.guild().message().globalLogChannel().equals("-1")) return;

        String messageId = event.getMessageId();
        String messageContent = event.getMessage().getContentDisplay();
        String authorId = event.getAuthor().getId();

        log.debug("Message To Save: [Content: {}, Author: {}]", messageContent, event.getAuthor().getName());
        repository.put(new GuildMessage(messageId, messageContent, authorId));
    }

    public void handleMessageUpdateEvent(@NonNull MessageUpdateEvent event) {

        // fetch the old message if present
        GuildMessage oldGuildMessage = repository.get(event.getMessageId());
        if (oldGuildMessage == null) {
            repository.put(new GuildMessage(event.getMessageId(), event.getMessage().getContentDisplay(), event.getAuthor().getId()));
            return;
        }

        // Fetch the old message content
        String oldMessageContent = oldGuildMessage.messageContent();
        // fetch the updated message content and its author from the event
        String updatedMessageContent = event.getMessage().getContentDisplay();
        String updatedMessageAuthor = event.getAuthor().getAsMention();

        // Ignore events where the message content wasn't edited (e.g., pin, embed resolve, thread creates and updates)
        // This is required since MessageUpdateEvent is triggered in case of pins and embed resolves with no change to content
        if (updatedMessageContent.equals(oldMessageContent))
            return;

        // Splitting is required because each field in an embed can display only up-to 1024 characters
        // A full embed can display up-to 6000 characters
        List<String> oldMessageContentSplits = Splitter.fixedLength(1000).splitToList(oldMessageContent);
        List<String> updatedMessageContentSplits = Splitter.fixedLength(1000).splitToList(updatedMessageContent);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message Edit Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Author: " + updatedMessageAuthor + "\n" + "Channel: " + event.getChannel().getAsMention()));
        eb.setColor(paperTrailConfig.embedColor().warningColor());

        oldMessageContentSplits.forEach(split -> eb.addField(MarkdownUtil.underline("Old Message"), MarkdownUtil.codeblock(split), false));
        updatedMessageContentSplits.forEach(split -> eb.addField(MarkdownUtil.underline("New Message"), MarkdownUtil.codeblock(split), false));

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        // update the repository with the new message
        repository.put(new GuildMessage(oldGuildMessage.messageId(), updatedMessageContent, event.getAuthor().getId()));

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().message().globalLogChannel());
    }

    public void handleMessageDeleteEvent(@NonNull MessageDeleteEvent event) {

        // fetch the old message if present
        GuildMessage oldGuildMessage = repository.get(event.getMessageId());
        if (oldGuildMessage == null)
            return;

        // Fetch the deleted message and it's author id
        String deletedMessage = oldGuildMessage.messageContent();
        String deletedMessageAuthorId = oldGuildMessage.authorId();

        User author = event.getJDA().getUserById(deletedMessageAuthorId);
        String mentionableAuthor = author != null ? author.getAsMention() : deletedMessageAuthorId;

        // Splitting is required because each field in an embed can display only up-to 1024 characters
        List<String> deletedMessageSplits = Splitter.fixedLength(1000).splitToList(deletedMessage);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Author: " + mentionableAuthor + "\n" + "Channel: " + event.getChannel().getAsMention()));
        eb.setColor(paperTrailConfig.embedColor().destructiveColor());

        deletedMessageSplits.forEach(split -> eb.addField(MarkdownUtil.underline("Deleted Message"), MarkdownUtil.codeblock(split), false));

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        // delete the message from the repository
        log.debug("Message To Delete: [Content: {}, Author: {}]", deletedMessage, author != null ? author.getName() : deletedMessageAuthorId);
        repository.delete(event.getMessageId());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().message().globalLogChannel());
    }
}
