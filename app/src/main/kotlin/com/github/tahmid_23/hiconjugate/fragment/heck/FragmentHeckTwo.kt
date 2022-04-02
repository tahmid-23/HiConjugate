package com.github.tahmid_23.hiconjugate.fragment.heck

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.edit
import com.github.tahmid_23.hiconjugate.R
import com.github.tahmid_23.hiconjugate.shaker.REPEAT_COUNT
import com.github.tahmid_23.hiconjugate.shaker.REPEAT_LENGTH
import com.github.tahmid_23.hiconjugate.shaker.shake
import kotlin.properties.Delegates
import kotlin.system.exitProcess

private const val SUCCESS = "success"

class FragmentHeckTwo : Fragment() {

    private var success by Delegates.notNull<Boolean>()

    private lateinit var badSfx: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            success = it.getBoolean(SUCCESS)
        }
        badSfx = MediaPlayer.create(requireContext().applicationContext, R.raw.bad_sfx)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_heck_one, container, false)

        val messageView = view.findViewById<TextView>(R.id.message_of_heck)
        if (success) {
            messageView.text = "You were lucky this time."
            requireActivity()
                .getSharedPreferences("conjugate_credits", Context.MODE_PRIVATE)
                .edit(true) {
                    putInt("credits", 1000)
                }
        }
        else {
            messageView.text = "I KNEW you were too weak."
        }
        shake(messageView)

        return view
    }

    override fun onStart() {
        super.onStart()
        badSfx.start()
        Handler(Looper.getMainLooper()).postDelayed({
            finishAffinity(requireActivity())
            exitProcess(0)
        }, REPEAT_COUNT * REPEAT_LENGTH)
    }

    override fun onDestroy() {
        super.onDestroy()
        badSfx.release()
    }

    companion object {
        @JvmStatic
        fun newInstance(success: Boolean) =
            FragmentHeckOne().apply {
                arguments = Bundle().apply {
                    putBoolean(SUCCESS, success)
                }
            }
    }

}