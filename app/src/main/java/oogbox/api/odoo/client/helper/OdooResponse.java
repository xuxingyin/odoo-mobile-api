package oogbox.api.odoo.client.helper;

import oogbox.api.odoo.client.helper.data.OdooError;
import oogbox.api.odoo.client.helper.data.OdooResult;

public class OdooResponse {
    public int id;
    public float jsonrpc;
    public OdooResult result;
    public OdooError error;
}
