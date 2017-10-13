package oogbox.api.odoo.client.helper.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class JSONUtils {

    public static JSONArray arrayToJsonArray(Integer[] items) {
        JSONArray itemArray = new JSONArray();
        try {
            for (int item : items) itemArray.put(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemArray;
    }

    public static <T> List<T> jsonArrayToList(JSONArray array) {
        List<T> items = new ArrayList<>();
        for (int i = 0; i < array.length(); i++)
            try {
                items.add((T) array.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return items;
    }
}
