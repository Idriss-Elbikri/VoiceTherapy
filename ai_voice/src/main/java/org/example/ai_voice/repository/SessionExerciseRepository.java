package org.example.ai_voice.repository;

import org.example.ai_voice.model.SessionExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SessionExerciseRepository extends JpaRepository<SessionExercise, UUID> {
} 