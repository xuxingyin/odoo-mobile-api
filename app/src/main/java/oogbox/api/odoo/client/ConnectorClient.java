package oogbox.api.odoo.client;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import oogbox.api.odoo.OdooClient;
import oogbox.api.odoo.OdooUser;
import oogbox.api.odoo.R;
import oogbox.api.odoo.client.helper.OdooErrorException;
import oogbox.api.odoo.client.helper.OdooResponse;
import oogbox.api.odoo.client.helper.data.OdooResult;
import oogbox.api.odoo.client.helper.utils.OArguments;
import oogbox.api.odoo.client.helper.utils.ODomain;
import oogbox.api.odoo.client.helper.utils.OdooFields;
import oogbox.api.odoo.client.helper.utils.OdooParams;
import oogbox.api.odoo.client.helper.utils.OdooValues;
import oogbox.api.odoo.client.helper.utils.ResponseQueue;
import oogbox.api.odoo.client.listeners.AuthenticateListener;
import oogbox.api.odoo.client.listeners.IOdooResponse;
import oogbox.api.odoo.client.listeners.OdooConnectListener;
import oogbox.api.odoo.client.listeners.OdooErrorListener;
import oogbox.api.odoo.client.listeners.OdooVersionListener;

public abstract class ConnectorClient<T> implements Response.Listener<JSONObject>, Response.ErrorListener {

    private static final String TAG = ConnectorClient.class.getSimpleName();
    protected Context mContext;
    protected String serverHost;
    protected String sessionId;
    protected Boolean synchronizedRequests = false;

    // Listeners
    protected OdooErrorListener errorListener;
    protected OdooConnectListener odooConnectListener;

    private Integer newRequestTimeout = OdooClient.REQUEST_TIMEOUT_MS;
    private Integer newRequestMaxRetry = OdooClient.DEFAULT_MAX_RETRIES;

    private ResponseQueue responseQueue;
    private RequestQueue requestQueue;

    private Gson gson = new Gson();

    /* Data */
    protected OdooVersion odooVersion = new OdooVersion();
    protected OdooUser odooUser = new OdooUser();
    protected List<String> databases = new ArrayList<>();
    protected Boolean isConnected = false;

    /* Request Data */
    private OdooParams requestKWArgs = new OdooParams();
    private OdooParams requestContext = new OdooParams();


    protected ConnectorClient(Context context) {
        mContext = context;
        responseQueue = ResponseQueue.getInstanceSingleton();
        requestQueue = Volley.newRequestQueue(context);
    }

    public void connect() {
        Log.v(TAG, "Connecting to " + serverHost);

        // First getting odoo version.
        getVersionInfo(new OdooVersionListener() {
            @Override
            public void onVersionLoad(final OdooVersion version) {

                if (version.version_mejor < 8) {
                    String message = "Unsupported Odoo Version. API Supports only Odoo 8.0+";
                    Log.e(TAG, message);
                    if (errorListener != null) {
                        errorListener.onError(new OdooErrorException(message,
                                OdooErrorException.ErrorType.UnsupportedVersion));
                    }
                    return;
                }

                Log.v(TAG, "Connected to " + serverHost);
                Log.v(TAG, "Odoo Version: " + version.server_version);

                // Getting database lists
                getDatabases(new IOdooResponse() {
                    @Override
                    public void onResult(OdooResult result) {
                        List<String> dbList = result.getArray("result");
                        databases.addAll(dbList);
                        isConnected = true;
                        if (odooConnectListener != null) odooConnectListener.onConnected(version);
                    }

                    @Override
                    public boolean onError(OdooErrorException error) {
                        isConnected = true;
                        if (odooConnectListener != null) odooConnectListener.onConnected(version);
                        return true;
                    }
                });
            }
        });
    }

    /**
     * Register odoo connection listener, Will be triggered when successfully connected
     * to Odoo
     *
     * @param listener connection listener callback
     * @return self object with new properties
     */
    public T setOnConnectListener(OdooConnectListener listener) {
        odooConnectListener = listener;
        return (T) this;
    }

    /**
     * Get the odoo server version information
     *
     * @param callback result response callback
     */
    public void getVersionInfo(final OdooVersionListener callback) {
        String url = serverHost + "/web/webclient/version_info";
        try {
            call(url, new JSONObject(), new IOdooResponse() {
                @Override
                public void onResult(OdooResult result) {
                    odooVersion = OdooVersion.parse(result);
                    if (callback != null) callback.onVersionLoad(odooVersion);
                }
            });
        } catch (Exception e) {
            // pass
        }
    }

