package com.example.weathersimulator.ui.screens.games.memory

object WeatherMemoryData {
    private val easyCards = listOf(
        WeatherMemoryCard(
            id = "sun",
            title = "Soare",
            icon = "☀️",
            category = "Vreme stabila",
            explanation = "Soarele incalzeste suprafata Pamantului si pune in miscare multe procese meteo, de la brize locale pana la evaporarea apei.",
            difficulty = "Usor"
        ),
        WeatherMemoryCard(
            id = "white_cloud",
            title = "Nor alb",
            icon = "☁️",
            category = "Nori",
            explanation = "Norii albi apar cand vaporii de apa se condenseaza in picaturi foarte mici. De multe ori indica o atmosfera relativ stabila.",
            difficulty = "Usor"
        ),
        WeatherMemoryCard(
            id = "rain",
            title = "Ploaie",
            icon = "🌧️",
            category = "Precipitatii",
            explanation = "Ploaia apare cand picaturile din nori devin suficient de grele incat cad spre sol sub forma de precipitatii lichide.",
            difficulty = "Usor"
        ),
        WeatherMemoryCard(
            id = "snow",
            title = "Ninsoare",
            icon = "❄️",
            category = "Precipitatii",
            explanation = "Ninsoarea apare cand cristalele de gheata se formeaza in nori si ajung la sol fara sa se topeasca pe drum.",
            difficulty = "Usor"
        ),
        WeatherMemoryCard(
            id = "fog",
            title = "Ceata",
            icon = "🌫️",
            category = "Vizibilitate",
            explanation = "Ceata apare cand aerul de langa sol este foarte umed, se raceste si vizibilitatea scade mult.",
            difficulty = "Usor"
        ),
        WeatherMemoryCard(
            id = "rainbow",
            title = "Curcubeu",
            icon = "🌈",
            category = "Optica atmosferica",
            explanation = "Curcubeul apare cand lumina soarelui este refractata si reflectata in picaturile de ploaie.",
            difficulty = "Usor"
        )
    )

    private val mediumCards = listOf(
        WeatherMemoryCard(
            id = "cumulus",
            title = "Cumulus",
            icon = "☁️",
            category = "Nori",
            explanation = "Norii cumulus sunt nori albi, pufosi, care apar de obicei pe vreme stabila. Daca se dezvolta vertical, pot anunta instabilitate.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "cirrus",
            title = "Cirrus",
            icon = "〰️",
            category = "Nori",
            explanation = "Norii cirrus sunt subtiri si se afla la altitudini mari. Ei pot indica schimbari de vreme in urmatoarele ore.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "stratus",
            title = "Stratus",
            icon = "▤",
            category = "Nori",
            explanation = "Norii stratus formeaza un strat jos si uniform, asemanator unei paturi gri, si pot aduce burnita sau vreme mohorata.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "storm",
            title = "Furtuna",
            icon = "⛈️",
            category = "Vreme severa",
            explanation = "Furtunile apar cand aerul cald si umed se ridica rapid si formeaza nori puternici, cu ploaie intensa, vant si descarcari electrice.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "pressure",
            title = "Presiune atmosferica",
            icon = "🧭",
            category = "Atmosfera",
            explanation = "Presiunea atmosferica este greutatea aerului de deasupra noastra. Scaderile rapide pot anunta vreme instabila.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "humidity",
            title = "Umiditate",
            icon = "💧",
            category = "Atmosfera",
            explanation = "Umiditatea arata cata apa sub forma de vapori exista in aer. Cand este ridicata, aerul se simte mai apasator.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "wind",
            title = "Vant",
            icon = "🌬️",
            category = "Miscarea aerului",
            explanation = "Vantul apare cand aerul se deplaseaza din zone cu presiune mai mare spre zone cu presiune mai mica.",
            difficulty = "Mediu"
        ),
        WeatherMemoryCard(
            id = "dew_point",
            title = "Punct de roua",
            icon = "💦",
            category = "Umiditate",
            explanation = "Punctul de roua este temperatura la care vaporii de apa incep sa se condenseze. Este important pentru ceata, roua si confort termic.",
            difficulty = "Mediu"
        )
    )

    private val advancedCards = listOf(
        WeatherMemoryCard(
            id = "cumulonimbus",
            title = "Cumulonimbus",
            icon = "⛈️",
            category = "Nori de furtuna",
            explanation = "Cumulonimbus este un nor foarte dezvoltat vertical, asociat cu furtuni puternice, averse, grindina si fulgere.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "cold_front",
            title = "Front rece",
            icon = "🔵",
            category = "Fronturi",
            explanation = "Un front rece apare cand o masa de aer rece impinge aerul cald in sus. Poate aduce ploi intense si scaderea temperaturii.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "warm_front",
            title = "Front cald",
            icon = "🔴",
            category = "Fronturi",
            explanation = "Un front cald apare cand aerul cald aluneca peste aerul rece. De obicei aduce nori intinsi si precipitatii de durata.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "cyclone",
            title = "Ciclon",
            icon = "🌀",
            category = "Sisteme meteo",
            explanation = "Ciclonul este o zona de presiune joasa in jurul careia aerul se roteste si urca, favorizand norii si precipitatiile.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "anticyclone",
            title = "Anticiclon",
            icon = "🔆",
            category = "Sisteme meteo",
            explanation = "Anticiclonul este o zona de presiune ridicata, unde aerul coboara. Deseori aduce vreme calma si cer mai senin.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "instability",
            title = "Instabilitate atmosferica",
            icon = "⚡",
            category = "Dinamica atmosferei",
            explanation = "Instabilitatea atmosferica apare cand aerul cald poate urca rapid. Ea sustine dezvoltarea norilor verticali si a furtunilor.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "convection",
            title = "Convectie",
            icon = "↟",
            category = "Dinamica atmosferei",
            explanation = "Convectia este miscarea ascendenta a aerului cald. Este unul dintre motoarele norilor cumulus si cumulonimbus.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "wind_shear",
            title = "Forfecare a vantului",
            icon = "↔",
            category = "Vreme severa",
            explanation = "Forfecarea vantului inseamna schimbarea vitezei sau directiei vantului cu altitudinea. Poate organiza furtunile puternice.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "occlusion",
            title = "Front oclus",
            icon = "🟣",
            category = "Fronturi",
            explanation = "Frontul oclus apare cand un front rece ajunge din urma un front cald. Este frecvent in cicloanele mature.",
            difficulty = "Avansat"
        ),
        WeatherMemoryCard(
            id = "inversion",
            title = "Inversiune termica",
            icon = "⇅",
            category = "Stratificare",
            explanation = "Inversiunea termica apare cand temperatura creste cu altitudinea pe un strat, blocand amestecul aerului si favorizand ceata sau poluarea.",
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
