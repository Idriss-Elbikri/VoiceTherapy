package org.example.ai_voice.repository;

import org.example.ai_voice.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
} 