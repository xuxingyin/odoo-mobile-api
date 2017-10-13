package oogbox.api.odoo.client.helper.utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ODomainArgsHelper<T> {
    public static final String TAG = ODomainArgsHelper.class.getSimpleName();
    protected List<Object> mObjects = new ArrayList<>();

    /**
     * Add domain condition, array list
     *
     * @param data
     * @return self object
     */
    public T add(Object data) {
        mObjects.add(data);
        return (T) this;
    }


    /**
     * Append another object values to current object value
     *
     * @param domain
     * @return updated domain
     */
    public T append(ODomain domain) {
        if (domain != null) {
            for (Object obj : domain.getObject()) {
                add(obj);
            }
        }
        return (T) this;
    }

    /**
     * Get domain as list object
     *
     * @return list of objects
     */
    public List<Object> getObject() {
        return mObjects;
    }

    /**
     * Gets domain objects as array
     *
     * @return JSON Array of objects
     */
    public JSONArray getArray() {
        JSONArray result = new JSONArray();
        for (Object obj : mObjects) {
            result.put(obj);
        }
        return result;
    }

    public List<Object> getAsList() {
        return mObjects;
    }

    public JSONArray listToArray(Object collection) {
        JSONArray array = new JSONArray();
        List<Object> list = (List<Object>) collection;
        try {
            for (Object data : list) {
                array.put(data);
            }
        } catch (Exception e) {

        }
        return array;
    }
}