    /**
     * Gets databases available on server
     *
     * @param callback response callback
     */
    public void getDatabases(IOdooResponse callback) {
        String url = serverHost;
        OdooParams params = new OdooParams();

        if (odooVersion.version_mejor == 9) {
            url += "/jsonrpc";
            params.add("method", "list");
            params.add("service", "db");
            params.add("args", new JSONArray());

        } else if (odooVersion.version_mejor >= 10) {
            url += "/web/database/list";
            params.add("context", new JSONObject());

        } else {
            url += "/web/database/get_list";
            params.add("context", new JSONObject());

        }
        try {
            call(url, params, callback);
        } catch (JSONException e) {
            // Pass
        }
    }

    /**
     * Gets the users session information from server
     *
     * @param callback response callback
     */
    public void getSessionInformation(IOdooResponse callback) {
        String url = serverHost + "/web/session/get_session_info";
        try {
            call(url, new OdooParams(), callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Odoo Authentication
     *
     * @param login    username or email address
     * @param password valid password
     * @param database database name to login
     * @param callback authenticate callback response
     */
    public void authenticate(String login, String password, String database, final AuthenticateListener callback) {
        String url = serverHost + "/web/session/authenticate";
        OdooParams params = new OdooParams();
        params.add("db", database);
        params.add("login", login);
        params.add("password", password);
        params.add("context", new OdooParams());

        try {
            call(url, params, new IOdooResponse() {
                @Override
                public void onResult(OdooResult result) {
                    OdooUser user = OdooUser.parse(result);
                    if (callback != null) callback.onLoginSuccess(user);
                    bindDetailsFromUserObject(user);
                }

                @Override
                public boolean onError(OdooErrorException error) {
                    switch (error.errorType) {
                        case ProgrammingError:
                        case SessionExpired:
                            if (callback != null)
                                callback.onLoginFail(AuthError.AuthenticationFailed);
                            break;
                        case InvalidDatabase:
                            if (callback != null) callback.onLoginFail(AuthError.DatabaseNotFound);
                            break;
                    }
                    return true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change current user password. Old password must be provided explicitly
     * to prevent hijacking an existing user session, or for cases where the cleartext
     * password is not used to authenticate requests.
     *
     * @param oldPassword old password
     * @param newPassword new password to set
     * @param callback    result response callback
     */
    public void change_password(String oldPassword, String newPassword, IOdooResponse callback) {
        String url = serverHost + "/web/session/change_password";
        try {
            OdooParams params = new OdooParams();
            JSONArray fields = new JSONArray();
            fields.put(new JSONObject().put("name", "old_pwd").put("value", oldPassword));
            fields.put(new JSONObject().put("name", "new_password").put("value", newPassword));
            fields.put(new JSONObject().put("name", "confirm_pwd").put("value", newPassword));
            params.add("fields", fields);

            call(url, params, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads the requested fields for the records in [ids]
     *
     * @param model    required model name
     * @param ids      ids list to read from model
     * @param fields   required fields to be read
     * @param callback result callback response
     */
    public void read(String model, List<Integer> ids, List<String> fields, IOdooResponse callback) {
        OArguments arguments = new OArguments();
        arguments.addItems(ids);
        arguments.add(fields);
        call_kw(model, "read", arguments, callback);
    }

    /**
     * Performs a search() followed by a read() on model.
     * Gets length of total records and filtered records based on domain, offset and limit
     *
     * @param model    model name from which data to be fetched
     * @param domain   data filtering domain (where clause)
     * @param fields   fields to fetch from model
     * @param offset   offset of record list
     * @param limit    limiting request data items
     * @param order    order of requested data (ASC or DESC)
     * @param callback result response callback
     */
    public void searchRead(String model, ODomain domain, OdooFields fields, int offset, int limit,
                           String order, IOdooResponse callback) {
        String url = serverHost + "/web/dataset/search_read";
        domain = domain == null ? new ODomain() : domain;
        fields = fields == null ? new OdooFields() : fields;

        try {
            OdooParams params = new OdooParams();
            params.add("model", model);
            params.add("fields", fields.get().getJSONArray("fields"));
            params.add("domain", domain.getArray());
            params.add("context", getUserContext());
            params.add("offset", offset);
            params.add("limit", limit);
            params.add("sort", order == null ? "" : order);

            call(url, params, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create record on the server with given values on model
     *
     * @param model    required model name to which record need to create
     * @param values   values data for model
     * @param callback result callback response
     */
    public void create(String model, OdooValues values, IOdooResponse callback) {
        OArguments arguments = new OArguments();
        arguments.add(values.toJSON());

        call_kw(model, "create", arguments, callback);
    }

    /**
     * Writes/Update record on server for given ids and values
     *
     * @param model    model required on which to update record
     * @param ids      list of ids on which updating record
     * @param values   new values to update on record
     * @param callback result callback response
     */
    public void write(String model, Integer[] ids, OdooValues values, IOdooResponse callback) {
        OArguments arguments = new OArguments();
        arguments.addItems(Arrays.asList(ids));
        arguments.add(values.toJSON());

        call_kw(model, "write", arguments, callback);
    }

    /**
     * Deletes the records of the current set of ids
     *
     * @param model    model from record need to be deleted
     * @param ids      list of record ids to delete from server model
     * @param callback result response callback
     */
    public void unlink(String model, Integer[] ids, IOdooResponse callback) {
        OArguments arguments = new OArguments();
        arguments.addItems(Arrays.asList(ids));

        call_kw(model, "unlink", arguments, callback);

    }

    /**
     * Responsible to call specified model's method with arguments.
     *
     * @param model     required model name
     * @param method    required model's method name to be called
     * @param arguments arguments for method
     * @param callback  response call back
     */
    public void call_kw(String model, String method, OArguments arguments, IOdooResponse callback) {
        String url = serverHost + "/web/dataset/call_kw/" + model + "/" + method;
        OdooParams params = new OdooParams();
        params.add("model", model);
        params.add("method", method);
        params.add("args", arguments.getArray());
        params.add("kwargs", requestKWArgs);
        params.add("context", requestContext);
        try {
            call(url, params, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call controller of backend server.
     * Note: Only JSON Type controllers supported
     *
     * @param url      Full url path of controller with server url
     * @param params   parameters to pass for controller
     * @param callback result response callback
     */
    public void callController(String url, OdooParams params, IOdooResponse callback) {
        try {
            call(url, params, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Responsible to make all rpc calls to server.
     *
     * @param url              Request url
     * @param params           Custom parameters for request
     * @param responseCallback Response callback if required.
     * @throws JSONException throws if invalid json found
     */
    private void call(String url, JSONObject params, IOdooResponse responseCallback) throws JSONException {
        JSONObject requestData = createRequestData(params);
        final int uuid = requestData.getInt("id");
        // Registering response queue
        if (responseCallback != null) {
            responseCallback.uuid = uuid;
            responseCallback.requestingURL = url;
            responseQueue.add(uuid, responseCallback);
        }
        if (synchronizedRequests) {
            RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
            // Creating request
            JsonObjectRequest objectRequest = new JsonObjectRequest(url, requestData, requestFuture, requestFuture) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getRequestHeader(uuid, super.getHeaders());
                }
            };
            // Setting retry policies
            objectRequest.setRetryPolicy(new DefaultRetryPolicy(newRequestTimeout, newRequestMaxRetry,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adding to queue
            requestQueue.add(objectRequest);

            try {
                onResponse(requestFuture.get(OdooClient.REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                if (errorListener != null) {
                    OdooErrorException error = new OdooErrorException(e.getMessage(),
                            OdooErrorException.ErrorType.UnknownError);
                    if (e instanceof TimeoutException) {
                        error.errorType = OdooErrorException.ErrorType.TimeOut;
                    }
                    if (e instanceof ExecutionException || e instanceof InterruptedException) {
                        error.errorType = OdooErrorException.ErrorType.ConnectionFail;
                    }
                    error.requestingURL = url;
                    error.requestUUID = uuid;
                    errorListener.onError(error);
                }
            }
            cleanRequestData();
            return;
        }

        // Creating request
        JsonObjectRequest objectRequest = new JsonObjectRequest(url, requestData, this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getRequestHeader(uuid, super.getHeaders());
            }
        };
        // Setting retry policies
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(newRequestTimeout, newRequestMaxRetry,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding to queue
        requestQueue.add(objectRequest);
        cleanRequestData();
    }

    /**
     * Volley Request response (JSON)
     *
     * @param response JSON Object with response data
     */
    @Override
    public void onResponse(JSONObject response) {

        // Validating result before parsing in gson
        try {

            // This will make result:[] to result: {result:[]} or
            // result: <number> to result: {result: <number>}
            // result: <boolean> to result: {result: <boolean>}
            if (response.has("result")) {
                if (response.get("result") instanceof JSONArray ||
                        response.get("result") instanceof Integer ||
                        response.get("result") instanceof Boolean) {
                    response.put("result", new JSONObject().put("result", response.get("result")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        OdooResponse odooResponse = gson.fromJson(response.toString(), OdooResponse.class);
        IOdooResponse resultCallback = responseQueue.get(odooResponse.id);
        if (resultCallback != null) {

            // Handling error
            if (odooResponse.error != null) {
                OdooErrorException errorException = OdooErrorException.parse(odooResponse.error);
                errorException.requestingURL = resultCallback.requestingURL;
                errorException.requestUUID = resultCallback.uuid;

                if (!resultCallback.onError(errorException)) {
                    if (errorListener != null) {
                        errorListener.onError(errorException);
                    } else {
                        String errorMessage = "ERROR: \n";
                        errorMessage += "**** " + errorException.getMessage() + " ****\n";
                        errorMessage += "on " + resultCallback.requestingURL + " [" + resultCallback.uuid + "]\n";
                        Log.e(TAG, errorMessage);
                    }
                }
                return;
            }

            // Processing result
            if (odooResponse.result != null) {
                resultCallback.onResult(odooResponse.result);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (errorListener == null)
            return;
        int statusCode = -404;
        if (error.networkResponse != null) {
            statusCode = error.networkResponse.statusCode;
        }
        OdooErrorException.ErrorType errorType = OdooErrorException.ErrorType.UnknownError;
        switch (statusCode) {
            case HttpURLConnection.HTTP_NOT_FOUND:
                errorType = OdooErrorException.ErrorType.NotFound;
                break;
            case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                errorType = OdooErrorException.ErrorType.TimeOut;
                break;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                errorType = OdooErrorException.ErrorType.BadRequest;
                break;
            case -404:
                errorType = OdooErrorException.ErrorType.NoConnection;
                break;
        }
        String message = error.getMessage() != null ? error.getMessage() :
                mContext.getString(R.string.error_odoo_server_error);
        OdooErrorException odooErrorException = new OdooErrorException(message, errorType);
        odooErrorException.statusCode = statusCode;
        errorListener.onError(odooErrorException);
    }

    /**
     * Creates new request header for each request. If session available it will fill header
     * with session id.
     * Every request store requestURL so it can be used if there is VollyError
     *
     * @param uuid   Unique request uuid
     * @param header Default headers from request
     * @return new header map with added headers
     */
    private Map<String, String> getRequestHeader(int uuid, Map<String, String> header) {
        if (header == null || header.equals(Collections.emptyMap())) {
            header = new HashMap<>();
        }
        if (sessionId != null) {
            header.put("Cookie", "session_id=" + sessionId);
        }
        return header;
    }

    /**
     * Generates required payload with user's custom parameters. Odoo Uses standard jsonrpc 2.0
     * architecture for calling to data on server
     *
     * @param params Payload parameters passed for calling method/controller on server
     * @return JSONObject with basic payload parameters and custom parameters object passed by user
     * @throws JSONException throws if json format invalid
     */
    private JSONObject createRequestData(JSONObject params) throws JSONException {
        JSONObject requestData = new JSONObject();

        // Every request have unique uuid to identify response of request by that uuid.
        int uuid = Math.abs(new Random().nextInt(9999));
        params = params != null ? params : new JSONObject();

        requestData.put("jsonrpc", "2.0");
        requestData.put("method", "call");
        requestData.put("params", params);
        requestData.put("id", uuid);

        return requestData;
    }

    //--------------------------------------------------------------------
    // Utility methods for connector client
    //--------------------------------------------------------------------

    private void bindDetailsFromUserObject(OdooUser user) {
        sessionId = user.sessionId;
        odooVersion = user.odooVersion;
    }

    /**
     * Clean request temporary values
     */
    private void cleanRequestData() {

        // Request timeout and max retry
        newRequestTimeout = OdooClient.REQUEST_TIMEOUT_MS;
        newRequestMaxRetry = OdooClient.DEFAULT_MAX_RETRIES;

        // cleaning request arguments
        requestKWArgs = new OdooParams();
        requestContext = new OdooParams();
    }

    /**
     * Create basic user context for request
     *
     * @return JSONObject with user details
     * @throws JSONException if json data create fail
     */
    private JSONObject getUserContext() throws JSONException {
        JSONObject context = new JSONObject();
        context.put("lang", odooUser.lang);
        context.put("tz", odooUser.tz);
        context.put("uid", odooUser.uid);
        return context;
    }

    //--------------------------------------------------------------------
    // Methods used for updating request parameters
    //--------------------------------------------------------------------

    /**
     * Changing request retry policy for volley
     *
     * @param requestTimeout request timeout
     * @param maxRetry       maximum retry if request timeout
     * @return client object
     */
    public T withRetryPolicy(Integer requestTimeout, Integer maxRetry) {
        newRequestTimeout = requestTimeout;
        newRequestMaxRetry = maxRetry;
        return (T) this;
    }

    /**
     * Adding request kwargs with request
     *
     * @param kwargs JSONObject with kwargs value
     * @return client object with new value
     */
    public T withKWArgs(OdooParams kwargs) {
        this.requestKWArgs = kwargs;
        return (T) this;
    }

    /**
     * Adding extra context value with request
     *
     * @param context Request context key value pair
     * @return client object with new context value for request
     */
    public T withContext(OdooParams context) {
        this.requestContext = context;
        return (T) this;
    }
}
