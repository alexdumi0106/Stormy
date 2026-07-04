from typing import List, Optional
from pydantic import BaseModel, Field


class WeatherStoryRequest(BaseModel):
    temperature: Optional[float] = None
    apparentTemperature: Optional[float] = None
    humidity: Optional[int] = None
    windSpeed: Optional[float] = None
    pressure: Optional[float] = None
    weatherCode: Optional[int] = None
    cloudCover: Optional[int] = None
    nextHours: List[str] = Field(default_factory=list)
    nextTemperatures: List[float] = Field(default_factory=list)
    nextPrecipitation: List[float] = Field(default_factory=list)
    nextWindSpeed: List[float] = Field(default_factory=list)
    nextWeatherCodes: List[int] = Field(default_factory=list)
    dailyDates: List[str] = Field(default_factory=list)
    dailyMaxTemperatures: List[float] = Field(default_factory=list)
    dailyMinTemperatures: List[float] = Field(default_factory=list)
    dailyWeatherCodes: List[int] = Field(default_factory=list)
