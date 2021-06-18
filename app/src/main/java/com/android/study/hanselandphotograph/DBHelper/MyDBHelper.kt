package com.android.study.hanselandphotograph.DBHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.android.study.hanselandphotograph.model.ARData
import com.android.study.hanselandphotograph.model.Location
import com.android.study.hanselandphotograph.model.Picture
import com.android.study.hanselandphotograph.model.Story
import com.android.study.hanselandphotograph.ui.ArActivity

class MyDBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        val DB_NAME = "db1.db"
        val DB_VERSION = 1
        val STORY_TABLE = "story"
        val STORY_ID = "story_id"
        val STORY_DATE = "story_date"
        val STORY_NAME = "story_name"
        val STORY_COMMENT = "story_comment"
        val LOC_TABLE = "location"
        val LOC_ID = "loc_id"
        val LOC_LAT = "loc_lat"
        val LOC_LONG = "loc_long"
        val PIC_TABLE = "picture"
        val PIC_ID = "pic_id"
        val PIC_TITLE = "pic_title"
        val PIC_PATH = "pic_path"
        val PIC_LAT = "pic_lat"
        val PIC_LONG = "pic_long"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_storyTable = "create table if not exists $STORY_TABLE(" +
                "$STORY_ID integer primary key autoincrement, " +
                "$STORY_DATE text, " +
                "$STORY_NAME text, " +
                "$STORY_COMMENT text);"
        val create_locTable = "create table if not exists $LOC_TABLE(" +
                "$LOC_ID integer, " +
                "$STORY_ID integer, " +
                "$LOC_LAT real, " +
                "$LOC_LONG real, " +
                "primary key($LOC_ID, $STORY_ID));"
        val create_picTable = "create table if not exists $PIC_TABLE(" +
                "$PIC_ID integer, " +
                "$STORY_ID integer, " +
                "$PIC_TITLE text, " +
                "$PIC_PATH text, " +
                "$PIC_LAT real, " +
                "$PIC_LONG real, " +
                "primary key($PIC_ID, $STORY_ID));"
        db!!.execSQL(create_storyTable)
        db!!.execSQL(create_locTable)
        db!!.execSQL(create_picTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_storyTable = "drop table if exists $STORY_TABLE;"
        val drop_locTable = "drop table if exists $LOC_TABLE;"
        val drop_picTable = "drop table if exists $PIC_TABLE;"
        db!!.execSQL(drop_storyTable)
        db!!.execSQL(drop_locTable)
        db!!.execSQL(drop_picTable)
        onCreate(db)
    }

