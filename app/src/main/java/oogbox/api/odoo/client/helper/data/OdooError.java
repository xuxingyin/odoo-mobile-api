package oogbox.api.odoo.client.helper.data;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;

public class OdooError extends OdooResult {


    public OdooError getErrorData() {
        if (isValidValue("data")) {
            LinkedTreeMap map = (LinkedTreeMap) get("data");
            OdooError error = new OdooError();
            error.putAll(new HashMap<>(map));
            return error;
        }
        return null;
    }

}
