package com.github.tahmid_23.hiconjugate.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.github.tahmid_23.hiconjugate.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

}