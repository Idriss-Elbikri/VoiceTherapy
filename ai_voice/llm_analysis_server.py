# llm_analysis_server.py
from flask import Flask, request, jsonify
import openai
import os
import json
import re

app = Flask(__name__)

# 🔐 Together setup
# Hardcoding the API key as requested.
# WARNING: Hardcoding sensitive information like API keys is not recommended for production environments.
openai.api_key = ""
openai.api_base = "https://api.together.xyz/v1"  # 👈 switch to Together endpoint

# Define the system prompt for the LLM
SYSTEM_PROMPT = """You are an AI assistant specializing in analyzing speech transcripts for signs of language and speech disorders, particularly focusing on disfluencies like stuttering, repetitions, prolongations, blocks, and unusual pausing patterns. Your goal is to provide a concise summary of potential issues observed in the text.\n\nAnalyze the following transcript and provide a clear, brief analysis of any potential speech disfluencies or patterns that might indicate a language disorder.\n\nAfter the analysis, generate exactly 3 specific exercises designed to help improve the identified speech patterns or general fluency. Each exercise should have a title, a brief description of how to perform it, and a difficulty level (Easy, Medium, or Hard).\n\nFormat your response as follows (plain text, no JSON, no code blocks, no markdown):\n\nAnalysis: <your analysis here>\n\nExercise 1:\nTitle: <title>\nDescription: <description>\nDifficulty: <difficulty>\n\nExercise 2:\nTitle: <title>\nDescription: <description>\nDifficulty: <difficulty>\n\nExercise 3:\nTitle: <title>\nDescription: <description>\nDifficulty: <difficulty>\n\nTranscript:\n"""

@app.route('/analyze_transcript', methods=['POST'])
def analyze_transcript():
    data = request.json
    if not data or 'transcript' not in data:
        return jsonify({'error': 'No transcript provided'}), 400

    transcript = data['transcript']
    full_prompt = SYSTEM_PROMPT + transcript

    try:
        # 💬 Run chat completion with DeepSeek V3
        response = openai.ChatCompletion.create(
            model="deepseek-ai/DeepSeek-V3",
            messages=[
                {"role": "system", "content": full_prompt},
                # {"role": "user", "content": transcript} # We put the transcript in the system prompt as requested by example
            ],
            temperature=0.7,
            max_tokens=250, # Keep max_tokens relatively low for concise analysis
        )

        # Assuming the LLM returns a JSON string within the content
        response_content = response["choices"][0]["message"]["content"]
        # Parse the plain text response
        def parse_llm_response(text):
            analysis = ""
            exercises = []
            lines = [line.strip() for line in text.splitlines() if line.strip()]
            i = 0
            while i < len(lines):
                if lines[i].startswith("Analysis:"):
                    analysis = lines[i][len("Analysis:"):].strip()
                    i += 1
                elif lines[i].startswith("Exercise"):
                    ex = {"title": "", "description": "", "difficulty": ""}
                    i += 1
                    while i < len(lines) and not lines[i].startswith("Exercise"):
                        if lines[i].startswith("Title:"):
                            ex["title"] = lines[i][len("Title:"):].strip()
                        elif lines[i].startswith("Description:"):
                            ex["description"] = lines[i][len("Description:"):].strip()
                        elif lines[i].startswith("Difficulty:"):
                            ex["difficulty"] = lines[i][len("Difficulty:"):].strip()
                        i += 1
                    exercises.append(ex)
                else:
                    i += 1
            return analysis, exercises
        analysis_result, exercises = parse_llm_response(response_content)
        return jsonify({'analysis': analysis_result, 'exercises': exercises})

    except openai.error.AuthenticationError as e:
        print("OpenAI Authentication Error:", e)
        return jsonify({'error': 'Authentication failed with the LLM provider. Check your API key.'}), 500
    except openai.error.APIError as e:
        print("OpenAI API Error:", e)
        return jsonify({'error': 'API error communicating with the LLM provider.'}), 500
    except Exception as e:
        print("Error during LLM analysis:", e)
        import traceback
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    # The server will listen on all interfaces, port 5006
    app.run(host='0.0.0.0', port=5006)