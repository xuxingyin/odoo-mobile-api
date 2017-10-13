package oogbox.api.odoo.client.builder;

import oogbox.api.odoo.OdooClient;
import oogbox.api.odoo.client.helper.utils.ODomain;
import oogbox.api.odoo.client.helper.utils.OdooFields;

/**
 * Responsible to create requests for Odoo Client
 */
public class RequestBuilder {

    private OdooClient client;
    private String modelName;
    private ODomain domain = new ODomain();
    private OdooFields fields = new OdooFields();
    private int offset = 0, limit = 80;
    private String sorting = null;

    private RequestBuilder(OdooClient client, String forModel) {
        this.client = client;
        this.modelName = forModel;
    }

    public static RequestBuilder init(OdooClient client, String forModel) {
        return new RequestBuilder(client, forModel);
    }

    public OdooClient getClient() {
        return client;
    }

    public String getModelName() {
        return modelName;
    }

    public ODomain getDomain() {
        return domain == null ? new ODomain() : domain;
    }

    public OdooFields getFields() {
        return fields != null ? fields : new OdooFields("id");
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public String getSorting() {
        return sorting;
    }

    public RequestBuilder withDomain(ODomain domain) {
        this.domain = domain;
        return this;
    }

    public RequestBuilder withFields(String... fields) {
        this.fields = new OdooFields(fields);
        return this;
    }

    public RequestBuilder setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public RequestBuilder setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public RequestBuilder withSorting(String sort) {
        sorting = sort;
        return this;
    }

    public DataResponse build() {
        return new DataResponse(this);
    }


}
