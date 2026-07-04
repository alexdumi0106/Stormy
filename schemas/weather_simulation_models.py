from pydantic import BaseModel


class WeatherSimulationRequest(BaseModel):
    prompt: str
