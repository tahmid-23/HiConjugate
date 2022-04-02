package com.github.tahmid_23.hiconjugate.fragment.heck

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.github.tahmid_23.hiconjugate.R
import com.github.tahmid_23.hiconjugate.conjugation.Conjugation
import com.github.tahmid_23.hiconjugate.globals.random
import com.github.tahmid_23.hiconjugate.shaker.shake

private const val CONJUGATIONS = "conjugations"

private const val MILLIS_PER_QUESTION = 10000L

class HeckFragmentGame : Fragment() {

    private lateinit var conjugations: List<Conjugation>

    private lateinit var timeRemaining: TextView

    private lateinit var chosenConjugation: Conjugation

    private lateinit var heckQuestion: TextView

    private lateinit var heckVerb: TextView

    private lateinit var questionAnimation: AnimatorSet

    private lateinit var verbAnimation: AnimatorSet

    private lateinit var buttons: List<Button>

    private lateinit var handler: Handler

    private var millisRemaining = MILLIS_PER_QUESTION

    private var answeredQuestions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            conjugations = it.getParcelableArray(CONJUGATIONS)!!.asList().map { conjugation ->
                conjugation as Conjugation
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_heck_game, container, false)

        heckQuestion = view.findViewById(R.id.heck_question)
        heckVerb = view.findViewById(R.id.heck_verb)

        verbAnimation = shake(heckVerb, 0.5F, ObjectAnimator.INFINITE)
        questionAnimation = shake(heckQuestion, 0.5F, ObjectAnimator.INFINITE)

        timeRemaining = view.findViewById(R.id.heck_time_left)

        buttons = buildList {
            add(view.findViewById(R.id.button))
            add(view.findViewById(R.id.button2))
            add(view.findViewById(R.id.button3))
            add(view.findViewById(R.id.button4))
        }
        for (button in buttons) {
            button.setOnClickListener {
                val choice = button.text
                if (choice == chosenConjugation.conjugation) {
                    if (++answeredQuestions < 10) {
                        handler.removeCallbacksAndMessages(null)
                        handler.postDelayed(::tick, 100L)
                        refreshConjugations()
                    }
                    else {
                        finish(true)
                    }
                }
                else {
                    finish(false)
                }
            }
        }

        refreshConjugations()

        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(::tick, 100L)

        return view
    }

    override fun onStart() {
        super.onStart()
        timeRemaining.text = formatTime()
        handler.postDelayed(::tick, 100L)
    }

    private fun tick() {
        millisRemaining -= 100
        timeRemaining.text = formatTime()
        if (millisRemaining == 0L) {
            finish(false)
        }
        else {
            handler.postDelayed(::tick, 100L)
        }
    }

    private fun formatTime(): String {
        val seconds = millisRemaining / 1000
        val tenthSeconds = (millisRemaining - 1000 * seconds) / 100

        return "$seconds.$tenthSeconds"
    }

    private fun refreshConjugations() {
        val choices = arrayOf(
            conjugations.random(random),
            conjugations.random(random),
            conjugations.random(random),
            conjugations.random(random)
        )

        for (x in 0 until 4) {
            buttons[x].text = choices[x].conjugation
        }

        chosenConjugation = choices.random(random)
        heckVerb.text = "${chosenConjugation.verb} (${chosenConjugation.group} ${chosenConjugation.type})"
        heckQuestion.text = ""
        if (chosenConjugation.particle != null) {
            heckQuestion.append(chosenConjugation.particle)
        }
        if (chosenConjugation.subject != null) {
            heckQuestion.append(chosenConjugation.subject)
        }
        if (chosenConjugation.aux != null) {
            heckQuestion.append(chosenConjugation.aux)
        }
        heckQuestion.append("______")
    }

    private fun finish(success: Boolean) {
        handler.removeCallbacksAndMessages(null)
        verbAnimation.cancel()
        questionAnimation.cancel()

        val action = HeckFragmentGameDirections.actionHeckFragmentGameToFragmentHeckTwo(false)
        findNavController().navigate(action)
    }

}