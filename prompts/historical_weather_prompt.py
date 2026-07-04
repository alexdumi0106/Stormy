def build_historical_day_description_prompt(data):
    return f"""
Esti asistentul AI al aplicatiei Weather Simulator AI.

Scrie o descriere foarte scurta, in limba romana, pentru ziua meteo selectata.
Raspunsul trebuie sa fie complet si sa nu se opreasca in mijlocul unei propozitii.
Incheie obligatoriu raspunsul cu punct.
Maximum 25 de cuvinte.
Scrie o singura propozitie.
Mentioneaza doar aspectul general al vremii, temperatura maxima si minima.
Nu mentiona umiditatea, rasaritul, apusul sau presiunea.
Nu descrie evolutia pe ore.
Nu inventa date. Foloseste doar valorile primite.
Nu saluta si nu te prezenta.

Data: {data.dateLabel}
Temperatura maxima: {data.maxTemperature}
Temperatura minima: {data.minTemperature}
Umiditate medie: {data.averageHumidity}
Presiune medie: {data.averagePressure}
Rasarit: {data.sunrise}
Apus: {data.sunset}
Evolutie pe ore reprezentative: {data.hourlySnapshots}
""".strip()


def build_climate_comparison_prompt(data):
    return f"""
Esti asistentul AI al aplicatiei Weather Simulator AI.

Compara foarte pe scurt doua zile meteo, in limba romana.
Raspunsul trebuie sa fie complet si sa nu se opreasca in mijlocul unei propozitii.
Incheie obligatoriu raspunsul cu punct.
Maximum 45 de cuvinte.
Scrie un singur paragraf.
Prima propozitie compara temperatura si umiditatea.
A doua propozitie spune care zi a parut mai calda, mai umeda sau mai stabila.
Foloseste doar datele primite. Nu inventa valori.
Nu saluta si nu te prezenta.

Zi selectata:
Data: {data.selectedDay.dateLabel}
Max: {data.selectedDay.maxTemperature}
Min: {data.selectedDay.minTemperature}
Umiditate medie: {data.selectedDay.averageHumidity}
Presiune medie: {data.selectedDay.averagePressure}

Zi de comparatie:
Data: {data.comparisonDay.dateLabel}
Max: {data.comparisonDay.maxTemperature}
Min: {data.comparisonDay.minTemperature}
Umiditate medie: {data.comparisonDay.averageHumidity}
Presiune medie: {data.comparisonDay.averagePressure}
""".strip()
