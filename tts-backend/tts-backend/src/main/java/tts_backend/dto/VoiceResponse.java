package tts_backend.dto;

public class VoiceResponse {

    private String name;
    private String language;
    private String languageName;

    public VoiceResponse() {
    }

    public VoiceResponse(
            String name,
            String language,
            String languageName
    ) {
        this.name = name;
        this.language = language;
        this.languageName = languageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}