package kh.edu.istad.stadoor.eureka;

import kh.edu.istad.stadoor.eureka.dto.ObservabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/eureka/observability")
@RequiredArgsConstructor
public class ObservabilityController {

    private final ApplicationAvailability applicationAvailability;

    @GetMapping
    public Mono<ObservabilityResponse> getObservability() {
        return Mono.fromSupplier(() -> new ObservabilityResponse(
                status(),
                uptime(),
                cpuUsed(),
                diskUsed(),
                memoryUsed()
        ));
    }

    private String status() {
        if (applicationAvailability.getLivenessState() != LivenessState.CORRECT) {
            return "DOWN";
        }
        if (applicationAvailability.getReadinessState() != ReadinessState.ACCEPTING_TRAFFIC) {
            return "OUT_OF_SERVICE";
        }
        return "UP";
    }

    private String uptime() {
        Duration duration = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        if (days > 0) {
            return "%dd %dh %dm %ds".formatted(days, hours, minutes, seconds);
        }
        return "%dh %dm %ds".formatted(hours, minutes, seconds);
    }

    private String cpuUsed() {
        java.lang.management.OperatingSystemMXBean systemBean = ManagementFactory.getOperatingSystemMXBean();
        if (systemBean instanceof com.sun.management.OperatingSystemMXBean operatingSystemBean) {
            return formatPercent(operatingSystemBean.getProcessCpuLoad());
        }
        return "N/A";
    }

    private String diskUsed() {
        try {
            FileStore fileStore = Files.getFileStore(Path.of("").toAbsolutePath());
            long total = fileStore.getTotalSpace();
            long used = total - fileStore.getUsableSpace();
            return formatPercent(total == 0 ? -1 : (double) used / total);
        } catch (IOException e) {
            return "N/A";
        }
    }

    private String memoryUsed() {
        Runtime runtime = Runtime.getRuntime();
        long usedBytes = runtime.totalMemory() - runtime.freeMemory();
        return String.format(Locale.US, "%.2f MB", usedBytes / 1024.0 / 1024.0);
    }

    private String formatPercent(double value) {
        if (value < 0) {
            return "N/A";
        }
        return String.format(Locale.US, "%.2f%%", value * 100);
    }

}
