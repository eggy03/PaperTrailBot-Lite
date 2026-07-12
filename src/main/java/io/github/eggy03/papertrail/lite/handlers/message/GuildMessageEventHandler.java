package io.github.eggy03.papertrail.lite.handlers.message;

import com.google.common.base.Splitter;
import io.github.eggy03.papertrail.lite.entity.CachedMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.awt.Color;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
@Slf4j
public final class GuildMessageEventHandler {

    private final @NonNull String messageLogChannel;
    private final @NonNull CaffeineMessageCacheService messageCacheService;

    @Inject
    public GuildMessageEventHandler(
            @ConfigProperty(name = "global.message.log.channel") @NonNull String messageLogChannel,
            @NonNull CaffeineMessageCacheService messageCacheService)
    {
        this.messageLogChannel = messageLogChannel;
        this.messageCacheService = messageCacheService;
    }


    private void performChecksThenBuildAndSendEmbed(@NonNull GenericMessageEvent event, @NonNull EmbedBuilder embedBuilder) {

        if(messageLogChannel.equals("-1")) return;

        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Message Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(messageLogChannel);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    public void handleMessageReceivedEvent(@NonNull MessageReceivedEvent event) {
        if(messageLogChannel.equals("-1")) return;

        String messageId = event.getMessageId();
        String messageContent = event.getMessage().getContentDisplay();
        String authorId = event.getAuthor().getId();

        messageCacheService.put(new CachedMessage(messageId, messageContent, authorId));
    }

    public void handleMessageUpdateEvent(@NonNull MessageUpdateEvent event) {

        // fetch the old cached message if present
        CachedMessage oldCachedMessage = messageCacheService.get(event.getMessageId());
        if (oldCachedMessage == null) {
            messageCacheService.put(new CachedMessage(event.getMessageId(), event.getMessage().getContentDisplay(), event.getAuthor().getId()));
            return;
        }

        // Fetch the old message content
        String oldMessageContent = oldCachedMessage.messageContent();
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
        eb.setColor(Color.YELLOW);

        oldMessageContentSplits.forEach(split -> eb.addField(MarkdownUtil.underline("Old Message"), MarkdownUtil.codeblock(split), false));
        updatedMessageContentSplits.forEach(split -> eb.addField(MarkdownUtil.underline("New Message"), MarkdownUtil.codeblock(split), false));

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        // update the cache with the new message
        messageCacheService.put(new CachedMessage(oldCachedMessage.messageId(), updatedMessageContent, event.getAuthor().getId()));

        performChecksThenBuildAndSendEmbed(event, eb);
    }

    public void handleMessageDeleteEvent(@NonNull MessageDeleteEvent event) {

        // fetch the old cached message if present
        CachedMessage oldCachedMessage = messageCacheService.get(event.getMessageId());
        if (oldCachedMessage == null)
            return;

        // Fetch the deleted message and it's author id
        String deletedMessage = oldCachedMessage.messageContent();
        String deletedMessageAuthorId = oldCachedMessage.authorId();

        User author = event.getJDA().getUserById(deletedMessageAuthorId);
        String mentionableAuthor = (author != null ? author.getAsMention() : deletedMessageAuthorId);

        // Splitting is required because each field in an embed can display only up-to 1024 characters
        List<String> deletedMessageSplits = Splitter.fixedLength(1000).splitToList(deletedMessage);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Message Delete Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Author: " + mentionableAuthor + "\n" + "Channel: " + event.getChannel().getAsMention()));
        eb.setColor(Color.RED);

        deletedMessageSplits.forEach(split -> eb.addField(MarkdownUtil.underline("Deleted Message"), MarkdownUtil.codeblock(split), false));

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        // delete the message from the database
        messageCacheService.delete(event.getMessageId());

        performChecksThenBuildAndSendEmbed(event, eb);
    }
}
