package io.github.eggy03.papertrail.lite.utils.guild.auditlog;

import io.github.eggy03.papertrail.lite.utils.NumberParseUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.StageInstance;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class StageUtils {

    @NonNull
    public static String resolveStagePrivacyLevel(@Nullable Object stagePrivacyLevelInteger) {
        Integer privacyLevel = NumberParseUtils.parseInt(stagePrivacyLevelInteger);

        if (privacyLevel == null) return "N/A";

        //JDA removed this, and it technically shouldn't happen either since all stages are GUILD_ONLY now
        if (privacyLevel == 1) return "PUBLIC (Deprecated)";

        return StageInstance.PrivacyLevel.fromKey(privacyLevel).name();
    }
}
