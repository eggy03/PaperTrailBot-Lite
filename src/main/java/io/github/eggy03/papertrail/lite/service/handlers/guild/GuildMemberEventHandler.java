package io.github.eggy03.papertrail.lite.service.handlers.guild;

import io.github.eggy03.papertrail.lite.configuration.PaperTrailConfig;
import io.github.eggy03.papertrail.lite.service.EmbedSendingService;
import io.github.eggy03.papertrail.lite.utils.BooleanUtils;
import io.github.eggy03.papertrail.lite.utils.DurationUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.time.Instant;

@ApplicationScoped
@Slf4j
public final class GuildMemberEventHandler {

    private final @NonNull PaperTrailConfig paperTrailConfig;
    private final @NonNull EmbedSendingService embedSendingService;

    @Inject
    public GuildMemberEventHandler(@NonNull PaperTrailConfig paperTrailConfig, @NonNull EmbedSendingService embedSendingService) {
        this.paperTrailConfig = paperTrailConfig;
        this.embedSendingService = embedSendingService;
    }

    public void handleGuildMemberJoin(@NonNull GuildMemberJoinEvent event) {

        Guild guild = event.getGuild();
        User user = event.getUser();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Join Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Member Joined: " + user.getName() + "\nGuild: " + guild.getName()));
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setColor(paperTrailConfig.embedColor().successColor());

        eb.addField(MarkdownUtil.underline("Member Name"), "╰┈➤" + user.getName(), false);
        eb.addField(MarkdownUtil.underline("Member Mention"), "╰┈➤" + user.getAsMention(), false);
        eb.addField(MarkdownUtil.underline("Member ID"), "╰┈➤" + user.getId(), false);
        eb.addField(MarkdownUtil.underline("Account Created"), "╰┈➤" + DurationUtils.isoToLocalTimeCounter(user.getTimeCreated()), false);
        eb.addField(MarkdownUtil.underline("Bot Account"), "╰┈➤" + BooleanUtils.formatToYesOrNo(user.isBot()), false);

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().memberEvent().logChannel());
    }

    public void handleGuildMemberRemove(@NonNull GuildMemberRemoveEvent event) {

        Guild guild = event.getGuild();
        User user = event.getUser();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Member Leave Event");
        eb.setDescription(MarkdownUtil.quoteBlock("Member Left: " + user.getName() + "\nGuild: " + guild.getName()));
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        eb.setColor(paperTrailConfig.embedColor().destructiveColor());

        eb.addField(MarkdownUtil.underline("Member Name"), "╰┈➤" + user.getName(), false);
        eb.addField(MarkdownUtil.underline("Member ID"), "╰┈➤" + user.getId(), false);
        eb.addField(MarkdownUtil.underline("Member Account Created"), "╰┈➤" + DurationUtils.isoToLocalTimeCounter(user.getTimeCreated()), false);
        eb.addField(MarkdownUtil.underline("Member Joined The Server On"), "╰┈➤" + getMemberJoinDate(event), false);
        eb.addField(MarkdownUtil.underline("Member Left The Server On"), "╰┈➤" + getMemberLeaveDate(), false);

        eb.setFooter(event.getGuild().getName());
        eb.setTimestamp(Instant.now());

        embedSendingService.checkAndSend(event, eb, paperTrailConfig.guild().memberEvent().logChannel());
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
