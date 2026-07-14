package io.github.eggy03.papertrail.lite.utils.guild.auditlog;

import io.github.eggy03.papertrail.lite.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class ThreadUtils {

    @NonNull
    public static final String FALLBACK_STRING = "N/A";

    @NonNull
    public static String resolveAutoArchiveDuration(@Nullable Object minutes) {

        Integer minuteInt = NumberParseUtils.parseInt(minutes);

        if (minuteInt == null)
            return FALLBACK_STRING;

        return ThreadChannel.AutoArchiveDuration.fromKey(minuteInt).name();
    }
}
