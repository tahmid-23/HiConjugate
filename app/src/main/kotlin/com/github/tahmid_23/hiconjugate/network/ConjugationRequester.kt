package com.github.tahmid_23.hiconjugate.network

import com.github.tahmid_23.hiconjugate.conjugation.Conjugation
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * I don't like hackathons.
 * They encourage bad design.
 */
class ConjugationRequester(private val client: HttpClient) {

    private val conjugationGroups = buildList {
        for (x in 1..8) {
            add("Indicatif")
        }
        for (x in 1..4) {
            add("Subjonctif")
        }
        for (x in 1..3) {
            add("Conditonnel")
        }
        for (x in 1..3) {
            add("Participe")
        }
        for (x in 1..2) {
            add("Impératif")
        }
    }

    suspend fun getVerb(verbName: String): Collection<Conjugation> {
        val conjugations = mutableListOf<Conjugation>()

        val url = "https://conjugator.reverso.net/conjugation-french-verb-$verbName.html"
        val document = Jsoup.parse(client.get(url).bodyAsText())

        val translation = document.getElementById("list-translations")!!.child(1).text()
        val rows = document.getElementById("ch_divSimple")!!.child(0).getElementsByClass("word-wrap-row")
        var i = 0
        mainLoop@ for (row in rows) {
            for (col in row.getElementsByClass("wrap-three-col")) {
                val sub = col.getElementsByClass("blue-box-wrap")[0]
                val type = sub.child(0)
                val list = sub.child(1)

                var conjugationList: List<Element> = list.children()
                if (conjugationList.size > 6) {
                    conjugationList = conjugationList.subList(0, 6)
                }
                for (conjugation in conjugationList) {
                    var particle: String? = null
                    val particleElements = conjugation.getElementsByClass("particletxt")
                    if (particleElements.isNotEmpty()) {
                        particle = particleElements[0].ownText()
                        if (!particleElements[0].ownText().endsWith("'")) {
                            particle += " "
                        }
                    }
                    var subject: String? = null
                    val grayElements = conjugation.getElementsByClass("graytxt")
                    if (grayElements.isNotEmpty()) {
                        subject = grayElements[0].ownText().trim()
                        if (!grayElements[0].ownText().endsWith("'")) {
                            subject += " "
                        }
                    }
                    var aux: String? = null
                    val auxElements = conjugation.getElementsByClass("auxgraytxt")
                    if (auxElements.isNotEmpty()) {
                        aux = auxElements[0].ownText() + " "
                    }
                    var verb: String? = null
                    val verbElements = conjugation.getElementsByClass("verbtxt")
                    if (verbElements.isNotEmpty()) {
                        verb = verbElements[0].ownText()
                    }

                    conjugations.add(Conjugation(
                        verbName,
                        translation,
                        conjugationGroups[i],
                        type.ownText(),
                        particle,
                        subject,
                        aux,
                        verb!!.trim()
                    ))
                }


                if (++i >= 20) {
                    break@mainLoop
                }
            }
        }

        return conjugations
    }

}