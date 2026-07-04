from pydantic import BaseModel

class SkyObservationRequest(BaseModel):
    cloudType: str
    rainProbability: int
    stormProbability: int
    photographyScore: int
    bestMoment: str
    sunsetScore: int
    sunriseScore: int
    stormScore: int
    dramaticCloudsScore: int
    fogScore: int
    skyRatio: float
    cloudRatio: float
    darkCloudRatio: float
    warmLightRatio: float
    averageBrightness: float
    averageSaturation: float
    contrast: float