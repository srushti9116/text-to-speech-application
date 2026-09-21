package tts_backend.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tts_backend.dto.TtsRequest;
import tts_backend.dto.TtsResponse;
import tts_backend.service.TtsService;

@RestController
@RequestMapping("/api")
public class TtsController {

    private final TtsService ttsService;

    public TtsController(TtsService ttsService) {
        this.ttsService = ttsService;
    }

    @PostMapping("/tts")
    public TtsResponse generateSpeech(@Valid @RequestBody TtsRequest request) {
        return ttsService.generateSpeech(request);
    }
}