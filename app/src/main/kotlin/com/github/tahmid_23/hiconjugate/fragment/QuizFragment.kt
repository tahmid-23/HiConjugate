package com.github.tahmid_23.hiconjugate.fragment

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.github.tahmid_23.hiconjugate.R
import com.github.tahmid_23.hiconjugate.conjugation.Conjugation
import com.github.tahmid_23.hiconjugate.globals.verbSet
import com.github.tahmid_23.hiconjugate.globals.random
import com.github.tahmid_23.hiconjugate.network.ConjugationRequester
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Hackathons: Making me make god-classes since 2022
 */
class QuizFragment : Fragment() {

    private val messagesOfHeck = listOf(
        "HOW DARE YOU\nGET A QUESTION WRONG?",
        "YOU'RE NOT\nSOARING TO NEW HEIGHTS.",
        "THIS WILL BE YOUR FAULT.",
        "TA FAMILLE.",
        "TES AMIS.",
        "JE LES AURAI.",
        "CRAINS-MOI.",
        "If you don't want to comply,\nWhat if I take away some of your credits?",
        "That's not enough\nto stop you?",
        "Etudiez pour l'interro sur le PP (de la dédicace à la fin du ch. 4).\n" +
                "\n" +
                "Pour le vocabulaire: Il y aura une section pour laquelle  il faudra faire une correspondance entre le mot en français et:  un exemple, un synoyme en français ou une traduction en anglais.",
        "Sorry, that was my homework.",
        "I'll get rid of ALL OF YOUR CREDITS.",
        "Nothing stops you.\nI'll just have to MAKE you learn."
    )

    private val requester = ConjugationRequester(HttpClient())

    private lateinit var verbName: TextView

    private lateinit var question: TextView

    private lateinit var creditCounter: TextView

    private lateinit var input: EditText

    private lateinit var inputResponse: TextView

    private lateinit var conjugation: Conjugation

    private lateinit var goodSfx: MediaPlayer

    private lateinit var okSfx: MediaPlayer

    private var stateCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContext = requireContext().applicationContext
        goodSfx = MediaPlayer.create(appContext, R.raw.good_sfx)
        okSfx = MediaPlayer.create(appContext, R.raw.ok_sfx)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        verbName = view.findViewById(R.id.verb_name)
        question = view.findViewById(R.id.quiz_question)
        creditCounter = view.findViewById(R.id.credit_counter)

        input = view.findViewById(R.id.answer_box)
        val submit = view.findViewById<Button>(R.id.answer_quiz)
        inputResponse = view.findViewById(R.id.input_response)
        input.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEND) {
                handleAction()
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
        submit.setOnClickListener {
            handleAction()
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            selectConjugation()
        }

        when (stateCounter) {
            8 -> changeCredits(-150)
            12 -> {
                val credits = requireActivity()
                    .getSharedPreferences("conjugate_credits", Context.MODE_PRIVATE)
                    .getInt("credits", 1000)
                if (credits > 0) { // why doesn't this work
                    changeCredits(-credits)
                }
                else {
                    syncCredits()
                }
            }
            13 -> {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    val verbSetCopy = verbSet.toMutableSet()
                    val chosenVerbs = buildSet {
                        for (x in 0..8) {
                            val nextVerb = verbSetCopy.random(random)
                            verbSetCopy.remove(nextVerb)

                            add(nextVerb)
                        }
                    }

                    val conjugations = buildList {
                        for (verb in chosenVerbs) {
                            addAll(requester.getVerb(verb))
                        }
                    }.toTypedArray()

                    launch(Dispatchers.Main) {
                        val action =
                            QuizFragmentDirections.actionQuizFragmentToHeckFragmentGame(conjugations)
                        findNavController().navigate(action)
                        stateCounter++
                    }
                }
            }
            14 -> {
                stateCounter = 0
                syncCredits()
            }
            else -> syncCredits()
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        goodSfx.release()
        okSfx.release()
    }

    private suspend fun selectConjugation() {
        val verb = verbSet.random(random)
        val conjugations = requester.getVerb(verb)
        val selected = conjugations.random(random)
        conjugation = selected

        coroutineScope {
            launch(Dispatchers.Main) {
                input.isFocusable = true
                input.isFocusableInTouchMode = true

                verbName.text = "$verb (${selected.group} ${selected.type})"
                question.text = ""
                if (selected.particle != null) {
                    question.append(createSpannable(selected.particle, Color.GRAY))
                }
                if (selected.subject != null) {
                    question.append(createSpannable(selected.subject, Color.GRAY))
                }
                if (selected.aux != null) {
                    question.append(createSpannable(selected.aux, Color.GRAY))
                }
                question.append(createSpannable("______", Color.CYAN))
            }
        }
    }

    private fun createSpannable(text: String, color: Int): Spannable {
        val spannable = SpannableString(text)
        spannable.setSpan(ForegroundColorSpan(color), 0, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }

    private fun handleAction() {
        val answer = input.text.toString().trim()
        input.text.clear()
        if (answer == conjugation.conjugation) {
            inputResponse.text = "Correct"
            inputResponse.setTextColor(Color.GREEN)
            if (!goodSfx.isPlaying) {
                goodSfx.start()
            }
            changeCredits(100)
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                selectConjugation()
            }
        }
        else {
            val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(requireContext().applicationContext)
            val allowEvilHaha = sharedPreferences.getBoolean("allow_evil_haha", true)

            if (allowEvilHaha) {
                castTheUserIntoTheDeepDepthsOfHeck()
            }
            else {
                normalFailure()
            }
        }
    }

    private fun syncCredits() {
        val creditCount = requireActivity()
            .getSharedPreferences("conjugate_credits", Context.MODE_PRIVATE)
            .getInt("credits", 1000)
        creditCounter.text = "Credits: $creditCount"
    }

    private fun changeCredits(delta: Int) {
        if (delta == 0) {
            return
        }

        val prefs = requireActivity()
            .getSharedPreferences("conjugate_credits", Context.MODE_PRIVATE)
        val creditCount = prefs.getInt("credits", 1000)
        val newCredits = creditCount + delta
        val animation = ValueAnimator.ofInt(creditCount, newCredits)
        animation.duration = (abs(delta) * 20).toLong()
        animation.addUpdateListener {
            creditCounter.text = "Credits: ${animation.animatedValue}"
        }
        val initialColor = creditCounter.currentTextColor
        animation.doOnEnd {
            prefs.edit(true) {
                putInt("credits", newCredits)
                input.isFocusable = true
                input.isFocusableInTouchMode = true
                creditCounter.setTextColor(initialColor)
            }
        }

        input.isFocusable = false
        input.isFocusableInTouchMode = false
        creditCounter.setTextColor(if (delta > 0) Color.GREEN else Color.RED)
        animation.start()
    }

    private fun castTheUserIntoTheDeepDepthsOfHeck() {
        val navController = findNavController()

        when (stateCounter++) {
            in messagesOfHeck.indices -> {
                val action = QuizFragmentDirections
                    .actionQuizFragmentToFragmentHeckOne(messagesOfHeck[stateCounter - 1])
                navController.navigate(action)
            }
            else -> {
                val action = QuizFragmentDirections
                    .actionQuizFragmentToFragmentHeckOne("you shouldn't be seeing this right now")
                navController.navigate(action)
            }
        }
    }

    private fun normalFailure() {
        inputResponse.text = "Incorrect"
        inputResponse.setTextColor(Color.RED)
        if (!okSfx.isPlaying) {
            okSfx.start()
        }
    }

}