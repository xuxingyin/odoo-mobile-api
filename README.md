# Odoo Mobile API (Android)

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
[ ![Download](https://api.bintray.com/packages/odooongobox/api/odoo-api/images/download.svg?version=1.0.0) ](https://bintray.com/odooongobox/api/odoo-api/1.0.0/link)

This project is Android Library for communicating with [Odoo](https://www.odoo.com) backend api. Main goal for this library is to provide integration support with Odoo and android native applications. 

This Library is built upon [Volley](https://developer.android.com/training/volley/index.html) for faster networking requests and works with JSON-RPC api of Odoo.

Releases
========

1.0.0 (20-October-2017)

- Basic methods for communicating with Odoo API
- Odoo 8.0+ support
- Request builder for fetching records from Odoo


1.0.0-Alpha (15-October-2017)
- Base API for communicating with Odoo server
- Supported versions: 8.0+

Android
=======
To use OOG Box Odoo API in android, first add the following dependency to your app level build.gradle 

	compile 'com.oogbox.api:odoo:1.0.0'
