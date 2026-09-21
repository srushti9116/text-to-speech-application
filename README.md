# Text-to-Speech Application

A full-stack Text-to-Speech web application built with React and Spring Boot. The application converts user-entered text into speech using the Windows System.Speech engine and provides voice selection, audio playback, and WAV download functionality.

## Features

- Convert text into speech
- Character count with a 5000-character limit
- Word count
- Dynamic language selection based on installed voices
- Dynamic voice selection
- Microsoft David Desktop voice support
- Microsoft Zira Desktop voice support
- Audio playback directly in the browser
- Download generated audio as WAV
- Input validation and error handling
- REST APIs for health, voices, speech generation, and audio retrieval
- CORS configuration for local frontend-backend communication

## Technology Stack

### Frontend

- React
- Vite
- JavaScript
- HTML5
- CSS3

### Backend

- Java 21
- Spring Boot
- Maven
- REST APIs
- Jakarta Validation

### Text-to-Speech

- Windows System.Speech
- Microsoft David Desktop
- Microsoft Zira Desktop

## Project Structure

```text
text-to-speech/
├── .gitignore
├── README.md
├── tts-backend/
│   └── tts-backend/
│       ├── pom.xml
│       ├── mvnw
│       ├── mvnw.cmd
│       └── src/
│           ├── main/java/tts_backend/
│           │   ├── config/
│           │   ├── controller/
│           │   ├── dto/
│           │   ├── exception/
│           │   ├── integration/
│           │   └── service/
│           └── test/
│
└── tts-frontend/
    ├── package.json
    ├── package-lock.json
    ├── vite.config.js
    └── src/
        ├── App.jsx
        ├── App.css
        └── index.css
