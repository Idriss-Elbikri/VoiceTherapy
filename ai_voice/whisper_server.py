from flask import Flask, request, jsonify
import whisper
import tempfile
import os
import subprocess

app = Flask(__name__)

# Charge le modèle Whisper ("base" est rapide et léger, tu peux tester "small" ou "medium" plus tard)
model = whisper.load_model("base")

@app.route('/transcribe', methods=['POST'])
def transcribe():
    if 'file' not in request.files:
        return jsonify({'error': 'No file provided'}), 400
    file = request.files['file']
    try:
        # Sauvegarde du fichier reçu en .3gp
        with tempfile.NamedTemporaryFile(delete=False, suffix=".3gp") as tmp_in:
            file.save(tmp_in.name)
            tmp_in_path = tmp_in.name

        # Conversion en .wav avec ffmpeg
        with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp_out:
            tmp_out_path = tmp_out.name

        cmd = ["ffmpeg", "-y", "-i", tmp_in_path, tmp_out_path]
        subprocess.run(cmd, check=True)

        # Transcription du .wav
        result = model.transcribe(tmp_out_path, language="en")

        # Nettoyage
        os.unlink(tmp_in_path)
        os.unlink(tmp_out_path)

        return jsonify({'transcription': result['text']})
    except Exception as e:
        print("Erreur lors de la transcription :", e)
        import traceback
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    # Le serveur écoute sur toutes les interfaces, port 5005
    app.run(host='0.0.0.0', port=5005) 