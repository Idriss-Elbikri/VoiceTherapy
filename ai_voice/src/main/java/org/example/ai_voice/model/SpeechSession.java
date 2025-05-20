package org.example.ai_voice.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "speech_session")
public class SpeechSession {

    @Id
    private UUID id;

    // Assuming user_id will be added later, for now it can be nullable or default
    // @Column(name = "user_id")
    // private UUID userId;

    @Column(name = "duration_sec")
    private Double durationSec;

    @Column(name = "words_per_min")
    private Double wordsPerMin;

    @Column(name = "disfluency_pct")
    private Double disfluencyPct;

    private Integer pauses;
    private Integer repetitions;
    private Integer prolongations;

    // Store the raw transcription and analysis result (JSONB in DB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_result", columnDefinition = "jsonb")
    private String rawResult;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private SessionStatus status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    // Enum for session status
    public enum SessionStatus {
        QUEUED,
        PROCESSING,
        DONE,
        ERROR
    }

    // Constructors, getters, and setters

    public SpeechSession() {
        this.id = UUID.randomUUID(); // Generate UUID on creation
        this.createdAt = OffsetDateTime.now();
        this.status = SessionStatus.QUEUED; // Default status
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(Double durationSec) {
        this.durationSec = durationSec;
    }

    public Double getWordsPerMin() {
        return wordsPerMin;
    }

    public void setWordsPerMin(Double wordsPerMin) {
        this.wordsPerMin = wordsPerMin;
    }

    public Double getDisfluencyPct() {
        return disfluencyPct;
    }

    public void setDisfluencyPct(Double disfluencyPct) {
        this.disfluencyPct = disfluencyPct;
    }

    public Integer getPauses() {
        return pauses;
    }

    public void setPauses(Integer pauses) {
        this.pauses = pauses;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer repetitions) {
        this.repetitions = repetitions;
    }

    public Integer getProlongations() {
        return prolongations;
    }

    public void setProlongations(Integer prolongations) {
        this.prolongations = prolongations;
    }

    public String getRawResult() {
        return rawResult;
    }

    public void setRawResult(String rawResult) {
        this.rawResult = rawResult;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 