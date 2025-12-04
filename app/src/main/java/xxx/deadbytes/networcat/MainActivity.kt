// Copyright (c) 2025, DeadBytes(Luka Maidanov). ALL RIGHTS RESERVED.
//
// SPDX-License-Identifier: Apache-2.0

package xxx.deadbytes.networcat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "NetworCat"
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .commit()
        }
    }
}