package com.radmiy.sagaconductordemo.util;

import com.netflix.conductor.common.metadata.tasks.TaskResult;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RandomErrorUtil {

    // Initialization task error
    // if random >= 0.2 normal workflow
    // if random < 0.2 and random >= 0.15 then application error
    // if random < 0.15 then terminate workflow as it has an error in business logic
    public static TaskResult getError(TaskResult result, String error, String errorStep, String failedTerminalError) {
        boolean isSimError = error != null && !error.isEmpty();

        Double random = Math.random();
        if (random < 0.2 || isSimError) {
            log.info("Random error={}", random);
            log.error(errorStep);
            if (random < 0.15) {
                result.setStatus(TaskResult.Status.FAILED_WITH_TERMINAL_ERROR);
                result.setReasonForIncompletion(failedTerminalError);
                return result;
            }
            throw new RuntimeException(errorStep);
        }
        return result;
    }
}
