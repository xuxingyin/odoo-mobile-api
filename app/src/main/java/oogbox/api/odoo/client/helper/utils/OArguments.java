package oogbox.api.odoo.client.helper.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class OArguments extends ODomainArgsHelper<OArguments> {
    public static final String TAG = OArguments.class.getSimpleName();

    public OArguments add(List<?> datas) {
        addItems(datas);
        return this;
    }

    public void addItems(List<?> datas) {
        JSONArray items = new JSONArray();
        for (Object o : datas) {
            items.put(o);
        }
        add(items);
    }

    public OArguments addNULL() {
        mObjects.add(null);
        return this;
    }

    public JSONArray get() {
        JSONArray arguments = new JSONArray();
        for (Object obj : getObject()) {
            JSONArray data = new JSONArray();
            data.put(obj);
            if (obj instanceof JSONObject) {
                arguments.put(obj);
            } else {
                arguments.put(data);
            }
        }
        return arguments;
    }
}
