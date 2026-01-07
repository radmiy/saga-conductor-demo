package com.radmiy.sagaconductordemo.conductor;

import com.netflix.conductor.client.exception.ConductorClientException;
import com.netflix.conductor.client.http.ConductorClient;
import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConductorInitializer {

    private final ConductorClient conductorClient;
    private final ObjectMapper objectMapper;

    @Value("${conductor.client.rootUri:http://localhost:8080/api/}")
    private String rootUri;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Start of metadata initialization Conductor...");

        MetadataClient metadataClient = new MetadataClient(conductorClient);

        try {
            List<TaskDef> taskDefs = objectMapper.readValue(
                    new ClassPathResource("conductor/tasks.json").getInputStream(),
                    new TypeReference<List<TaskDef>>() {
                    }
            );
            metadataClient.registerTaskDefs(taskDefs);
            log.info("Task definitions registered {}", taskDefs.size());

            WorkflowDef compensationWf = objectMapper.readValue(
                    new ClassPathResource("conductor/compensation_wf.json").getInputStream(),
                    WorkflowDef.class
            );
            metadataClient.registerWorkflowDef(compensationWf);
            log.info("Rollback Workflow Registered: {}", compensationWf.getName());

            WorkflowDef mainWf = objectMapper.readValue(
                    new ClassPathResource("conductor/saga_wf.json").getInputStream(),
                    WorkflowDef.class
            );
            metadataClient.registerWorkflowDef(mainWf);
            log.info("The main Workflow is registered: {}", mainWf.getName());

        } catch (Exception e) {
            if (e instanceof ConductorClientException cce) {
                if (cce.getStatus() == 409) {
                    log.warn("The metadata already exists, skip initialization.");
                    return;
                }
            }
            log.error("Critical initialization error: ", e);
        }
    }
}
