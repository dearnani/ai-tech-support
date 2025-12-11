package com.techsupport.mcp;

import com.techsupport.service.SupportOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketProcessor {

    private final SupportOrchestrator orchestrator;

    @KafkaListener(topics = "support-tickets", groupId = "tech-support-group")
    public void processTicket(String ticketJson) {
        try {
            log.info("Processing ticket: {}", ticketJson);
            // Parse JSON and process
            String response = orchestrator.handleTicket(ticketJson).block();
            log.info("Ticket processed with response: {}", response);
        } catch (Exception e) {
            log.error("Error processing ticket", e);
        }
    }
}
