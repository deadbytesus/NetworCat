// Copyright (c) 2025, DeadBytes(Luka Maidanov). ALL RIGHTS RESERVED.
//
// SPDX-License-Identifier: Apache-2.0

package xxx.deadbytes.networcat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment

class MainFragment : Fragment() {

    private var prefs: SharedPreferences? = null
    private var etCountryIso: EditText? = null
    private var etNumeric: EditText? = null
    private var etOperator: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)

        etCountryIso = view.findViewById(R.id.iso_country_input)
        etNumeric = view.findViewById(R.id.numeric_input)
        etOperator = view.findViewById(R.id.operator_input)

        view.findViewById<Button>(R.id.button_save).setOnClickListener { savePrefs() }
        view.findViewById<Button>(R.id.button_reset).setOnClickListener { resetPrefs() }

        loadPrefs()
    }

    private fun loadPrefs() {
        etCountryIso?.setText(prefs?.getString("pref_country_iso", null))
        etNumeric?.setText(prefs?.getString("pref_numeric", null))
        etOperator?.setText(prefs?.getString("pref_operator", null))
    }

    private fun savePrefs() {
        val countryIso = etCountryIso?.text.toString()
        val numeric = etNumeric?.text.toString()
        val operator = etOperator?.text.toString()

        if (countryIso.isNotEmpty() && !countryIso.matches(Regex("^[a-zA-Z]{2}$"))) {
            Toast.makeText(requireContext(), "Invalid country ISO", Toast.LENGTH_SHORT).show()
            return
        }
        if (numeric.isNotEmpty() && !numeric.matches(Regex("^\\d{5,6}$"))) {
            Toast.makeText(requireContext(), "Invalid MCC/MNC value", Toast.LENGTH_SHORT).show()
            return
        }

        prefs!!.edit {
            putString("pref_country_iso", countryIso)
            putString("pref_numeric", numeric)
            putString("pref_operator", operator)
        }

        val serviceIntent = Intent(requireContext(), ConfigService::class.java)
        requireContext().startService(serviceIntent)

        Toast.makeText(requireContext(), "Setting saved. Now you can patch app.", Toast.LENGTH_SHORT).show()
    }

    private fun resetPrefs() {
        etCountryIso?.setText("")
        etNumeric?.setText("")
        etOperator?.setText("")

        prefs?.edit { clear() }

        Toast.makeText(requireContext(), "Settings reset", Toast.LENGTH_SHORT).show()
    }

}