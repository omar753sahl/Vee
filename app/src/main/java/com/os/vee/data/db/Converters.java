package com.os.vee.data.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Omar on 30-Jul-18 3:22 PM.
 */

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
