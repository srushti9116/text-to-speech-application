package tts_backend.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private final Path audioDirectory =
            Paths.get("generated-audio").toAbsolutePath().normalize();

    @GetMapping("/{fileName}")
    public ResponseEntity<ByteArrayResource> getAudio(
            @PathVariable String fileName) throws IOException {

        Path audioFile = audioDirectory.resolve(fileName).normalize();

        if (!audioFile.startsWith(audioDirectory)) {
            return ResponseEntity.badRequest().build();
        }

        if (!Files.exists(audioFile) || !Files.isRegularFile(audioFile)) {
            return ResponseEntity.notFound().build();
        }

        byte[] audioData = Files.readAllBytes(audioFile);

        ByteArrayResource resource =
                new ByteArrayResource(audioData);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileName + "\""
                )
                .contentLength(audioData.length)
                .body(resource);
    }
}