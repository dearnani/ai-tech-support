package com.techsupport.controller;

import com.techsupport.service.SupportOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportOrchestrator orchestrator;

    @PostMapping
    public Mono<ResponseEntity<SupportResponse>> createTicket(@RequestBody TicketRequest request) {
        return orchestrator.handleTicket(request.ticket())
            .map(response -> ResponseEntity.ok(
                new SupportResponse(request.ticket(), response)
            ))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                .body(new SupportResponse(request.ticket(), "Error: " + e.getMessage()))));
    }

    record TicketRequest(String ticket) {}
    record SupportResponse(String ticket, String resolution) {}
}
