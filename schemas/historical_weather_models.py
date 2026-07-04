from typing import List, Optional
from pydantic import BaseModel, Field


class HistoricalHourlySnapshot(BaseModel):
    time: str
    temperature: str
    weatherCode: int
    cloudCover: int


class HistoricalDayDescriptionRequest(BaseModel):
    dateLabel: str
    maxTemperature: str
    minTemperature: str
    averageHumidity: str
    averagePressure: str
    sunrise: Optional[str] = None
    sunset: Optional[str] = None
    hourlySnapshots: List[HistoricalHourlySnapshot] = Field(default_factory=list)


class ClimateDaySummaryRequest(BaseModel):
    dateLabel: str
    maxTemperature: str
    minTemperature: str
    averageHumidity: str
    averagePressure: str


class ClimateComparisonRequest(BaseModel):
    selectedDay: ClimateDaySummaryRequest
    comparisonDay: ClimateDaySummaryRequest
