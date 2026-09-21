package tts_backend.dto;

public class TtsResponse {

    private boolean success;
    private String audioUrl;

    public TtsResponse() {
    }

    public TtsResponse(boolean success, String audioUrl) {
        this.success = success;
        this.audioUrl = audioUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}