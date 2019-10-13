package nsu.manasyan.treechat.util;

import com.google.gson.Gson;

public class JsonService {
    private static Gson gson = new Gson();

    public static <T> String toJson(T object){
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> tClass){
        return gson.fromJson(json,tClass);
    }
}
