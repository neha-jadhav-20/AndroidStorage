package com.example.androidstorage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {
    val studentList = mutableListOf<Student>()
    lateinit var studentListView: ListView
    lateinit var stdAdapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        studentListView = findViewById(R.id.lv)

        populateList()
        stdAdapter = StudentAdapter(this,studentList)
        studentListView.adapter = stdAdapter
        registerForContextMenu(studentListView)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val name = studentList [ info.position ].name

        menu?.setHeaderTitle(name)
        menu?.add("edit")
        menu?.add("delete")
    }


//    val MENU_EDIT = 0
//    val MENU_DELETE = 1
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val std = studentList[info.position]
        val wrapper = DBWrapper(this)
        when(item.title){
            "edit"->{
                std.name = std.name.uppercase()
                wrapper.editStudent(std)
            }
            "delete"->{
                wrapper.deleteStudent(std)
                studentList.remove(std)
            }
        }
        stdAdapter.notifyDataSetChanged()
        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        populateList()
//        studentList.sortByDescending {   // instead of this we can do changes in query to sort
//            it.marks                     // in orderBy
//        }
        stdAdapter = StudentAdapter(this,studentList)
        studentListView.adapter = stdAdapter
    }

    private fun populateList() {
        val wrapper = DBWrapper(this)
        val resultC = wrapper.getStudent()         // calling getstudents result will be returned as Cursor object
        studentList.clear()

        if(resultC.count>0){
            resultC.moveToFirst()

            val idx_id = resultC.getColumnIndex(DBHelper.CLM_STD_ID)
            val idx_name = resultC.getColumnIndex(DBHelper.CLM_STD_NAME)
            val idx_marks = resultC.getColumnIndex(DBHelper.CLM_STD_MARKS)

            do{
                val id = resultC.getInt(idx_id)
                val name = resultC.getString(idx_name)
                val marks = resultC.getInt(idx_marks)
                val std = Student(id, name, marks)
                studentList.add(std)

            }while (resultC.moveToNext())      // movetonext returns booleans result

            Log.d("MainActivity","StudentList : $studentList")
            Toast.makeText(this, "No of students : ${studentList.size}",
                Toast.LENGTH_SHORT).show()

        }

        else{
            Toast.makeText(this, "No students added yet", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("Login")
        menu?.add("Logout")
        menu?.add("Save")
        menu?.add("Read")
        menu?.add("Delete File")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.title){
           "Login"->{
               //read the credentials
               val pref = getSharedPreferences("credentials", MODE_PRIVATE)
               val userid = pref.getString("userid",null)     //(get userid if present otherwise return null)
               val password = pref.getString("password", null)    // you get data by getter methods


               //If not present credentials then only we will launch auth activity otherwise return a toast
               if(userid == null || password == null) {
                   val i = Intent(this, AuthActivity::class.java)
                   startActivity(i)
               }
               else
                   Toast.makeText(this, "Already logged in as: $userid, $password", Toast.LENGTH_SHORT).show()
           }

           "Logout"->{
               removeCredentials()
           }

           "Save"->{
               writeToFile()
           }

           "Read"->{
               readFromFile()
           }
           "Delete File"->{
               deleteFile("test.txt")
           }
       }
        return super.onOptionsItemSelected(item)
    }

    private fun readFromFile() {
        try{val fis = openFileInput("test.txt")
        val data = String(fis.readBytes())
        fis.close()
        Toast.makeText(this, "File content: $data", Toast.LENGTH_LONG).show()
        }catch (e : Exception){
            Toast.makeText(this, "File reading issue: File might have been deleted", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeToFile() {
        val data = "\n${Calendar.getInstance().time}This is a test data"
//        val fos = openFileOutput("test.txt", MODE_PRIVATE)      // data will be overridden here (replaced)
        val fos = openFileOutput("test.txt", MODE_APPEND)     // data will be appended to previous one
        fos.write(data.toByteArray())
        fos.close()
        Toast.makeText(this, "Data Saved in file", Toast.LENGTH_LONG).show()
    }

    private fun removeCredentials() {
        val pref = getSharedPreferences("credentials", MODE_PRIVATE)
        if (pref.all.isEmpty()) {
            Toast.makeText(this, "Already logged out", Toast.LENGTH_SHORT).show()

        } else {
            val editor = pref.edit()
            editor.clear()
            editor.commit()
            Toast.makeText(this, "Logged out...", Toast.LENGTH_SHORT).show()
        }
    }

    fun fabClick(view: View) {
        val i = Intent(this, AddStudentActivity::class.java)
        startActivity(i)
    }
}