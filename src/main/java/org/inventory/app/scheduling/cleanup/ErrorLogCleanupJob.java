package org.inventory.app.scheduling.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.model.ErrorLog;
import org.inventory.app.repository.ErrorLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorLogCleanupJob {

    private final ErrorLogRepository errorLogRepository;

    private static final long MAX_LOG_COUNT = 10_000;


    @Scheduled(cron = "0 0 * * * ?")
    public void cleanOldLogs() {
        long count = errorLogRepository.count();
        log.info("Starting ErrorLog cleanup job. Current log count: {}", count);

        if (count > MAX_LOG_COUNT) {
            long toDelete = count - MAX_LOG_COUNT;
            log.info("Log count exceeds max limit ({}). Deleting {} oldest logs...", MAX_LOG_COUNT, toDelete);

            List<ErrorLog> oldestLogs = errorLogRepository.findOldestLogs(PageRequest.of(0, (int) toDelete));
            errorLogRepository.deleteAll(oldestLogs);

            log.info("Deleted {} oldest error logs. Cleanup job completed.", toDelete);
        } else {
            log.info("Log count is within limit. No cleanup needed.");
        }
    }
}
