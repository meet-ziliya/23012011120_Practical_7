package com.example.practical7_23012011120

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.practical7_23012011120.db.DatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val personList = ArrayList<Person>()
    lateinit var personsRecycleAdapter: PersonAdapter
    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // ⭐ VERY IMPORTANT — Set toolbar as action bar
        setSupportActionBar(findViewById(R.id.toolbar))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DatabaseHelper(applicationContext)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView1)
        personsRecycleAdapter = PersonAdapter(this, personList)
        recyclerView.adapter = personsRecycleAdapter

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            networkDb()
        }
    }

    private fun getPersonDetailsFromSQLiteDb() {
        val size = personList.size
        personList.clear()
        personsRecycleAdapter.notifyItemRangeRemoved(0, size)

        try {
            personList.addAll(db.allPersons)
            personsRecycleAdapter.notifyItemRangeInserted(0, personList.size)
        } catch (ee: JSONException) {
            ee.printStackTrace()
        }

        Toast.makeText(this, "Fetched details from SQLite database", Toast.LENGTH_SHORT).show()
    }

    fun deletePerson(position: Int) {
        val deletedPerson = personList[position]
        db.deletePerson(deletedPerson)
        personList.removeAt(position)
        personsRecycleAdapter.notifyItemRemoved(position)

        Toast.makeText(this, "At $position, ${deletedPerson.name} removed",
            Toast.LENGTH_LONG).show()
    }

    private fun networkDb() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = HttpRequest().makeServiceCall(
                    reqUrl = "https://api.json-generator.com/templates/NsZKF2mpqr_K/data",
                    token = "fi9lk8pykjkehcve5n6hmuxw6tiicu955jc04lkk"
                )

                withContext(Dispatchers.Main) {
                    if (data != null)
                        getPersonDetailsFromJson(data)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getPersonDetailsFromJson(sJson: String) {
        val size = personList.size
        personList.clear()
        personsRecycleAdapter.notifyItemRangeRemoved(0, size)

        try {
            val jsonArray = JSONArray(sJson)
            for (i in 0 until jsonArray.length()) {

                val jsonObject = jsonArray[i] as JSONObject
                val person = Person(jsonObject)

                personList.add(person)

                try {
                    if (db.getPerson(person.id) != null)
                        db.updatePerson(person)
                    else
                        db.insertPerson(person)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            personsRecycleAdapter.notifyItemRangeInserted(0, personList.size)

        } catch (ee: JSONException) {
            ee.printStackTrace()
        }

        Toast.makeText(this, "Fetch details from JSON", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_sqlitedb -> {
                getPersonDetailsFromSQLiteDb()
                true
            }

            R.id.action_nwdb -> {
                networkDb()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
