package io.github.eggy03.papertrail.lite.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class ReadinessCheckController implements HealthCheck {

    private final @NonNull JDA jda;

    @Inject
    public ReadinessCheckController(@NonNull JDA jda) {
        this.jda = jda;
    }

    @Override
    public HealthCheckResponse call() {

        if (jda.getStatus() == JDA.Status.CONNECTED) {
            return HealthCheckResponse.named("Readiness Check")
                    .withData("JDA Readiness", "JDA is ready")
                    .up()
                    .build();
        } else {
            return HealthCheckResponse.named("Readiness Check")
                    .withData("JDA Readiness", "Preparing: " + jda.getStatus())
                    .down()
                    .build();
        }
    }
}
