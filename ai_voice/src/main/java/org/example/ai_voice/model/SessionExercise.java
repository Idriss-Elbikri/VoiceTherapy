package org.example.ai_voice.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "session_exercise")
public class SessionExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private SpeechSession session;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    public SessionExercise() {}

    public SessionExercise(SpeechSession session, Exercise exercise) {
        this.session = session;
        this.exercise = exercise;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SpeechSession getSession() { return session; }
    public void setSession(SpeechSession session) { this.session = session; }
    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }
} 