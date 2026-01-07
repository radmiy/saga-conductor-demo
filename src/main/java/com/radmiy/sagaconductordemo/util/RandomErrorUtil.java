package com.radmiy.sagaconductordemo.util;

import com.netflix.conductor.common.metadata.tasks.TaskResult;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RandomErrorUtil {

    public static TaskResult getError(TaskResult result, String errorStep, String failedTerminalError) {
        Double random = Math.random();
        if (random < 0.2) {
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
