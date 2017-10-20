package oogbox.api.odoo.client.helper;

import oogbox.api.odoo.client.helper.data.OdooError;

public class OdooErrorException extends Exception {

    public enum ErrorType {
        ConnectionFail,
        UnsupportedVersion,
        InternalServerError,
        AccessDenied,
        NotFound,
        TimeOut,
        BadRequest,
        NoConnection,
        SessionExpired,
        InvalidDatabase,
        ProgrammingError,
        UnknownError
    }

    public ErrorType errorType = ErrorType.UnknownError;
    public int statusCode = -1;
    public String debugMessage, odooException = null;
    public String requestingURL = null;
    public int requestUUID = 0;


    public static OdooErrorException parse(OdooError error) {
        OdooError data = error.getErrorData();
        String name = null, debugMessage = null, message;
        ErrorType type = ErrorType.InternalServerError;
        message = error.getString("message");
        if (data != null) {
            message = data.getString("message");
            name = data.getString("name");
            debugMessage = data.getString("debug");
            switch (name) {
                case "odoo.exceptions.AccessDenied":
                    type = ErrorType.AccessDenied;
                    break;
                case "odoo.http.SessionExpiredException":
                    type = ErrorType.SessionExpired;
                    break;
                case "psycopg2.OperationalError":
                    type = ErrorType.InvalidDatabase;
                    break;
                case "psycopg2.ProgrammingError":
                    type = ErrorType.ProgrammingError;
                    break;
            }
        }
        OdooErrorException errorException = new OdooErrorException(message, type);
        errorException.odooException = name;
        errorException.debugMessage = debugMessage;
        return errorException;
    }

    public OdooErrorException(String message, ErrorType type) {
        super(message);
        errorType = type;
    }

    @Override
    public String toString() {
        String errorMessage = "Odoo Error: \n\n";
        errorMessage += getMessage() + " on " + requestingURL + " [" + requestUUID + "]\n";
        errorMessage += (odooException != null ? odooException : "") + "\n\n";
        errorMessage += debugMessage;
        return errorMessage;
    }
}
