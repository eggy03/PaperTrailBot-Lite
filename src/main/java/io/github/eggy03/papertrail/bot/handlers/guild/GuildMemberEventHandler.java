package io.github.eggy03.papertrail.bot.handlers.guild;

import io.github.eggy03.papertrail.bot.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.utils.DurationUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.awt.Color;
import java.time.Instant;

@ApplicationScoped
@Slf4j
public final class GuildMemberEventHandler {

    private final @NonNull String guildMemberEventLogChannel;

    @Inject
    public GuildMemberEventHandler(@ConfigProperty(name = "guild.member.event.log.channel") @NonNull String guildMemberEventLogChannel) {
        this.guildMemberEventLogChannel = guildMemberEventLogChannel;
    }

    
    private void performChecksThenBuildAndSendEmbed(@NonNull GenericGuildEvent event, @NonNull EmbedBuilder embedBuilder) {

        if (guildMemberEventLogChannel.equals("-1")) return;

        if (!embedBuilder.isValidLength() || embedBuilder.isEmpty()) {
            log.warn("Embed is empty or too long (current length: {}).", embedBuilder.length());
            return;
        }

        TextChannel sendingChannel = event.getGuild().getTextChannelById(guildMemberEventLogChannel);
        if (sendingChannel != null && sendingChannel.canTalk()) {
            sendingChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    public void handleGuildMemberJoin(@NonNull GuildMemberJoinEvent event) {
       
        Guild guild = event.getGuild();
        User user = event.getUser();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Join Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Member Joined: " + user.getName() + "\nGuild: " + guild.getName()));
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setColor(Color.GREEN);

        eb.addField(MarkdownUtil.underline("Member Name"), "╰┈➤" + user.getName(), false);
        eb.addField(MarkdownUtil.underline("Member Mention"), "╰┈➤" + user.getAsMention(), false);
        eb.addField(MarkdownUtil.underline("Member ID"), "╰┈➤" + user.getId(), false);
        eb.addField(MarkdownUtil.underline("Account Created"), "╰┈➤" + DurationUtils.isoToLocalTimeCounter(user.getTimeCreated()), false);
        eb.addField(MarkdownUtil.underline("Bot Account"), "╰┈➤" + BooleanUtils.formatToYesOrNo(user.isBot()), false);

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        performChecksThenBuildAndSendEmbed(event, eb);
    }

    public void handleGuildMemberRemove(@NonNull GuildMemberRemoveEvent event) {
       
        Guild guild = event.getGuild();
        User user = event.getUser();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Leave Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Member Left: " + user.getName() + "\nGuild: " + guild.getName()));
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setColor(Color.RED);

        eb.addField(MarkdownUtil.underline("Member Name"), "╰┈➤" + user.getName(), false);
        eb.addField(MarkdownUtil.underline("Member ID"), "╰┈➤" + user.getId(), false);
        eb.addField(MarkdownUtil.underline("Member Account Created"), "╰┈➤" + DurationUtils.isoToLocalTimeCounter(user.getTimeCreated()), false);
        eb.addField(MarkdownUtil.underline("Member Joined The Server On"), "╰┈➤" + getMemberJoinDate(event), false);
        eb.addField(MarkdownUtil.underline("Member Left The Server On"), "╰┈➤" + getMemberLeaveDate(), false);

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        performChecksThenBuildAndSendEmbed(event, eb);
    }

    @NonNull
    private String getMemberJoinDate(@NonNull GuildMemberRemoveEvent event) {

        Member member = event.getMember();
        if (member == null)
            return "Member not cached";

        if (member.hasTimeJoined())
            return TimeFormat.DATE_TIME_LONG.format(member.getTimeJoined());

        return "Unavailable";
    }

    @NonNull
    private String getMemberLeaveDate() {
        return TimeFormat.DATE_TIME_LONG.format(Instant.now());
    }
}
