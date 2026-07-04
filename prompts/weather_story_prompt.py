def build_weather_story_prompt(data):
    return f"""
Esti asistentul AI al aplicatiei Weather Simulator AI.

Scrie un rezumat natural al vremii in limba romana.
Stilul trebuie sa fie clar si prietenos.
Raspunsul trebuie sa aiba maximum 2 propozitii.
Foloseste doar datele primite. Nu inventa valori.
Nu saluta si nu te prezenta.

Date curente:
Temperatura: {data.temperature} C
Temperatura resimtita: {data.apparentTemperature} C
Umiditate: {data.humidity}%
Vant: {data.windSpeed} km/h
Presiune: {data.pressure} hPa
Cod meteo: {data.weatherCode}
Nori: {data.cloudCover}%

Urmatoarele ore:
Ore: {data.nextHours}
Temperaturi: {data.nextTemperatures}
Precipitatii: {data.nextPrecipitation}
Vant: {data.nextWindSpeed}
Coduri meteo: {data.nextWeatherCodes}

Prognoza zilnica:
Zile: {data.dailyDates}
Maxime: {data.dailyMaxTemperatures}
Minime: {data.dailyMinTemperatures}
Coduri meteo: {data.dailyWeatherCodes}
""".strip()
