package oogbox.api.odoo.client.listeners;

import oogbox.api.odoo.client.builder.data.OdooRecords;

public interface IOdooRecords {

    void onRecords(OdooRecords records);
}
