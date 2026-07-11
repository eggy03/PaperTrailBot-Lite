package io.github.eggy03.papertrail.lite.about;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@SuppressWarnings("java:S6206")
public final class ApplicationInfo {

    private final @NonNull String projectVersion;
    private final @NonNull String projectName;

    @Inject
    public ApplicationInfo(
            @ConfigProperty(name = "quarkus.application.version") @NonNull String projectVersion,
            @ConfigProperty(name = "project.name") @NonNull String projectName)
    {
        this.projectVersion = projectVersion;
        this.projectName = projectName;
    }

    public @NonNull String projectName() {
        return projectName;
    }

    public @NonNull String projectVersion() {
        return "v" + projectVersion;
    }
}
