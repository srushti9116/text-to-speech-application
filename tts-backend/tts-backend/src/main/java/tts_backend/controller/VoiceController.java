package tts_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tts_backend.dto.VoiceResponse;
import tts_backend.integration.LocalTtsProvider;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VoiceController {

    private final LocalTtsProvider localTtsProvider;

    public VoiceController(LocalTtsProvider localTtsProvider) {
        this.localTtsProvider = localTtsProvider;
    }

    @GetMapping("/voices")
    public List<VoiceResponse> getVoices() {
        return localTtsProvider.getInstalledVoices();
    }
}