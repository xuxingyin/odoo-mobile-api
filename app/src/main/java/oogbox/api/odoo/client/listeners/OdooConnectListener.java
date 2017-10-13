package oogbox.api.odoo.client.listeners;

import oogbox.api.odoo.client.OdooVersion;

public interface OdooConnectListener {
    void onConnected(OdooVersion version);
}
