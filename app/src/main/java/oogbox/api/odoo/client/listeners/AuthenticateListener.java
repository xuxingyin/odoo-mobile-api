package oogbox.api.odoo.client.listeners;

import oogbox.api.odoo.OdooUser;
import oogbox.api.odoo.client.AuthError;

public interface AuthenticateListener {
    void onLoginSuccess(OdooUser user);

    void onLoginFail(AuthError error);
}
