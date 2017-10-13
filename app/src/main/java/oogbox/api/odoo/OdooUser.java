package oogbox.api.odoo;

import oogbox.api.odoo.client.OdooVersion;
import oogbox.api.odoo.client.helper.data.OdooResult;

public class OdooUser {

    public int uid, companyId, partnerId;
    public String name, username, lang, tz, database, fcmProjectId, sessionId;
    public boolean isSuperuser;
    public OdooVersion odooVersion = new OdooVersion();

    public static OdooUser parse(OdooResult result) {
        OdooUser user = new OdooUser();

        // Related record ids (user id, company id and user's partner id)
        user.uid = result.getInt("uid");
        user.companyId = result.getInt("company_id");
        user.partnerId = result.getInt("partner_id");

        user.name = result.getString("name");
        if (user.name == null) {
            // Odoo 10.0 Only contain name
            user.name = result.getString("username");
        }
        user.database = result.getString("db");
        user.fcmProjectId = result.getString("fcm_project_id");
        user.sessionId = result.getString("session_id");
        user.username = result.getString("username");

        // Context details
        OdooResult context = result.getData("user_context");
        user.lang = context.getString("lang");
        user.tz = context.getString("tz");
        user.isSuperuser = result.getBoolean("is_superuser");

        // Odoo Version
        // Odoo 10.0+ contains only version info in user session
        if (result.containsKey("server_version_info"))
            user.odooVersion = OdooVersion.parse(result);
        return user;
    }

    @Override
    public String toString() {
        return "OdooUser{" +
                "uid=" + uid +
                ", companyId=" + companyId +
                ", partnerId=" + partnerId +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", lang='" + lang + '\'' +
                ", tz='" + tz + '\'' +
                ", database='" + database + '\'' +
                ", fcmProjectId='" + fcmProjectId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", isSuperuser=" + isSuperuser +
                ", odooVersion=" + odooVersion +
                '}';
    }
}
