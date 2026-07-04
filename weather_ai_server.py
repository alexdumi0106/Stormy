from fastapi import FastAPI
from pydantic import BaseModel
from ollama import Client
import os
import requests
import uvicorn
import re
from prompts.sky_observation_prompt import build_sky_observation_prompt
from schemas.sky_analyzer_models import SkyObservationRequest
from prompts.outfit_recommendation_prompt import build_outfit_recommendation_prompt
from schemas.outfit_models import OutfitRecommendationRequest
from prompts.weather_simulation_prompt import build_weather_simulation_prompt
from schemas.weather_simulation_models import WeatherSimulationRequest
from prompts.weather_story_prompt import build_weather_story_prompt
from prompts.historical_weather_prompt import (
    build_historical_day_description_prompt,
    build_climate_comparison_prompt
)
from schemas.weather_story_models import WeatherStoryRequest
from schemas.historical_weather_models import (
    HistoricalDayDescriptionRequest,
    ClimateComparisonRequest
)

app = FastAPI(title="Weather AI Backend")
ollama_client = Client(host="http://127.0.0.1:11434", timeout=1000.0)

class PromptRequest(BaseModel):
    prompt: str

class GeminiQuotaExceededError(Exception):
    pass

class GeminiBackendError(Exception):
    pass

def ask_gemini(
    prompt: str,
    max_output_tokens: int = 220,
    temperature: float = 0.4,
    top_p: float = 0.8
) -> str:
    api_key = os.getenv("GEMINI_API_KEY")
    model = os.getenv("GEMINI_MODEL", "gemini-2.5-flash-lite")

    if not api_key:
        raise GeminiBackendError("GEMINI_API_KEY is not set.")

    url = f"https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={api_key}"

    response = requests.post(
        url,
        headers={"Content-Type": "application/json"},
        json={
            "contents": [
                {
                    "parts": [
                        {
                            "text": prompt
                        }
                    ]
                }
            ],
            "generationConfig": {
                "temperature": temperature,
                "topP": top_p,
                "maxOutputTokens": max_output_tokens,
                "thinkingConfig": {
                    "thinkingBudget": 0
                }
            }
        },
        timeout=60
    )

    response_text = response.text or ""

    if response.status_code == 429 or "RESOURCE_EXHAUSTED" in response_text:
        raise GeminiQuotaExceededError("Gemini quota or rate limit was reached.")

    if response.status_code >= 400:
        raise GeminiBackendError(f"Gemini error {response.status_code}: {response_text[:300]}")

    data = response.json()
    candidate = data.get("candidates", [{}])[0]
    parts = candidate.get("content", {}).get("parts", [])

    text = "".join(
        part.get("text", "")
        for part in parts
        if part.get("text")
    ).strip()

    if not text:
        finish_reason = candidate.get("finishReason", "")
        raise GeminiBackendError(f"Gemini returned empty text. Finish reason: {finish_reason}.")

    return text

def ask_ai_with_quota_fallback(
    prompt: str,
    fallback,
    max_output_tokens: int = 220,
    temperature: float = 0.4,
    top_p: float = 0.8
) -> str:
    try:
        return ask_gemini(
            prompt=prompt,
            max_output_tokens=max_output_tokens,
            temperature=temperature,
            top_p=top_p
        )
    except GeminiQuotaExceededError:
        return fallback()

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/generate")
def generate(request: PromptRequest):
    try:
        user_prompt = request.prompt.strip()

        instruction = (
            "Esti asistentul AI al aplicatiei Weather Simulator AI. "
            "Raspunde strict in limba romana. "
            "Nu te prezenta. Nu spune salut. "
            "Scrie o poveste scurta a vremii, naturala si clara. "
            "Foloseste doar datele primite. Nu inventa valori. "
            "Raspunsul trebuie sa aiba exact 2 propozitii."
        )

        full_prompt = f"{instruction}\n\nDate meteo:\n{user_prompt}"

        response = ask_ai_with_quota_fallback(
            prompt=full_prompt,
            fallback=lambda: generate_local(PromptRequest(prompt=user_prompt))["response"],
            max_output_tokens=512,
            temperature=0.4,
            top_p=0.8
        )

        return {"response": response}

    except Exception as e:
        return {"response": f"Backend error: {str(e)}"}

