package com.example.hypeadvice.domain.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final String DATE_TIME_FORMAT_2 = "dd/MM/yyyy HH:mm:ss";

    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static Gson getGson() {
        return GSON;
    }

    public static <T> T jsonToObject(Class<T> clazz, String jsonAsString) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonAsString, clazz);
    }

    private static String format(Date data, SimpleDateFormat dateTimeFormat) {
        if (data == null) {
            return null;
        }
        return dateTimeFormat.format(data);
    }

    public static String formatDateTime2(Date data) {
        return format(data, new SimpleDateFormat(DATE_TIME_FORMAT_2));
    }

    public static String format(Date data, String pattern) {
        return format(data, new SimpleDateFormat(pattern));
    }
}
