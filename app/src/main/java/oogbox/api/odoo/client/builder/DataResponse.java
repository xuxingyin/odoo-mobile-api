package oogbox.api.odoo.client.builder;

import android.util.Log;

import oogbox.api.odoo.OdooClient;
import oogbox.api.odoo.client.builder.data.OdooRecords;
import oogbox.api.odoo.client.helper.OdooErrorException;
import oogbox.api.odoo.client.helper.data.OdooRecord;
import oogbox.api.odoo.client.helper.data.OdooResult;
import oogbox.api.odoo.client.helper.utils.ODomain;
import oogbox.api.odoo.client.helper.utils.OdooFields;
import oogbox.api.odoo.client.listeners.IOdooRecords;
import oogbox.api.odoo.client.listeners.IOdooResponse;
import oogbox.api.odoo.client.listeners.OdooErrorListener;

public class DataResponse {
    private static final String TAG = DataResponse.class.getSimpleName();
    private RequestBuilder requestBuilder;
    private IOdooRecords resultCallback;
    private OdooErrorListener errorListener;
    private OdooRecords odooRecords = new OdooRecords();

    private OdooClient client;

    public DataResponse(RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
        client = requestBuilder.getClient();
        odooRecords.setDataResponse(this);
    }

    /**
     * Sets request error listener
     *
     * @param listener callback for error listener
     */
    public void setErrorListener(OdooErrorListener listener) {
        errorListener = listener;
    }

    /**
     * Request data and return records list from server
     *
     * @param resultCallback Records Callback listener
     */
    public void getRecords(final IOdooRecords resultCallback) {
        this.resultCallback = resultCallback;
        _basicRequest(new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();
                odooRecords.appendRecords(records);
                odooRecords.setDBLength(result.getInt("length"));
                if (resultCallback != null) resultCallback.onRecords(odooRecords);
            }

            @Override
            public boolean onError(OdooErrorException error) {
                if (errorListener != null) errorListener.onError(error);
                return super.onError(error);
            }
        });
    }

    /**
     * Responsible to request data with new offset (by adding old offset + limit)
     */
    public void requestNext() {
        int offset = requestBuilder.getOffset();
        int limit = requestBuilder.getLimit();
        requestBuilder.setOffset(limit + offset);
        getRecords(resultCallback);
    }


    private void _basicRequest(IOdooResponse response) {
        if (validSession()) {
            String model = requestBuilder.getModelName();
            ODomain domain = requestBuilder.getDomain();
            OdooFields fields = requestBuilder.getFields();
            int limit = requestBuilder.getLimit();
            int offset = requestBuilder.getOffset();
            String sorting = requestBuilder.getSorting();

            // Requesting data
            client.searchRead(model, domain, fields, offset, limit, sorting, response);
        } else {
            Log.e(TAG, "Invalid session information");
        }
    }

    /**
     * Checks for valid session available for user.
     *
     * @return true if session available otherwise false
     */
    private boolean validSession() {
        if (client.isConnected()) {
            if (client.getSessionId() == null) {
                OdooErrorException error = new OdooErrorException("User must be authenticated before getting data from server",
                        OdooErrorException.ErrorType.SessionExpired);
                if (errorListener != null) errorListener.onError(error);
                else {
                    Log.e(TAG, error.getMessage(), error);
                }
                return false;
            }
        } else {
            Log.w(TAG, "Odoo Not connected. Please make sure Odoo Connection before requesting data.");
            return false;
        }
        return true;
    }
}
