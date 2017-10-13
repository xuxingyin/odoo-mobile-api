package oogbox.api.odoo.client.helper.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Generate domain for RPC-Call
 */
public class ODomain extends ODomainArgsHelper<ODomain> {
    public static final String TAG = ODomain.class.getSimpleName();

    /**
     * Add Domain with column, operator and it's condition value
     *
     * @param column
     * @param operator
     * @param value
     * @return self object
     */
    public ODomain add(String column, String operator, Object value) {
        JSONArray domain = new JSONArray();
        domain.put(column);
        domain.put(operator);
        if (value instanceof List) {
            domain.put(listToArray(value));
        } else {
            domain.put(value);
        }
        add(domain);
        return this;
    }

    /**
     * Gets JSONObject with domain key
     *
     * @return JSONObject with domain key
     */
    public JSONObject get() {
        JSONObject result = new JSONObject();
        try {
            result.put("domain", getArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
