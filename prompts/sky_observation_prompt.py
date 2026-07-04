def build_sky_observation_prompt(data):
    photo_level = (
        "excelent" if data.photographyScore >= 85 else
        "foarte bun" if data.photographyScore >= 70 else
        "bun" if data.photographyScore >= 50 else
        "modest"
    )

    rain_level = (
        "ridicat" if data.rainProbability >= 65 else
        "moderat" if data.rainProbability >= 35 else
        "redus"
    )

    storm_level = (
        "ridicat" if data.stormProbability >= 65 else
        "moderat" if data.stormProbability >= 35 else
        "redus"
    )

    cloud_strength = (
        "multi nori in cadru" if data.cloudRatio >= 0.45 else
        "nori putini sau dispersati" if data.cloudRatio < 0.18 else
        "acoperire noroasa partiala"
    )

    best_category = data.bestMoment if data.photographyScore >= 50 else "nicio categorie foto puternica"
    dramatic_level = (
        "ridicat" if data.dramaticCloudsScore >= 65 else
        "moderat" if data.dramaticCloudsScore >= 50 else
        "slab"
    )
    storm_photo_level = (
        "ridicat" if data.stormScore >= 65 else
        "moderat" if data.stormScore >= 50 else
        "slab"
    )

    return f"""
Esti un asistent meteo si foto. Primesti rezultate numerice extrase automat dintr-o fotografie a cerului.

Scrie o observatie scurta, naturala, in limba romana, de maximum 2 propozitii.
Reformuleaza interpretarea sigura de mai jos. Nu contrazice interpretarea sigura.

Reguli stricte:
- Foloseste doar datele de mai jos.
- Nu spune ca un fenomen domina cerul daca scorul lui este sub 50/100.
- Daca probabilitatea de furtuna este sub 20%, spune ca nu exista indicii vizuale importante de furtuna.
- Daca probabilitatea de ploaie este sub 20%, spune ca riscul de ploaie este redus.
- Daca tipul norilor este Cirrus, descrie nori subtiri/inalti/fini, nu nori grei sau de furtuna.
- Nu folosi bullet-uri, titluri sau formule de salut.

Interpretare sigura:
Tipul norilor este {data.cloudType}.
Acoperirea noroasa este: {cloud_strength}.
Riscul de ploaie este {rain_level}, cu {data.rainProbability}%.
Riscul de furtuna este {storm_level}, cu {data.stormProbability}%.
Potentialul foto este {photo_level}, cu scor {data.photographyScore}/100.
Categoria foto recomandata este: {best_category}.
Potentialul de nori dramatici este {dramatic_level}.
Potentialul de furtuna fotografica este {storm_photo_level}.

Date:
Tip nori: {data.cloudType}
Probabilitate ploaie: {data.rainProbability}%
Probabilitate furtuna: {data.stormProbability}%
Scor foto: {data.photographyScore}/100
Nivel foto interpretat: {photo_level}
Categorie foto recomandata: {best_category}
Nivel ploaie interpretat: {rain_level}
Nivel furtuna interpretat: {storm_level}
Acoperire nori interpretata: {cloud_strength}
Scor apus: {data.sunsetScore}/100
Scor rasarit: {data.sunriseScore}/100
Potential furtuni interpretat: {storm_photo_level}
Potential nori dramatici interpretat: {dramatic_level}
Scor ceata: {data.fogScore}/100
Lumina calda: {data.warmLightRatio}
Contrast: {data.contrast}
Luminozitate medie: {data.averageBrightness}
Saturatie medie: {data.averageSaturation}
Proportie cer albastru: {data.skyRatio}
Proportie nori: {data.cloudRatio}
Proportie nori intunecati: {data.darkCloudRatio}

Raspuns:
""".strip()
