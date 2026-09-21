package tts_backend.integration;

import org.springframework.stereotype.Component;
import tts_backend.dto.TtsRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import tts_backend.dto.VoiceResponse;

@Component
public class LocalTtsProvider {

    private final Path audioDirectory = Paths.get("generated-audio");

    public String generateAudio(TtsRequest request)
            throws IOException, InterruptedException {

        Files.createDirectories(audioDirectory);

        String fileName = UUID.randomUUID() + ".wav";
        Path outputFile = audioDirectory.resolve(fileName);

        String voice = request.getVoice() == null
                ? "default"
                : request.getVoice();

        voice = voice.replace("'", "''");

        String encodedText = Base64.getEncoder().encodeToString(
                request.getText().getBytes(StandardCharsets.UTF_8)
        );

        String outputPath = outputFile.toAbsolutePath()
                .toString()
                .replace("'", "''");

        String script = """
                Add-Type -AssemblyName System.Speech

                $speaker = New-Object System.Speech.Synthesis.SpeechSynthesizer

                if ('%s' -ne 'default') {
                    $speaker.SelectVoice('%s')
                }

                $text = [System.Text.Encoding]::UTF8.GetString(
                    [System.Convert]::FromBase64String('%s')
                )

                $speaker.SetOutputToWaveFile('%s')
                $speaker.Speak($text)
                $speaker.Dispose()
                """.formatted(
                voice,
                voice,
                encodedText,
                outputPath
        );

        String encodedScript = Base64.getEncoder().encodeToString(
                script.getBytes(StandardCharsets.UTF_16LE)
        );

        Process process = new ProcessBuilder(
                "powershell.exe",
                "-NoProfile",
                "-EncodedCommand",
                encodedScript
        ).redirectErrorStream(true).start();

        String processOutput = new String(
                process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        int exitCode = process.waitFor();

        if (exitCode != 0 || !Files.exists(outputFile)) {
            throw new IOException(
                    "Failed to generate audio. " + processOutput
            );
        }

        return fileName;
    }
    
    public List<VoiceResponse> getInstalledVoices() {

        List<VoiceResponse> voices = new ArrayList<>();

        String script = """
                Add-Type -AssemblyName System.Speech

                $speaker = New-Object System.Speech.Synthesis.SpeechSynthesizer

                $speaker.GetInstalledVoices() |
                    ForEach-Object {
                        $voice = $_.VoiceInfo

                        Write-Output (
                            $voice.Name + "|||" +
                            $voice.Culture.Name + "|||" +
                            $voice.Culture.DisplayName
                        )
                    }

                $speaker.Dispose()
                """;

        try {

            String encodedScript = Base64.getEncoder().encodeToString(
                    script.getBytes(StandardCharsets.UTF_16LE)
            );

            Process process = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-EncodedCommand",
                    encodedScript
            )
                    .redirectErrorStream(true)
                    .start();

            String output = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            ).trim();

            int exitCode = process.waitFor();

            if (exitCode != 0 || output.isBlank()) {
                return voices;
            }

            String[] lines = output.split("\\R");

            for (String line : lines) {

                String[] parts = line.split("\\|\\|\\|", -1);

                if (parts.length == 3) {

                    String name = parts[0].trim();
                    String language = parts[1].trim();
                    String languageName = parts[2].trim();

                    if (!name.isBlank()
                            && !language.isBlank()
                            && !languageName.isBlank()) {

                        voices.add(
                                new VoiceResponse(
                                        name,
                                        language,
                                        languageName
                                )
                        );
                    }
                }
            }

        } catch (Exception e) {
            return voices;
        }

        return voices;
    }
}