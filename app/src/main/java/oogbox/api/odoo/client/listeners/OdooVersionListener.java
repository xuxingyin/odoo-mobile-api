package oogbox.api.odoo.client.listeners;

import oogbox.api.odoo.client.OdooVersion;

public interface OdooVersionListener {
    void onVersionLoad(OdooVersion version);
}
