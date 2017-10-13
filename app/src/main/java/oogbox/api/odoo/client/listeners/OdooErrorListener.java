package oogbox.api.odoo.client.listeners;

import oogbox.api.odoo.client.helper.OdooErrorException;

public interface OdooErrorListener {
    void onError(OdooErrorException error);
}
