package com.example.weathersimulator.ui.screens.games.memory

object WeatherMemoryData {
    private val easyCards = listOf(
        WeatherMemoryCard(
            id = "sun",
            title = "Soare",
            icon = "☀️",
            category = "Vreme stabilă",
            explanation = "Soarele încălzește suprafața Pământului și pune în mișcare multe procese meteo, de la brize locale până la evaporarea apei.",
            difficulty = "Ușor"
        ),
        WeatherMemoryCard(
            id = "white_cloud",
            title = "Nor alb",
            icon = "☁️",
            category = "Nori",
            explanation = "Norii albi apar când vaporii de apă se condensează în picături foarte mici. De multe ori indică o atmosferă relativ stabilă.",
            difficulty = "Ușor"
        ),
        WeatherMemoryCard(
            id = "rain",
            title = "Ploaie",
            icon = "🌧️",
            category = "Precipitații",
            explanation = "Ploaia apare când picăturile din nori devin suficient de grele încât cad spre sol sub formă de precipitații lichide.",
            difficulty = "Ușor"
        ),
        WeatherMemoryCard(
            id = "snow",
            title = "Ninsoare",
            icon = "❄️",
            category = "Precipitații",
            explanation = "Ninsoarea apare când cristalele de gheață se formează în nori și ajung la sol fără să se topească pe drum.",
            difficulty = "Ușor"
        ),
        WeatherMemoryCard(
            id = "fog",
            title = "Ceață",
            icon = "🌫️",
            category = "Vizibilitate",
            explanation = "Ceața apare când aerul de lângă sol este foarte umed, se răcește și vizibilitatea scade mult.",
            difficulty = "Ușor"
        ),
        WeatherMemoryCard(
            id = "rainbow",
            title = "Curcubeu",
            icon = "🌈",
            category = "Optică atmosferică",
            explanation = "Curcubeul apare când lumina soarelui este refractată și reflectată în picăturile de ploaie.",
            difficulty = "Ușor"
        )
    )

    private val mediumCards = listOf(
        WeatherMemoryCard(
            id = "cumulus",
            title = "Cumulus",
            icon = "☁️",
            category = "Nori",
            explanation = "Norii cumulus sunt nori albi, pufoși, care apar de obicei pe vreme stabilă. Dacă se dezvoltă vertical, pot anunța instabilitate.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "cirrus",
            title = "Cirrus",
            icon = "〰️",
            category = "Nori",
            explanation = "Norii cirrus sunt subțiri și se află la altitudini mari. Ei pot indica schimbări de vreme în următoarele ore.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "stratus",
            title = "Stratus",
            icon = "▤",
            category = "Nori",
            explanation = "Norii stratus formează un strat jos și uniform, asemănător unei pături gri, și pot aduce burniță sau vreme mohorâtă.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "storm",
            title = "Furtună",
            icon = "⛈️",
            category = "Vreme severă",
            explanation = "Furtunile apar când aerul cald și umed se ridică rapid și formează nori puternici, cu ploaie intensă, vânt și descărcări electrice.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "pressure",
            title = "Presiune atmosferică",
            icon = "🧭",
            category = "Atmosfera",
            explanation = "Presiunea atmosferică este greutatea aerului de deasupra noastră. Scăderile rapide pot anunța vreme instabilă.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "humidity",
            title = "Umiditate",
            icon = "💧",
            category = "Atmosfera",
            explanation = "Umiditatea arată câtă apă sub formă de vapori există în aer. Când este ridicată, aerul se simte mai apăsător.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "wind",
            title = "Vânt",
            icon = "🌬️",
            category = "Mișcarea aerului",
            explanation = "Vântul apare când aerul se deplasează din zone cu presiune mai mare spre zone cu presiune mai mică.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "dew_point",
            title = "Punct de rouă",
            icon = "💦",
            category = "Umiditate",
            explanation = "Punctul de rouă este temperatura la care vaporii de apă încep să se condenseze. Este important pentru ceață, rouă și confort termic.",
            difficulty = "Mediu"
        )
    )

    private val advancedCards = listOf(
        WeatherMemoryCard(
            id = "cumulonimbus",
            title = "Cumulonimbus",
            icon = "⛈️",
            category = "Nori de furtună",
            explanation = "Cumulonimbus este un nor foarte dezvoltat vertical, asociat cu furtuni puternice, averse, grindină și fulgere.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "cold_front",
            title = "Front rece",
            icon = "🔵",
            category = "Fronturi",
            explanation = "Un front rece apare când o masă de aer rece împinge aerul cald în sus. Poate aduce ploi intense și scăderea temperaturii.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "warm_front",
            title = "Front cald",
            icon = "🔴",
            category = "Fronturi",
            explanation = "Un front cald apare când aerul cald alunecă peste aerul rece. De obicei aduce nori întinși și precipitații de durată.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "cyclone",
            title = "Ciclon",
            icon = "🌀",
            category = "Sisteme meteo",
            explanation = "Ciclonul este o zonă de presiune joasă în jurul căreia aerul se rotește și urcă, favorizând norii și precipitațiile.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "anticyclone",
            title = "Anticiclon",
            icon = "🔆",
            category = "Sisteme meteo",
            explanation = "Anticiclonul este o zonă de presiune ridicată, unde aerul coboară. Deseori aduce vreme calmă și cer mai senin.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "instability",
            title = "Instabilitate atmosferică",
            icon = "⚡",
            category = "Dinamica atmosferei",
            explanation = "Instabilitatea atmosferică apare când aerul cald poate urca rapid. Ea susține dezvoltarea norilor verticali și a furtunilor.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "convection",
            title = "Convecție",
            icon = "↟",
            category = "Dinamica atmosferei",
            explanation = "Convecția este mișcarea ascendentă a aerului cald. Este unul dintre motoarele norilor cumulus și cumulonimbus.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "wind_shear",
            title = "Forfecare a vântului",
            icon = "↔",
            category = "Vreme severă",
            explanation = "Forfecarea vântului înseamnă schimbarea vitezei sau direcției vântului cu altitudinea. Poate organiza furtunile puternice.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "occlusion",
            title = "Front oclus",
            icon = "🟣",
            category = "Fronturi",
            explanation = "Frontul oclus apare când un front rece ajunge din urmă un front cald. Este frecvent în cicloanele mature.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "inversion",
            title = "Inversiune termică",
            icon = "⇅",
            category = "Stratificare",
            explanation = "Inversiunea termică apare când temperatura crește cu altitudinea pe un strat, blocând amestecul aerului și favorizând ceața sau poluarea.",
            difficulty = "Avansat"
        )
    )

    private val allCards = easyCards + mediumCards + advancedCards

    fun selectedCards(level: WeatherMemoryLevel): List<WeatherMemoryCard> {
        return when (level) {
            WeatherMemoryLevel.Easy -> easyCards
            WeatherMemoryLevel.Medium -> mediumCards
            WeatherMemoryLevel.Advanced -> advancedCards
        }.take(level.pairCount)
    }

    fun cardById(id: String): WeatherMemoryCard? {
        return allCards.firstOrNull { it.id == id }
    }
}