//    private fun setStoryID():Int{
//        val strsql = "select count($STORY_ID) from $STORY_TABLE;"
//        val db1 = readableDatabase
//        val cursor = db1.rawQuery(strsql, null)
//        cursor.moveToFirst()
//        val sID = cursor.count + 1
////        if(cursor!=null){
////            cursor.moveToLast()
////            sID = cursor.getInt(0) + 1
////            cursor.close()
////            db1.close()
////        }
//        cursor.close()
//        db1.close()
//        return sID
//    }

    /*private fun getStoryID():Int{
        val strsql = "select $STORY_ID from $STORY_TABLE order by $STORY_ID;"
        val db1 = readableDatabase
        val cursor = db1.rawQuery(strsql, null)
        cursor.moveToLast()
        val sID = cursor.getInt(0)
        cursor.close()
        db1.close()
        return sID
    }*/

    fun getStoryID(): Int {
        val strsql = "select count($STORY_ID) from $STORY_TABLE;"
        val db1 = readableDatabase
        val cursor = db1.rawQuery(strsql, null)
        var sID = 0
        if (cursor != null) {
            cursor.moveToFirst()
            sID = cursor.getInt(0)
            cursor.close()
            db1.close()
        }
        return sID
    }

    fun selectLocID(dat: Int): Int {
//        val strsql = "select count(*) from $LOC_TABLE where $STORY_ID = $dat;"
        val db = readableDatabase
        val cursor = db.query(
            LOC_TABLE,
            arrayOf("count(*)"),
            "$STORY_ID = ?",
            arrayOf("$dat"),
            null,
            null,
            null
        )
//        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        val flag = cursor.getInt(0)
        Log.i("flag", flag.toString())
        cursor.close()
        db.close()
        return flag
    }

    fun selectPicID(dat: Int): Int {
//        val strsql = "select count(*) from $PIC_TABLE where $STORY_ID = $dat;"
        val db = readableDatabase
        val cursor = db.query(
            PIC_TABLE,
            arrayOf("count(*)"),
            "$STORY_ID = ?",
            arrayOf("$dat"),
            null,
            null,
            null
        )
//        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        val flag = cursor.getInt(0)
        cursor.close()
        db.close()
        return flag
    }

    fun insertLocation(location: Location): Boolean {
        val sID = getStoryID()
        val values = ContentValues()
        values.put(LOC_ID, selectLocID(sID))
        values.put(STORY_ID, sID)
        values.put(LOC_LAT, location.x)
        values.put(LOC_LONG, location.y)
        val db2 = writableDatabase
        val flag = db2.insert(LOC_TABLE, null, values) > 0
        Log.i("flag", flag.toString())
        db2.close()
        return flag
    }

    fun insertPicture(picture: Picture): Boolean {
        val sID = getStoryID()
        val values = ContentValues()
        values.put(PIC_ID, selectPicID(sID))
        values.put(STORY_ID, sID)
        values.put(PIC_TITLE, picture.title)
        values.put(PIC_PATH, picture.path)
        values.put(PIC_LAT, picture.lat)
        values.put(PIC_LONG, picture.long)
        val db = writableDatabase
        val flag = db.insert(PIC_TABLE, null, values) > 0
        db.close()
        return flag
    }

    fun insertStory(story: Story): Boolean {
        val values = ContentValues()
        values.put(STORY_DATE, story.date)
        values.put(STORY_NAME, story.name)
        values.put(STORY_COMMENT, story.comment)
        val db = writableDatabase
        val flag = db.insert(STORY_TABLE, null, values) > 0
        db.close()
        Log.i("sid", getStoryID().toString())
        return flag
    }

    fun arSearch(): Boolean {
        val strsql = "select * from $PIC_TABLE;"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if (flag) {
            val activity = context as ArActivity
            cursor.moveToFirst()
            do {
                activity.data.add(
                    ARData(
                        cursor.getDouble(4),
                        cursor.getDouble(5),
                        cursor.getString(2),
                        cursor.getString(3)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return flag
    }

    /**************************************************************************************************/
    fun getAllStory(): ArrayList<Story> {
        val db = readableDatabase
        val sql = "select $STORY_ID, $STORY_DATE, $STORY_NAME, $STORY_COMMENT from $STORY_TABLE"
        val cur = db.rawQuery(sql, null)
        cur.moveToFirst()
        val ret = arrayListOf<Story>()
        for (i in 1..cur.count) {
            val story = Story(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3))
            ret.add(story)
            cur.moveToNext()
        }
        cur.close()
        db.close()
        return ret
    }

    fun getStory(_story_id: Int): Story {
        val db = readableDatabase
        val sql =
            "select $STORY_ID, $STORY_DATE, $STORY_NAME, $STORY_COMMENT from $STORY_TABLE where $STORY_ID = $_story_id"
        val cur = db.rawQuery(sql, null)
        cur.moveToFirst()
        if (cur.count == 0) {
            return Story(-1, "", "", "")
        }
        return Story(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3))
    }

    fun getLocation(_story_id: Int): ArrayList<Location> {
        val db = readableDatabase
        // 위치를 순서대로 받아와야 이동한 순서대로 루트를 그린다.
        val sql =
            "select $LOC_LAT, $LOC_LONG from $LOC_TABLE where $STORY_ID = $_story_id order by $LOC_ID"
        val cur = db.rawQuery(sql, null)
        cur.moveToFirst()
        val ret = arrayListOf<Location>()
        for (i in 1..cur.count) {
            val location = Location(cur.getDouble(0), cur.getDouble(1))
            ret.add(location)
            cur.moveToNext()
        }
        cur.close()
        db.close()
        return ret
    }

    fun getPicture(_story_id: Int): ArrayList<Picture> {
        val db = readableDatabase
        val sql =
            "select $PIC_ID, $PIC_TITLE, $PIC_PATH, $PIC_LAT, $PIC_LONG from $PIC_TABLE where $STORY_ID = $_story_id"
        val cur = db.rawQuery(sql, null)
        cur.moveToFirst()
        val ret = arrayListOf<Picture>()
        for (i in 1..cur.count) {
            val picture = Picture(
                cur.getInt(0),
                cur.getString(1),
                cur.getString(2),
                cur.getDouble(3),
                cur.getDouble(4)
            )
            ret.add(picture)
            cur.moveToNext()
        }
        cur.close()
        db.close()
        return ret
    }
    /**************************************************************************************************/

}