@app.post("/generate-local")
def generate_local(request: PromptRequest):
    try:
        system_prompt = """
Esti asistentul AI al aplicatiei Weather Simulator AI.

Raspunde strict in limba romana.
Scrie descrieri meteorologice naturale, clare si concise.
Raspunde doar despre vreme, meteorologie, climatologie si simulare meteo.

Pentru descrieri meteo istorice:
- Descrie mai intai aspectul fizic al vremii: starea cerului, nebulozitatea, soarele, ploaia, ceata, vantul si felul in care conditiile s-au schimbat pe parcursul zilei.
- Apoi mentioneaza valorile primite pentru temperatura, umiditate, presiune, rasarit si apus, daca sunt prezente.
- Foloseste doar datele primite in prompt.
- Nu inventa ploaie, furtuni, ceata, viteza vantului sau schimbari ale norilor daca nu apar in prompt.
- Nu saluta utilizatorul.
- Nu te prezenta.
- Pastreaza raspunsul la 2 sau 3 propozitii.

Pentru comparatii climatice:
- Compara mai intai impresia fizica a vremii, daca promptul ofera destule informatii.
- Apoi compara temperatura maxima, temperatura minima, umiditatea si presiunea.
- Foloseste doar datele primite in prompt.
- Pastreaza raspunsul la 2 sau 3 propozitii.
"""

        user_prompt = request.prompt.strip()

        response = ollama_client.generate(
            model="llama3.2:latest",
            system=system_prompt,
            prompt=f"Date meteo primite de la utilizator:\n{user_prompt}\n\nRaspunde strict in limba romana.",
            options={
                "num_ctx": 1024,
                "num_predict": 220,
                "temperature": 0.2,
                "top_p": 0.6,
                "repeat_penalty": 1.25
            }
        )

        return {
            "response": response.response.strip()
        }

    except Exception as e:
        return {
            "response": f"Backend local error: {str(e)}"
        }

def ask_ollama(prompt: str) -> str:
    response = ollama_client.generate(
        model="llama3.2:latest",
        system=(
            "Esti asistentul AI al aplicatiei Weather Simulator AI. "
            "Raspunde strict in limba romana. "
            "Scrie descrieri scurte, clare si naturale pentru fotografii ale cerului. "
            "Foloseste doar datele primite. Nu inventa valori, fenomene sau prognoze."
        ),
        prompt=prompt,
        options={
            "num_ctx": 1024,
            "num_predict": 140,
            "temperature": 0.1,
            "top_p": 0.45,
            "repeat_penalty": 1.18
        }
    )
    return response.response.strip()

def ask_outfit_ollama(prompt: str) -> str:
    response = ollama_client.generate(
        model="llama3.2:latest",
        system=(
            "Esti asistentul AI al aplicatiei Weather Simulator AI. "
            "Raspunde strict in limba romana. "
            "Oferi recomandari de tinute pe baza datelor meteo. "
            "Foloseste doar datele primite. Nu inventa valori. "
            "Nu folosi Markdown, stelute, bold sau liste cu asterisc. "
            "Nu repeta valorile brute primite."
        ),
        prompt=prompt,
        options={
            "num_ctx": 2048,
            "num_predict": 260,
            "temperature": 0.2,
            "top_p": 0.6,
            "repeat_penalty": 1.15
        }
    )
    return response.response.strip()

def clean_outfit_response(text: str) -> str:
    cleaned = text.replace("**", "")
    cleaned = re.sub(r"(?m)^\s*\*\s*", "", cleaned)
    cleaned = re.sub(r"(?m)^\s*[-#]\s*", "", cleaned)
    cleaned = re.sub(r"\n{3,}", "\n\n", cleaned)
    return cleaned.strip()

def ask_weather_simulation_ollama(prompt: str) -> str:
    response = ollama_client.generate(
        model="llama3.2:latest",
        system=(
            "Esti asistentul AI al simulatorului meteo Weather Simulator AI. "
            "Raspunde strict in limba romana. "
            "Pentru simulari, raspunde folosind doar cei 5 parametri ai simulatorului: "
            "temperatura, umiditate, presiune, vant si nori. "
            "Foloseste valori concrete si nu inventa controale care nu exista."
        ),
        prompt=prompt,
        options={
            "num_ctx": 2048,
            "num_predict": 220,
            "temperature": 0.15,
            "top_p": 0.55,
            "repeat_penalty": 1.18
        }
    )
    return response.response.strip()

def clean_weather_simulation_response(text: str) -> str:
    cleaned = text.replace("**", "")
    cleaned = re.sub(r"(?m)^\s*\*\s*", "", cleaned)
    cleaned = re.sub(r"(?m)^\s*[-#]\s*", "", cleaned)
    cleaned = re.sub(r"\n{3,}", "\n\n", cleaned)
    return cleaned.strip()

def ask_weather_text_ollama(prompt: str, max_tokens: int = 160) -> str:
    response = ollama_client.generate(
        model="llama3.2:latest",
        system=(
            "Esti asistentul AI al aplicatiei Weather Simulator AI. "
            "Raspunde strict in limba romana. "
            "Scrie descrieri meteo scurte, clare si naturale. "
            "Foloseste doar datele primite. Nu inventa valori. "
            "Respecta strict limita de cuvinte si numarul de propozitii cerute in prompt. "
            "Incheie raspunsul cu punct."
        ),
        prompt=prompt,
        options={
            "num_ctx": 2048,
            "num_predict": max_tokens,
            "temperature": 0.2,
            "top_p": 0.6,
            "repeat_penalty": 1.2
        }
    )
    return response.response.strip()

