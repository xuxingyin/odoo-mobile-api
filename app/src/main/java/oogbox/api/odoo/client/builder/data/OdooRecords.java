package oogbox.api.odoo.client.builder.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oogbox.api.odoo.client.builder.DataResponse;
import oogbox.api.odoo.client.helper.data.OdooRecord;

public class OdooRecords {
    private DataResponse dataResponse;
    private List<OdooRecord> records = new ArrayList<>();
    private List<OdooRecord> recentRecords = new ArrayList<>();
    private Integer length = 0;

    public void appendRecords(OdooRecord[] records) {
        this.records.addAll(Arrays.asList(records));
        recentRecords = Arrays.asList(records);
    }

    public void setDBLength(int dbLength) {
        length = dbLength;
    }

    public void setDataResponse(DataResponse dataResponse) {
        this.dataResponse = dataResponse;
    }

    public List<OdooRecord> getAllRecords() {
        return records;
    }

    public List<OdooRecord> getNewRecords() {
        return recentRecords;
    }

    public int recordDBLength() {
        return length;
    }

    public boolean hasNext() {
        return length != records.size();
    }

    public void requestNext() {
        this.dataResponse.requestNext();
    }
}
