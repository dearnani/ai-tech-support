package com.techsupport.service;

import com.techsupport.rag.RetrievalService;
import io.langfuse.client.Lib;
import io.langfuse.client.types.CreateGenerationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportOrchestrator {

    private final ChatClient chatClient;
    private final RetrievalService retrievalService;
    private final Lib langfuse;

    public Mono<String> handleTicket(String ticketText) {
        var trace = langfuse.trace("support-ticket-" + System.currentTimeMillis());

        return Mono.fromCallable(() -> {
            log.info("=== Router Agent ===");
            String classification = routerAgent(ticketText, trace);
            log.info("Classification: {}", classification);

            if (classification.contains("RETRIEVAL")) {
                log.info("=== Retrieval Agent ===");
                return retrievalAgent(ticketText, trace);
            } else {
                log.info("=== Resolution Agent (Direct) ===");
                return resolutionAgent(ticketText, trace);
            }
        }).doFinally(signal -> trace.end());
    }

    private String routerAgent(String input, io.langfuse.client.objects.Trace trace) {
        var generation = trace.generation("router-agent");
        try {
            String prompt = "Classify this support ticket. Is it a common issue (RETRIEVAL) " +
                    "or needs direct LLM reasoning (DIRECT)?\n\nTicket: " + input;

            var response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

            generation.end(response);
            return response;
        } catch (Exception e) {
            log.error("Router agent failed", e);
            generation.end("ERROR");
            return "RETRIEVAL";
        }
    }

    private String retrievalAgent(String input, io.langfuse.client.objects.Trace trace) {
        var generation = trace.generation("retrieval-agent");
        try {
            var docs = retrievalService.retrieve(input, 3);
            String context = docs.stream()
                .map(d -> d.getContent())
                .reduce("", (a, b) -> a + "\n" + b);

            String prompt = "Based on the knowledge base below, provide support:\n\n" +
                    "Knowledge Base:\n" + context + "\n\n" +
                    "Customer: " + input;

            var response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

            generation.end(response);
            return response;
        } catch (Exception e) {
            log.error("Retrieval agent failed", e);
            generation.end("ERROR");
            return "Unable to retrieve documents";
        }
    }

    private String resolutionAgent(String input, io.langfuse.client.objects.Trace trace) {
        var generation = trace.generation("resolution-agent");
        try {
            var response = chatClient.prompt()
                .user("Provide technical support for: " + input)
                .call()
                .content();

            generation.end(response);
            return response;
        } catch (Exception e) {
            log.error("Resolution agent failed", e);
            generation.end("ERROR");
            return "I'm unable to help with this request.";
        }
    }
}
