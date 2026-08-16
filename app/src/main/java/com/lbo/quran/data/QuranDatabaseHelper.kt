package com.lbo.quran.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream

object QuranDatabaseHelper {

    @Volatile private var db: SQLiteDatabase? = null

    fun getDatabase(context: Context): SQLiteDatabase {
        db?.let { return it }
        synchronized(this) {
            db?.let { return it }
            val dbFile = context.getDatabasePath("quran.db")
            if (!dbFile.exists()) {
                dbFile.parentFile?.mkdirs()
                // فایل quran.db.gz در assets فشرده است (حجم اصلی دیتابیس بیش از ۲۵ مگابایت
                // است و برای جا شدن راحت‌تر در گیت‌هاب، به‌صورت gzip نگه‌داری می‌شود).
                // در اولین اجرای برنامه، یک‌بار باز و در حافظه داخلی گوشی ذخیره می‌شود.
                context.assets.open("quran.db.gz").use { input ->
                    GZIPInputStream(input).use { gzipInput ->
                        FileOutputStream(dbFile).use { output ->
                            gzipInput.copyTo(output)
                        }
                    }
                }
            }
            val opened = SQLiteDatabase.openDatabase(
                dbFile.path, null, SQLiteDatabase.OPEN_READONLY
            )
            db = opened
            return opened
        }
    }
}
