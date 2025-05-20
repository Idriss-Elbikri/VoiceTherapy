
This README includes:
1. Project branding with logo reference
2. Feature highlights in bullet points
3. Clean tech stack table
4. Key performance metrics
5. Getting started section with code blocks
6. Documentation links
7. Real-world impact data
8. Contribution guidelines pointer
9. License and contact info
10. Inspirational quote with deployment status

The formatting uses GitHub-flavored Markdown with emojis for visual scanning. I've referenced the images from your report (assuming they'll be uploaded to a `/media` folder) and maintained all technical accuracy from the source document.



=======================================================================================================================================================================================================================================================



# SpeechEase: AI-Powered Speech Disorder Screening & Therapy

![SpeechEase Logo](media/image1.png)

SpeechEase is an Android application coupled with a Spring Boot backend that enables early screening of speech disorders and proposes personalized therapy exercises. The platform leverages state-of-the-art AI models like OpenAI Whisper and HuggingFace Transformers to democratize access to speech therapy.

## 🌟 Key Features
- **Real-time stuttering detection** with severity scoring (0-5)
- **AI-generated articulation drills** tailored to user needs
- **Progress dashboard** with streaks, heatmaps, and clinician notes
- **Offline mode** with encrypted session storage
- **GDPR-compliant** data handling and privacy controls

## 🛠️ Tech Stack
| Component          | Technologies                                                                 |
|--------------------|------------------------------------------------------------------------------|
| **Mobile**         | Java/Kotlin, Android SDK 35, Retrofit 2.9                                   |
| **Backend**        | Spring Boot 3 (Java 21), JPA-Hibernate, PostgreSQL 15, Redis                |
| **AI Services**    | Python 3.10, FastAPI, Librosa 0.10, Torchaudio 2.2, Whisper-large-v3, HuBERT|
| **DevOps**         | Docker 24, Kubernetes, GitHub Actions, Prometheus/Grafana                   |

## 📊 Performance
- **Accuracy**: 0.87 Macro F1-score (Transformer model)
- **Latency**: <300 ms for dashboard queries (Redis-cached)
- **Scalability**: Handles 1,200 req/s at p95=280 ms (k6 load-tested)

## 🚀 Getting Started
### Prerequisites
- Java 17+, Python 3.10, Android SDK 35
- PostgreSQL 15, Docker 24+
- NVIDIA GPU (for AI inference)

### Installation
```bash
# Clone repository
git clone https://github.com/Idriss-Elbikri/VoiceTherapy.git

# Backend setup
cd backend && mvn clean install

# AI service setup
cd ../ai-service && pip install -r requirements.txt

# Mobile app setup (Android Studio required)
