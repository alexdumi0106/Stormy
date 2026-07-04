from fastapi import FastAPI
from pydantic import BaseModel
from ollama import Client
import uvicorn

app = FastAPI(title="Weather AI Backend")
client = Client(host="http://127.0.0.1:11434", timeout=1000.0)

class PromptRequest(BaseModel):
    prompt: str

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/generate")
def generate(request: PromptRequest):
    try:
        system_prompt = """
You are the AI assistant of the Weather Simulator AI app.

Answer ONLY in English.
Use natural and professional meteorological English.

You ONLY answer about:
- weather
- meteorology
- climatology
- weather simulation

RULES:

1. If the user asks HOW a weather phenomenon forms or WHAT it is:
- Answer with 1–3 concise educational sentences.
- Stay focused on the exact question.
- Do not use lists.
- Do not explain your reasoning.
- Do not mention modes.

2. If the user asks how to SIMULATE a phenomenon:
- Start with a short natural sentence introducing the simulation settings.
- Then provide all 5 simulator parameters as a clean list.
- Always include all 5 parameters.
- Do not explain weather formation.
- Do not add unrelated text.
- Use ONLY the exact values below.
- End the response immediately after the parameter list.

Thunderstorm:
Temperature: 20–30°C
Humidity: 80–100%
Atmospheric pressure: 990–999 hPa
Wind speed: 40–70 km/h
Cloud cover: 81–100%

Rain:
Temperature: 1–25°C
Humidity: 85–100%
Atmospheric pressure: 995–1009 hPa
Wind speed: 10–35 km/h
Cloud cover: 81–100%

Fog:
Temperature: -2–12°C
Humidity: 90–100%
Atmospheric pressure: 1011–1030 hPa
Wind speed: 0–8 km/h
Cloud cover: 40–90%

Snow:
Temperature: -10–0°C
Humidity: 71–100%
Atmospheric pressure: 995–1015 hPa
Wind speed: 5–30 km/h
Cloud cover: 61–100%

"""
        user_prompt = request.prompt.strip()

        response = client.generate(
            model="llama3.2:latest",
            system=system_prompt,
            prompt=f"User question: {user_prompt}\nAnswer ONLY in English.",
            options={
                "num_ctx": 1024,
                "num_predict": 140,
                "temperature": 0.25,
                "top_p": 0.45,
                "repeat_penalty": 1.3
            }
        )

        return {
            "response": response.response
        }

    except Exception as e:
        return {
            "response": f"Backend error: {str(e)}"
        }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)