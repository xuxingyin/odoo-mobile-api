# Odoo Mobile API (Android)

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
[ ![Download](https://api.bintray.com/packages/openongobox/api/odoo-api/images/download.svg?version=1.0.1) ](https://bintray.com/openongobox/api/odoo-api/1.0.1/link)

This project is Android Library for communicating with [Odoo](https://www.odoo.com) backend api. Main goal for this library is to provide integration support with Odoo and android native applications. 

This Library is built upon [Volley](https://developer.android.com/training/volley/index.html) for faster networking requests and works with JSON-RPC api of Odoo.

CHANGELOG
=========

- 1.0.1 (January 2018)
    - Fix: Odoo 8.0 Wrong authentication crash
    - Handling of all error when authenticate request failed.

- 1.0.0 (October 2017)

    - Basic methods for communicating with Odoo API
    - Odoo 8.0+ support
    - Request builder for fetching records from Odoo


- 1.0.0-Alpha (October 2017)

    - Base API for communicating with Odoo server
    - Supported versions: 8.0+


HOW TO START
============

OOG Box Odoo API as simple as you write your java code. To use in android, first add the following dependency to your app level ``build.gradle``

```gradle
compile 'com.oogbox.api:odoo:1.0.1'
```

FEATURES
=========

Basic method support
--------------------
- Synchronized requests support for background services
- Version information
- Active Session information
- Basic Model methods (read, search_read, write, unlink, call_kw)
- Controller (JSON type) call support

Request Builder
----------------

Request builder helps you to fetch records based on offset and limit, single request can be re-call while there is data on server. 

It will automatically set offset based on limiting records per request. Default it takes ``80`` limit to fetch record from server.

GETTING STARTED
===============

Working with ``OdooClient``
----------------------------

``OdooClient`` class helps you to bind your server with api.

```java
OdooClient client = new OdooClient.Builder(context)
                .setHost("http://example.com")
                .build();
```

With the use of ``OdooClient.Builder`` you are allowed to create instance for your odoo server.

``Builder`` have some chaining methods that can be used for tracking instance operations.

``setHost(String host)``
------------------------

Set odoo server url.

**Note:** Server URL is required before performing any operation.

``setSession(String session)``
------------------------------

``setSession()`` will be useful when you have already logged in user available. It contain session_id for user, just pass that session and rest work will be done automatically.

```java
OdooClient client = new OdooClient.Builder(context)
                    .setHost("http://www.example.com")
                    .setSession("f35afb7584ea1195be5400d65415d6ab8f7a9440")
                    .build();
```

When using session_id, if session not expired on server, same session can be used to communicate with backend of odoo without authentication.

``setSynchronizedRequests(Boolean enable)``
-------------------------------------------

Enable/Disable synchronized requests, when pass true, each requests treated as synchronized and you have to manually put your code in background services or background thread.

```java

OdooClient client = new OdooClient.Builder(context)
                    .setHost("http://www.example.com")
                    .setSession("f35afb7584ea1195be5400d65415d6ab8f7a9440")
                    .setSynchronizedRequests(true)
                    .build();

```

``setConnectListener(OdooConnectListener listener)``
----------------------------------------------------

How to detect that ``OdooClient`` connected successfully ? By implimenting ``OdooConnectListener`` you can get Odoo Version information.

```java
OdooClient client = new OdooClient.Builder(null)
                .setHost("http://example.com")
                .setConnectListener(new OdooConnectListener() {
                    @Override
                    public void onConnected(OdooVersion version) {
                        // Success connection
                    }
                }).build();

```

``setErrorListener(OdooErrorListener listener)``
-------------------------------------------------

For all types of global error when communicating with Odoo will be handled by ``OdooErrorListener``

```java
OdooClient client = new OdooClient.Builder(null)
                .setHost("http://example.com")
                .setErrorListener(new OdooErrorListener() {
                    @Override
                    public void onError(OdooErrorException error) {
                        // Error exception with detail of errors
                    }
                })
                .setConnectListener(new OdooConnectListener() {
                    @Override
                    public void onConnected(OdooVersion version) {
                        // Success connection
                    }
                }).build();

```

**After connecting sucessful you can perform your operations like authentication,
fetching data, database listing and other...**

``getDatabases()`` or ``getDatabases(IOdooResponse response)``
--------------------------------------------------------------

For listing all available databases on server, you can go through two methods.

**``getDatabases()``**

Contains list of databases, filled up when connection success.

**``getDatabases(IOdooResponse response)``**

New request for getting databases, in callback response you will get ``OdooResult`` containing database list array.

```java
......
@Override
public void onConnected(OdooVersion version) {
    List<String> databases = client.getDatabases();
}
......

......
client.getDatabases(new IOdooResponse() {
    @Override
    public void onResult(OdooResult result) {
        List<String> dbList = result.getArray("result");
    }
});
......

```


Authenticating
--------------

Odoo required authentication before fetching data from backend models. Todo that, user need to use their credetials **Username/Email**, **Password** and **Database**

With ``OdooClient`` you can use ``authenticate()`` method for getting basic user detail and making session between android and odoo.

``authenticate(String username, String password, String database, AuthenticateListener listener)``

``AuthenticateListener`` impliment two methods ``onLoginSuccess(OdooUser user)`` and ``onLoginFail(AuthError error)``

```java
OdooClient client = new OdooClient.Builder(context)
                    .setHost("https://www.example.com")
                    .setConnectListener(new OdooConnectListener() {
                        @Override
                        public void onConnected(OdooVersion version) {
                            client.authenticate("user","pass", "db", loginCallback);
                        }
                    }).build();

AuthenticateListener loginCallback = new AuthenticateListener() {
    @Override
    public void onLoginSuccess(OdooUser user) {

    }

    @Override
    public void onLoginFail(AuthError error) {

    }
};
```

Reading data from server
========================

**Required Authentication**

``read()``
----------

Reading specified records from model. It takes IDs as list and Fields list to fetch record from server.

Use when you have fix ids' data to read from model.

**Syntax:**

```java

void read(String model, List<Integer> ids, List<String> fields, IOdooResponse callback);

```

**Example:**

```java

List<Integer> ids = Arrays.asList(1, 2, 3);
List<String> fields = Arrays.asList("id", "name");

client.read("res.partner", ids, fields, new IOdooResponse() {
    @Override
    public void onResult(OdooResult result) {
        OdooRecord[] records = result.getRecords();

        for(OdooRecord record: records) {
            Log.v("Name:", record.getString("name"));
        }
    }
});

```

``searchRead()``
----------------

Used to read records from server with custom domain filtering, fields, offset, limit and sorting of data.

**Syntax:**

```java

void searchRead(String model, ODomain domain, OdooFields fields, int offset, int limit, String sort, IOdooResponse callback)

```

Here,

``ODomain domain`` is set of conditions to applied when fetching data from server uses prefix unary mechanism same as <a href="https://www.odoo.com" target="_blank">Odoo</a>

**Example:**

```java

ODomain domain = new ODomain();
domain.add("name", "like", "a%");

OdooFields fields = new OdooFields();
fields.addAll("id", "name", "city", "mobile");

int offset = 0;
int limit = 80;

String sorting = "name DESC";

client.searchRead("res.partner", domain, fields, offset, limit, sorting, new IOdooResponse() {
    @Override
    public void onResult(OdooResult result) {
        OdooRecord[] records = result.getRecords();
        for (OdooRecord record : records) {
            Log.e(">>", record.getString("name"));
        }
    }
});


```

Creating record on server
=========================

**Required Authentication**

``create()``
------------

Create record on server and return new created server id in response result.

**Syntax:**

```java
void create(String model, OdooValues values, IOdooResponse response);
```

Here,
``OdooValues`` is set of key-value pair data

**Example:**

```java

OdooValues values = new OdooValues();
values.put("name", "OOG Box");
values.put("is_company", true);
values.put("email", "hello@oogbox.com");

client.create("res.partner", values, new IOdooResponse() {
    @Override
    public void onResult(OdooResult result) {
        int serverId = result.getInt("result");
    }
});

```

Updating, deleting record from server
=====================================

**Required Authentication**

``write()``
-----------

Write or Update specified records with values for given server record ids.

**Syntax:**

```java
void write(String model, Integer[] ids, OdooValues values, IOdooResponse response)
```

**Example:**

```java
OdooValues values = new OdooValues();
values.put("name", "OOG Box");
values.put("country", "India");

client.write("res.partner", new Integer[]{10}, values, new IOdooResponse() {
    @Override
    public void onResult(OdooResult result) {
        // Success response
    }
});
```

``unlink()``
------------

Remove record from server permanently.

**Syntax:**

```java
void unlink(String model, Integer[] ids, IOdooResponse response)
```

**Example:**

```java
client.unlink("res.partner", new Integer[]{10, 11}, new IOdooResponse() {
    @Override
    public void onResult(OdooResult result) {
        // Success response
    }
});
```


Calling controller of type JSON
===============================

Yes, you can call controllers of odoo with type JSON.

**Syntax:**

```java

void callController(String fullURL, OdooParams params, IOdooResponse response)

```

Here,

- **Server URL** must be full uri, e.g., ``http://www.example.com/my/controller``
- ``OdooParams`` are key-value pair data to pass in controller method

**Example:**

```java

OdooParams params = new OdooParams();
params.add("name", "OOG Box");
params.add("device", "Android");

client.callController(client.getServerHost() + "/my/controller", params, new IOdooResponse() {
    @Override
    public void onResult(OdooResult result) {
        // response from controller
    }
});

```

Calling model methods
=====================

There are some method you need to call from android, by use of ``call_kw`` you can call any method of model.

**Syntax:**

```java

void call_kw(String model, String method, OArguments arguments, IOdooResponse response)

```

Here,

- ``OArguments`` are method arguments 

**Syntax:**

```java

OArguments arguments = new OArguments();
arguments.add("OOG Box");

client.call_kw("res.partner", "create_name", arguments, new IOdooResponse() {
    @Override
    public void onResult(OdooResult result) {
        // response        
    }
});

```

## NOTE

Request Error Handling
-----------------------

Every ``IOdooResponse`` have optional error response callback method. You can specify it by overriding ``onError`` method in ``IOdooResponse`` implimentation

**Example**

```java
client.unlink("res.partner", new Integer[]{10, 11}, new IOdooResponse() {
    @Override
    public void onResult(OdooResult result) {
        // Success response
    }

    @Override
    public boolean onError(OdooErrorException error) {
        // If you handle error by your self, you can return true
        return true;
    }
});
```

Security for Operations (Access Rights)
---------------------------------------

Don't worry for access rights, every JSON Request treated by Odoo Server, and server will check for all access rights for user.

If Access failed, you got response in ``onError``


GIVE FEEDBACK
=============

- Please report bug or isssues https://github.com/oogbox/odoo-mobile-api/issues
- or write us on hello@oogbox.com

Follow us on Twitter: <a href="https://twitter.com/oogbox">@oogbox</a>
