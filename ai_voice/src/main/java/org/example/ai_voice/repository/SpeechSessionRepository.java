package org.example.ai_voice.repository;

import org.example.ai_voice.model.SpeechSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpeechSessionRepository extends JpaRepository<SpeechSession, UUID> {
    // Custom query methods can be added here if needed
} 