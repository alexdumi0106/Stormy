package com.example.weathersimulator.data.repository

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

data class WeatherQuizQuestion(
    val id: String,
    val question: String,
    val answers: List<String>,
    val correctIndex: Int,
    val category: String,
    val difficulty: String,
    val explanation: String
)

data class WeatherQuizQuestionSet(
    val questions: List<WeatherQuizQuestion>,
    val loadedFromFallback: Boolean
)

@Singleton
class WeatherQuizRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val prefs = context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)

    suspend fun loadRandomQuestions(count: Int): WeatherQuizQuestionSet {
        val firebaseQuestions = runCatching {
            firestore.collection(CollectionName)
                .get()
                .await()
                .documents
                .mapNotNull { it.toWeatherQuizQuestion() }
        }.getOrDefault(emptyList())

        val pool = when {
            firebaseQuestions.size >= count -> firebaseQuestions
            firebaseQuestions.isEmpty() -> fallbackQuestions
            else -> {
                val firebaseIds = firebaseQuestions.map { it.id }.toSet()
                firebaseQuestions + fallbackQuestions.filter { it.id !in firebaseIds }
            }
        }
        val selected = selectWithoutRecentRepeats(pool, count.coerceIn(1, 10))

        return WeatherQuizQuestionSet(
            questions = selected,
            loadedFromFallback = firebaseQuestions.size < count
        )
    }

    private fun selectWithoutRecentRepeats(
        allQuestions: List<WeatherQuizQuestion>,
        count: Int
    ): List<WeatherQuizQuestion> {
        if (allQuestions.isEmpty()) return emptyList()

        val usedIds = prefs.getStringSet(UsedQuestionIdsKey, emptySet()).orEmpty().toSet()
        val unusedQuestions = allQuestions.filter { it.id !in usedIds }
        val selectionPool = if (unusedQuestions.size >= count) {
            unusedQuestions
        } else {
            prefs.edit().remove(UsedQuestionIdsKey).apply()
            allQuestions
        }

        val selectedQuestions = selectionPool
            .shuffled()
            .take(count.coerceAtMost(selectionPool.size))

        rememberUsedQuestions(selectedQuestions.map { it.id })
        return selectedQuestions
    }

    private fun rememberUsedQuestions(questionIds: List<String>) {
        val currentIds = prefs.getStringSet(UsedQuestionIdsKey, emptySet()).orEmpty().toMutableSet()
        currentIds.addAll(questionIds)
        prefs.edit()
            .putStringSet(UsedQuestionIdsKey, currentIds)
            .apply()
    }

    private fun DocumentSnapshot.toWeatherQuizQuestion(): WeatherQuizQuestion? {
        val question = getString("question")?.trim().orEmpty()
        val answers = (get("answers") as? List<*>)
            ?.mapNotNull { it as? String }
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()
        val correctIndex = getLong("correctIndex")?.toInt()
            ?: (get("correctIndex") as? Number)?.toInt()
            ?: -1
        val explanation = getString("explanation")?.trim().orEmpty()

        if (
            question.isBlank() ||
            answers.size != RequiredAnswerCount ||
            correctIndex !in answers.indices ||
            explanation.isBlank()
        ) {
            return null
        }

        return WeatherQuizQuestion(
            id = id,
            question = question,
            answers = answers,
            correctIndex = correctIndex,
            category = getString("category")?.trim().orEmpty(),
            difficulty = getString("difficulty")?.trim().orEmpty(),
            explanation = explanation
        )
    }

    private companion object {
        const val CollectionName = "quiz_questions"
        const val PrefsName = "weather_quiz_prefs"
        const val UsedQuestionIdsKey = "used_question_ids"
        const val RequiredAnswerCount = 3

        val fallbackQuestions = listOf(
            WeatherQuizQuestion(
                id = "fallback_pressure",
                question = "Ce este presiunea atmosferică?",
                answers = listOf(
                    "Greutatea aerului asupra suprafeței Pământului",
                    "Cantitatea de ploaie dintr-o zi",
                    "Viteza vântului"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Presiunea atmosferică reprezintă forța exercitată de coloana de aer asupra unei suprafețe."
            ),
            WeatherQuizQuestion(
                id = "fallback_atmosphere_gas",
                question = "Care este principalul gaz din atmosfera Pământului?",
                answers = listOf(
                    "Oxigen",
                    "Azot",
                    "Dioxid de carbon"
                ),
                correctIndex = 1,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Azotul reprezintă cea mai mare parte din atmosfera terestră."
            ),
            WeatherQuizQuestion(
                id = "fallback_thermometer",
                question = "Ce instrument măsoară temperatura aerului?",
                answers = listOf(
                    "Barometru",
                    "Termometru",
                    "Anemometru"
                ),
                correctIndex = 1,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Termometrul este instrumentul folosit pentru măsurarea temperaturii."
            ),
            WeatherQuizQuestion(
                id = "fallback_rainbow",
                question = "Ce fenomen produce curcubeul?",
                answers = listOf(
                    "Refracția și reflexia luminii în picăturile de apă",
                    "Umbra norilor",
                    "Poluarea atmosferică"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Curcubeul apare când lumina solară este refractată și reflectată în picăturile de ploaie."
            ),
            WeatherQuizQuestion(
                id = "fallback_humidity_100",
                question = "Ce indică o umiditate relativă de 100%?",
                answers = listOf(
                    "Aer foarte uscat",
                    "Aer saturat cu vapori de apă",
                    "Vânt foarte puternic"
                ),
                correctIndex = 1,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "La 100% umiditate relativă, aerul este saturat cu vapori de apă."
            ),
            WeatherQuizQuestion(
                id = "fallback_cumulonimbus",
                question = "Care nor este asociat cel mai des cu furtunile?",
                answers = listOf(
                    "Cirrus",
                    "Stratus",
                    "Cumulonimbus"
                ),
                correctIndex = 2,
                category = "nori",
                difficulty = "easy",
                explanation = "Norii cumulonimbus se dezvoltă vertical și pot produce furtuni, fulgere și grindină."
            ),
            WeatherQuizQuestion(
                id = "fallback_anemometer",
                question = "Ce instrument măsoară viteza vântului?",
                answers = listOf(
                    "Anemometru",
                    "Termometru",
                    "Pluviometru"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Anemometrul este instrumentul utilizat pentru măsurarea vitezei vântului."
            ),
            WeatherQuizQuestion(
                id = "fallback_rain",
                question = "Cum se numește apa care cade din atmosferă sub formă lichidă?",
                answers = listOf(
                    "Ninsoare",
                    "Ploaie",
                    "Grindină"
                ),
                correctIndex = 1,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Ploaia este forma lichidă a precipitațiilor."
            ),
            WeatherQuizQuestion(
                id = "fallback_cirrus",
                question = "Ce tip de nor este subțire și fibros?",
                answers = listOf(
                    "Cirrus",
                    "Cumulus",
                    "Nimbostratus"
                ),
                correctIndex = 0,
                category = "nori",
                difficulty = "easy",
                explanation = "Norii cirrus sunt subțiri, fibroși și apar la altitudini mari."
            ),
            WeatherQuizQuestion(
                id = "fallback_evaporation",
                question = "Cum se numește fenomenul prin care apa se transformă în vapori?",
                answers = listOf(
                    "Condensare",
                    "Evaporare",
                    "Sublimare"
                ),
                correctIndex = 1,
                category = "climatologie",
                difficulty = "easy",
                explanation = "Evaporarea este procesul prin care apa lichidă se transformă în vapori."
            ),
            WeatherQuizQuestion(
                id = "fallback_anticyclone",
                question = "Ce reprezintă un anticiclon?",
                answers = listOf(
                    "Zonă cu presiune ridicată",
                    "Zonă cu presiune scăzută",
                    "Un tip de nor"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "medium",
                explanation = "Anticiclonii sunt asociați de obicei cu vreme stabilă și cer mai senin."
            ),
            WeatherQuizQuestion(
                id = "fallback_cyclone",
                question = "Ce reprezintă un ciclon?",
                answers = listOf(
                    "Zonă cu presiune ridicată",
                    "Zonă cu presiune scăzută",
                    "O formă de precipitație"
                ),
                correctIndex = 1,
                category = "meteorologie",
                difficulty = "medium",
                explanation = "Ciclonii sunt zone cu presiune scăzută și favorizează formarea norilor și precipitațiilor."
            ),
            WeatherQuizQuestion(
                id = "fallback_fog",
                question = "Ce este ceața?",
                answers = listOf(
                    "Un nor aflat la nivelul solului",
                    "O furtună de praf",
                    "O ploaie slabă"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Ceața este formată din picături foarte mici de apă suspendate aproape de sol."
            ),
            WeatherQuizQuestion(
                id = "fallback_tornado_scale",
                question = "Ce scară este utilizată pentru clasificarea tornadelor?",
                answers = listOf(
                    "Richter",
                    "Fujita Îmbunătățită",
                    "Beaufort"
                ),
                correctIndex = 1,
                category = "fenomene_extreme",
                difficulty = "medium",
                explanation = "Scara Fujita Îmbunătățită clasifică tornadele în funcție de daunele produse."
            ),
            WeatherQuizQuestion(
                id = "fallback_rain_gauge",
                question = "Ce măsoară pluviometrul?",
                answers = listOf(
                    "Cantitatea de precipitații",
                    "Presiunea atmosferică",
                    "Umiditatea aerului"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Pluviometrul măsoară cantitatea de precipitații căzută într-o anumită perioadă."
            ),
            WeatherQuizQuestion(
                id = "fallback_hail",
                question = "Ce este grindina?",
                answers = listOf(
                    "Picături de apă foarte reci",
                    "Bucăți de gheață formate în norii de furtună",
                    "Cristale de zăpadă"
                ),
                correctIndex = 1,
                category = "fenomene_extreme",
                difficulty = "easy",
                explanation = "Grindina se formează în norii de furtună, mai ales în cumulonimbus."
            ),
            WeatherQuizQuestion(
                id = "fallback_sun_energy",
                question = "Care este sursa principală de energie a vremii pe Pământ?",
                answers = listOf(
                    "Luna",
                    "Soarele",
                    "Vântul"
                ),
                correctIndex = 1,
                category = "climatologie",
                difficulty = "easy",
                explanation = "Soarele furnizează energia care pune în mișcare procesele atmosferice."
            ),
            WeatherQuizQuestion(
                id = "fallback_condensation",
                question = "Cum se numește procesul prin care vaporii de apă devin picături?",
                answers = listOf(
                    "Evaporare",
                    "Condensare",
                    "Topire"
                ),
                correctIndex = 1,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Condensarea este procesul prin care vaporii de apă se transformă în picături și pot forma nori."
            ),
            WeatherQuizQuestion(
                id = "fallback_pressure_unit",
                question = "Care este unitatea uzuală pentru presiunea atmosferică?",
                answers = listOf(
                    "hPa",
                    "km/h",
                    "Grade Celsius"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "medium",
                explanation = "Presiunea atmosferică este exprimată frecvent în hectopascali, adică hPa."
            ),
            WeatherQuizQuestion(
                id = "fallback_cold_front",
                question = "Ce tip de front aduce adesea furtuni puternice?",
                answers = listOf(
                    "Front rece",
                    "Front cald",
                    "Front staționar"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "medium",
                explanation = "Fronturile reci pot ridica rapid aerul cald, favorizând averse și furtuni."
            ),
            WeatherQuizQuestion(
                id = "fallback_troposphere_weather",
                question = "Cum se numește stratul atmosferic în care se produce vremea?",
                answers = listOf(
                    "Stratosfera",
                    "Troposfera",
                    "Mezosfera"
                ),
                correctIndex = 1,
                category = "meteorologie",
                difficulty = "medium",
                explanation = "Majoritatea fenomenelor meteorologice au loc în troposferă."
            ),
            WeatherQuizQuestion(
                id = "fallback_pressure_drop",
                question = "Ce poate indica o presiune atmosferică în scădere?",
                answers = listOf(
                    "Posibilă deteriorare a vremii",
                    "Cer complet senin garantat",
                    "Lipsa vântului"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "medium",
                explanation = "Presiunea în scădere poate indica apropierea unui sistem depresionar și vreme instabilă."
            ),
            WeatherQuizQuestion(
                id = "fallback_heat_wave",
                question = "Ce este un val de căldură?",
                answers = listOf(
                    "Perioada cu temperaturi mult peste normal",
                    "O furtună tropicală",
                    "O perioadă cu umiditate scăzută"
                ),
                correctIndex = 0,
                category = "climatologie",
                difficulty = "easy",
                explanation = "Valul de căldură este o perioadă cu temperaturi ridicate, peste valorile normale ale zonei."
            ),
            WeatherQuizQuestion(
                id = "fallback_convection",
                question = "Ce fenomen apare atunci când aerul cald urcă?",
                answers = listOf(
                    "Convecție",
                    "Reflexie",
                    "Eroziune"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "medium",
                explanation = "Convecția este mișcarea aerului cald în sus și are un rol important în formarea norilor."
            ),
            WeatherQuizQuestion(
                id = "fallback_snow",
                question = "Ce tip de precipitație este format din cristale de gheață?",
                answers = listOf(
                    "Ploaia",
                    "Ninsoarea",
                    "Burniță"
                ),
                correctIndex = 1,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Ninsoarea este formată din cristale de gheață care cad sub formă de fulgi."
            ),
            WeatherQuizQuestion(
                id = "fallback_lowest_layer",
                question = "Care este cel mai apropiat strat al atmosferei de suprafața Pământului?",
                answers = listOf(
                    "Troposfera",
                    "Stratosfera",
                    "Termosfera"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Troposfera începe de la nivelul solului și conține majoritatea fenomenelor meteo."
            ),
            WeatherQuizQuestion(
                id = "fallback_greenhouse_effect",
                question = "Ce este efectul de seră?",
                answers = listOf(
                    "Procesul prin care anumite gaze rețin căldura în atmosferă",
                    "Formarea norilor",
                    "Măsurarea temperaturii"
                ),
                correctIndex = 0,
                category = "climatologie",
                difficulty = "medium",
                explanation = "Efectul de seră este procesul prin care anumite gaze rețin o parte din căldura radiată de Pământ."
            ),
            WeatherQuizQuestion(
                id = "fallback_lightning",
                question = "Ce fenomen meteo produce descarcari electrice?",
                answers = listOf(
                    "Ceață",
                    "Furtună",
                    "Burniță"
                ),
                correctIndex = 1,
                category = "fenomene_extreme",
                difficulty = "easy",
                explanation = "Fulgerele apar în timpul furtunilor electrice."
            ),
            WeatherQuizQuestion(
                id = "fallback_forecast",
                question = "Ce înseamnă prognoza meteo?",
                answers = listOf(
                    "Estimarea condițiilor atmosferice viitoare",
                    "Măsurarea temperaturii curente",
                    "Analiza solului"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Prognoza meteo folosește observații și modele pentru a estima evoluția vremii."
            ),
            WeatherQuizQuestion(
                id = "fallback_freezing_point",
                question = "Care este temperatura la care apa îngheață la presiune normală?",
                answers = listOf(
                    "0 grade Celsius",
                    "10 grade Celsius",
                    "-10 grade Celsius"
                ),
                correctIndex = 0,
                category = "meteorologie",
                difficulty = "easy",
                explanation = "Apa îngheață la 0 grade Celsius în condiții normale de presiune."
            )
        )
    }
}