def ask_weather_story_gemini(prompt: str) -> str:
    api_key = os.getenv("GEMINI_API_KEY")

    if not api_key:
        return "Backend error: GEMINI_API_KEY is not set."

    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key={api_key}"

    response = requests.post(
        url,
        headers={"Content-Type": "application/json"},
        json={
            "contents": [
                {
                    "parts": [
                        {
                            "text": prompt
                        }
                    ]
                }
            ],
            "generationConfig": {
                "temperature": 0.4,
                "topP": 0.8,
                "maxOutputTokens": 220,
                "thinkingConfig": {
                    "thinkingBudget": 0
                }
            }
        },
        timeout=60
    )

    if response.status_code == 429:
        return "Serviciul AI a atins limita de utilizare pentru moment. Reincearca peste cateva minute."

    if response.status_code >= 400:
        return "Serviciul AI nu este disponibil momentan. Reincearca mai tarziu."

    data = response.json()
    candidate = data.get("candidates", [{}])[0]
    parts = candidate.get("content", {}).get("parts", [])

    text = "".join(
        part.get("text", "")
        for part in parts
        if part.get("text")
    ).strip()

    if not text:
        finish_reason = candidate.get("finishReason", "")
        return f"Backend error: Gemini returned empty text. Finish reason: {finish_reason}."

    return text

def clean_weather_text_response(text: str) -> str:
    cleaned = text.replace("**", "")
    cleaned = re.sub(r"(?m)^\s*\*\s*", "", cleaned)
    cleaned = re.sub(r"(?m)^\s*[-#]\s*", "", cleaned)
    cleaned = re.sub(r"\n{3,}", "\n\n", cleaned)
    cleaned = cleaned.strip()

    if cleaned and cleaned[-1] not in ".!?":
        last_sentence_end = max(cleaned.rfind("."), cleaned.rfind("!"), cleaned.rfind("?"))
        if last_sentence_end > 0:
            cleaned = cleaned[: last_sentence_end + 1].strip()

    return cleaned

@app.post("/ai/sky-observation")
def sky_observation(data: SkyObservationRequest):
    try:
        prompt = build_sky_observation_prompt(data)
        response = ask_ai_with_quota_fallback(
            prompt=prompt,
            fallback=lambda: ask_ollama(prompt),
            max_output_tokens=140,
            temperature=0.1,
            top_p=0.45
        )
        return {"response": response}
    except Exception as e:
        return {"response": f"Backend error: {str(e)}"}

@app.post("/ai/outfit-recommendation")
def outfit_recommendation(data: OutfitRecommendationRequest):
    try:
        prompt = build_outfit_recommendation_prompt(data)
        response = clean_outfit_response(
            ask_ai_with_quota_fallback(
                prompt=prompt,
                fallback=lambda: ask_outfit_ollama(prompt),
                max_output_tokens=260,
                temperature=0.2,
                top_p=0.6
            )
        )
        return {"response": response}
    except Exception as e:
        return {"response": f"Backend error: {str(e)}"}

@app.post("/ai/weather-simulation")
def weather_simulation(data: WeatherSimulationRequest):
    try:
        prompt = build_weather_simulation_prompt(data)
        response = clean_weather_simulation_response(
            ask_ai_with_quota_fallback(
                prompt=prompt,
                fallback=lambda: ask_weather_simulation_ollama(prompt),
                max_output_tokens=220,
                temperature=0.15,
                top_p=0.55
            )
        )
        return {"response": response}
    except Exception as e:
        return {"response": f"Backend error: {str(e)}"}

@app.post("/ai/weather-story")
def weather_story(data: WeatherStoryRequest):
    try:
        prompt = build_weather_story_prompt(data)
        response = clean_weather_text_response(
            ask_ai_with_quota_fallback(
                prompt=prompt,
                fallback=lambda: ask_weather_text_ollama(prompt, max_tokens=160),
                max_output_tokens=220,
                temperature=0.4,
                top_p=0.8
            )
        )
        return {"response": response}
    except Exception as e:
        return {"response": f"Backend error: {str(e)}"}

@app.post("/ai/historical-day-description")
def historical_day_description(data: HistoricalDayDescriptionRequest):
    try:
        prompt = build_historical_day_description_prompt(data)
        response = clean_weather_text_response(
            ask_ai_with_quota_fallback(
                prompt=prompt,
                fallback=lambda: ask_weather_text_ollama(prompt, max_tokens=220),
                max_output_tokens=220,
                temperature=0.2,
                top_p=0.6
            )
        )
        return {"response": response}
    except Exception as e:
        return {"response": f"Backend error: {str(e)}"}

@app.post("/ai/climate-comparison")
def climate_comparison(data: ClimateComparisonRequest):
    try:
        prompt = build_climate_comparison_prompt(data)
        response = clean_weather_text_response(
            ask_ai_with_quota_fallback(
                prompt=prompt,
                fallback=lambda: ask_weather_text_ollama(prompt, max_tokens=260),
                max_output_tokens=260,
                temperature=0.2,
                top_p=0.6
            )
        )
        return {"response": response}
    except Exception as e:
        return {"response": f"Backend error: {str(e)}"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
