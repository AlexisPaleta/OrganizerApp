package com.fcc.organizador.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fcc.organizador.Teacher
import com.fcc.organizador.schedule.Schedule
import com.fcc.organizador.schedule.ScheduleProvider

//The app uses a unique database helper to create all the necessary tables, this class has all the methods to add and delete elements
//of all the tables
class AppDatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "organizer_app.db"
        private const val DATABASE_VERSION = 2


        //TABLE: TEACHER
        private const val TABLE_TEACHER = "teacher"
        private const val COL_TEACHER_ID = "id"
        private const val COL_TEACHER_NAME = "name"
        private const val COL_TEACHER_CUBICLE = "cubicle"
        private const val COL_TEACHER_CONTACT = "contact"
        private const val COL_TEACHER_DESCRIPTION = "description"
        private const val COL_TEACHER_POSITION = "position"

        //TABLE: SCHEDULE
        private const val TABLE_SCHEDULE = "schedule_cells"
        private const val COL_SCHEDULE_ID = "id"
        private const val COL_SCHEDULE_TEXT = "content"
        private const val COL_SCHEDULE_COLOR = "color"
        private const val COL_SCHEDULE_POSITION = "position"
        private val scheduleList: List<Schedule> = ScheduleProvider.scheduleList
    }

    override fun onCreate(db: SQLiteDatabase?) {
        var createTableQuery = "CREATE TABLE $TABLE_SCHEDULE ($COL_SCHEDULE_ID INTEGER PRIMARY KEY, $COL_SCHEDULE_TEXT TEXT NOT NULL, $COL_SCHEDULE_COLOR INTEGER NOT NULL, $COL_SCHEDULE_POSITION INTEGER NOT NULL UNIQUE)"
        db?.execSQL(createTableQuery)

        //Adding initial weekdays
        for (schedule in scheduleList){
            val values = ContentValues().apply{
                put(COL_SCHEDULE_TEXT, schedule.content)
                put(COL_SCHEDULE_COLOR, schedule.color)
                put(COL_SCHEDULE_POSITION, schedule.position)
            }
            db?.insert(TABLE_SCHEDULE, null, values)
        }

        createTableQuery = "CREATE TABLE $TABLE_TEACHER ($COL_TEACHER_ID INTEGER PRIMARY KEY, $COL_TEACHER_NAME TEXT NOT NULL, $COL_TEACHER_CUBICLE TEXT NOT NULL, $COL_TEACHER_DESCRIPTION TEXT NOT NULL, $COL_TEACHER_CONTACT TEXT NOT NULL, $COL_TEACHER_POSITION INTEGER NOT NULL UNIQUE)"
        db?.execSQL(createTableQuery)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var dropTableQuery = "DROP TABLE IF EXISTS $TABLE_SCHEDULE"
        db?.execSQL(dropTableQuery)
        dropTableQuery = "DROP TABLE IF EXISTS $TABLE_TEACHER"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertScheduleCell(schedule: Schedule){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COL_SCHEDULE_TEXT, schedule.content)
            put(COL_SCHEDULE_COLOR, schedule.color)
            put(COL_SCHEDULE_POSITION, schedule.position)
        }
        db.insert(TABLE_SCHEDULE, null, values)
        db.close()
    }

    fun getAllScheduleCells(): MutableList<Schedule>{
        val scheduleList = mutableListOf<Schedule>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_SCHEDULE ORDER BY $COL_SCHEDULE_POSITION ASC"
        val cursor = db.rawQuery(query, null)

        with(cursor) {
            while (moveToNext()) {
                val content = getString(getColumnIndexOrThrow(COL_SCHEDULE_TEXT))
                val color = getInt(getColumnIndexOrThrow(COL_SCHEDULE_COLOR))
                val position = getInt(getColumnIndexOrThrow(COL_SCHEDULE_POSITION))

                scheduleList.add(Schedule(content, color, position))
            }
            close()
        }
        return scheduleList
    }

    fun deleteScheduleCell(position: Int){
        val db = writableDatabase
        try {
            db.delete(TABLE_SCHEDULE, "$COL_SCHEDULE_POSITION = ?", arrayOf(position.toString()))
        } finally {
            db.close()
        }
    }

    fun updateScheduleCell(schedule: Schedule){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COL_SCHEDULE_TEXT, schedule.content)
            put(COL_SCHEDULE_COLOR, schedule.color)
        }

        db.update(TABLE_SCHEDULE, values, "$COL_SCHEDULE_POSITION = ?", arrayOf(schedule.position.toString()))
        db.close()
    }

    fun insertTeacher(teacher: Teacher){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COL_TEACHER_NAME, teacher.name)
            put(COL_TEACHER_CUBICLE, teacher.cubicle)
            put(COL_TEACHER_CONTACT, teacher.contact)
            put(COL_TEACHER_DESCRIPTION, teacher.description)
            put(COL_TEACHER_POSITION, teacher.position)
        }
        db.insert(TABLE_TEACHER, null, values)
        db.close()
    }
}