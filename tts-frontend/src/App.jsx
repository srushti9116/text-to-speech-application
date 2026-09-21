import { useEffect, useMemo, useState } from "react";
import "./App.css";

function App() {
  const [text, setText] = useState("");

  const [language, setLanguage] = useState("");

  const [voice, setVoice] = useState("");

  const [voices, setVoices] = useState([]);

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState("");

  const [success, setSuccess] = useState("");

  const [audioUrl, setAudioUrl] = useState("");

  useEffect(() => {
    const loadVoices = async () => {
      try {
        setError("");

        const response = await fetch("/api/voices");

        if (!response.ok) {
          throw new Error("Failed to load voices.");
        }

        const data = await response.json();

        setVoices(data);

        if (data.length > 0) {
          setLanguage(data[0].language);
          setVoice(data[0].name);
        }

      } catch (err) {
        setError(err.message || "Failed to load voices.");
      }
    };

    loadVoices();
  }, []);

  const languages = useMemo(() => {

    const uniqueLanguages = [];

    voices.forEach((item) => {

      const alreadyExists = uniqueLanguages.some(
        (languageItem) =>
          languageItem.code === item.language
      );

      if (!alreadyExists) {
        uniqueLanguages.push({
          code: item.language,
          name: item.languageName
        });
      }
    });

    return uniqueLanguages;

  }, [voices]);

  const filteredVoices = useMemo(() => {

    return voices.filter(
      (item) => item.language === language
    );

  }, [voices, language]);

  const handleLanguageChange = (newLanguage) => {

    setLanguage(newLanguage);

    const voicesForLanguage = voices.filter(
      (item) => item.language === newLanguage
    );

    if (voicesForLanguage.length > 0) {
      setVoice(voicesForLanguage[0].name);
    } else {
      setVoice("");
    }

    setAudioUrl("");
    setSuccess("");
    setError("");
  };

  const generateSpeech = async () => {

    setError("");
    setSuccess("");
    setAudioUrl("");

    if (!text.trim()) {
      setError("Please enter some text.");
      return;
    }

    if (!language) {
      setError("Please select a language.");
      return;
    }

    if (!voice) {
      setError("Please select a voice.");
      return;
    }

    setLoading(true);

    try {

      const response = await fetch("/api/tts", {
        method: "POST",

        headers: {
          "Content-Type": "application/json"
        },

        body: JSON.stringify({
          text: text,
          language: language,
          voice: voice
        })
      });

      const data = await response.json();

      if (!response.ok) {

        if (data.errors) {
          const messages = Object.values(data.errors);

          throw new Error(messages.join(", "));
        }

        throw new Error(
          data.message || "Failed to generate speech."
        );
      }

      if (!data.success || !data.audioUrl) {
        throw new Error("Speech generation failed.");
      }

      setAudioUrl(data.audioUrl);

      setSuccess(
        "Speech generated successfully!"
      );

    } catch (err) {

      setError(
        err.message || "Something went wrong."
      );

    } finally {

      setLoading(false);
    }
  };

  const clearText = () => {

    setText("");

    setAudioUrl("");

    setSuccess("");

    setError("");
  };

  return (
    <div className="app">

      <div className="container">

        <h1>Text-to-Speech</h1>

        <p className="subtitle">
          Convert your text into natural speech
        </p>

        <div className="form-group">

          <label>Enter Text</label>

          <textarea
            value={text}
            onChange={(e) =>
              setText(e.target.value)
            }
            placeholder="Type or paste your text here..."
            maxLength={5000}
          />

          <button
            type="button"
            className="clear-button"
            onClick={clearText}
          >
            Clear Text
          </button>

          <div className="text-stats">

            <span>
              {text.trim()
                ? text.trim().split(/\s+/).length
                : 0}{" "}
              words
            </span>

            <span>
              {text.length} / 5000 characters
            </span>

          </div>

        </div>

        <div className="selection-row">

          <div className="form-group">

            <label>Language</label>

            <select
              value={language}
              onChange={(e) =>
                handleLanguageChange(
                  e.target.value
                )
              }
            >

              {languages.length === 0 ? (
                <option value="">
                  No languages available
                </option>
              ) : (
                languages.map((item) => (
                  <option
                    key={item.code}
                    value={item.code}
                  >
                    {item.name}
                  </option>
                ))
              )}

            </select>

          </div>

          <div className="form-group">

            <label>Voice</label>

            <select
              value={voice}
              onChange={(e) =>
                setVoice(e.target.value)
              }
            >

              {filteredVoices.length === 0 ? (
                <option value="">
                  No voices available
                </option>
              ) : (
                filteredVoices.map((item) => (
                  <option
                    key={item.name}
                    value={item.name}
                  >
                    {item.name}
                  </option>
                ))
              )}

            </select>

          </div>

        </div>

        <button
          className="generate-button"
          onClick={generateSpeech}
          disabled={loading}
        >

          {loading
            ? "⏳ Generating..."
            : "🔊 Generate Speech"}

        </button>

        {success && (
          <div className="success-message">
            ✓ {success}
          </div>
        )}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {audioUrl && (
          <div className="audio-section">

            <h2>Generated Audio</h2>

            <audio
              controls
              src={audioUrl}
            />

            <a
              className="download-button"
              href={audioUrl}
              download
            >
              ⬇ Download Audio
            </a>

          </div>
        )}

      </div>

    </div>
  );
}

export default App;