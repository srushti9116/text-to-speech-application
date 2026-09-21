package tts_backend.service;

import org.springframework.stereotype.Service;
import tts_backend.dto.TtsRequest;
import tts_backend.dto.TtsResponse;
import tts_backend.dto.VoiceResponse;
import tts_backend.integration.LocalTtsProvider;

import java.util.List;

@Service
public class TtsService {

    private final LocalTtsProvider localTtsProvider;

    public TtsService(LocalTtsProvider localTtsProvider) {
        this.localTtsProvider = localTtsProvider;
    }

    public TtsResponse generateSpeech(TtsRequest request) {

        List<VoiceResponse> installedVoices =
                localTtsProvider.getInstalledVoices();

        VoiceResponse selectedVoice = installedVoices.stream()
                .filter(voice ->
                        voice.getName().equals(request.getVoice())
                )
                .findFirst()
                .orElse(null);

        if (selectedVoice == null) {
            throw new IllegalArgumentException(
                    "Unsupported voice: " + request.getVoice()
            );
        }

        boolean languageSupported = installedVoices.stream()
                .anyMatch(voice ->
                        voice.getLanguage().equals(request.getLanguage())
                );

        if (!languageSupported) {
            throw new IllegalArgumentException(
                    "Unsupported language: " + request.getLanguage()
            );
        }

        if (!selectedVoice.getLanguage()
                .equals(request.getLanguage())) {

            throw new IllegalArgumentException(
                    "Voice '" + request.getVoice()
                            + "' does not belong to language '"
                            + request.getLanguage() + "'"
            );
        }

        try {

            String fileName =
                    localTtsProvider.generateAudio(request);

            return new TtsResponse(
                    true,
                    "/api/audio/" + fileName
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate speech.",
                    e
            );
        }
    }
}