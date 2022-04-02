package com.github.tahmid_23.hiconjugate.fragment.heck

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.github.tahmid_23.hiconjugate.R
import com.github.tahmid_23.hiconjugate.shaker.REPEAT_COUNT
import com.github.tahmid_23.hiconjugate.shaker.REPEAT_LENGTH
import com.github.tahmid_23.hiconjugate.shaker.shake

private const val MESSAGE = "message"

class FragmentHeckOne : Fragment() {

    private var message: String? = null

    private lateinit var badSfx: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString(MESSAGE)
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
        messageView.text = message
        shake(messageView)

        return view
    }

    override fun onStart() {
        super.onStart()
        badSfx.start()
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().popBackStack()
        }, REPEAT_COUNT * REPEAT_LENGTH)
    }

    override fun onDestroy() {
        super.onDestroy()
        badSfx.release()
    }

    companion object {
        @JvmStatic
        fun newInstance(message: String) =
            FragmentHeckOne().apply {
                arguments = Bundle().apply {
                    putString(MESSAGE, message)
                }
            }
    }
}