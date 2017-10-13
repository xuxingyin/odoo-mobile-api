package oogbox.api.odoo.client.helper.utils;

import org.json.JSONObject;

import java.util.HashMap;

public class OdooValues {

    private HashMap<String, Object> _data = new HashMap<>();

    public void put(String key, Object value) {
        _data.put(key, value);
    }

    public JSONObject toJSON() {
        JSONObject values = new JSONObject();
        try {
            for (String key : _data.keySet()) {
                values.put(key, _data.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public void putAll(HashMap<String, Object> data) {
        _data.putAll(data);
    }

    public HashMap<String, Object> getData() {
        return _data;
    }

    @Override
    public String toString() {
        return _data.toString();
    }
}
