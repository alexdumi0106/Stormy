from typing import List, Optional
from pydantic import BaseModel, Field


class OutfitRecommendationRequest(BaseModel):
    cityName: str
    temperature: Optional[float] = None
    apparentTemperature: Optional[float] = None
    humidity: Optional[int] = None
    windSpeed: Optional[float] = None
    precipitationNextHours: List[float] = Field(default_factory=list)
    uvIndex: Optional[float] = None
    momentOfDay: str
    nextHours: List[str] = Field(default_factory=list)
    nextTemperatures: List[float] = Field(default_factory=list)