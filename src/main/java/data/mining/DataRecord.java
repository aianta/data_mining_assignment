package data.mining;

import java.util.Arrays;

public class DataRecord {
    private int recordId;
    private String country;
    private int year;
    private int quarter;
    private String manufacturer;
    private int salesUnits;

    public static DataRecord fromCSVEntry(String entry){
        String data [] = entry.split(",");
        DataRecord rec = new DataRecord();
        rec.setRecordId(Integer.parseInt(data[0]));
        rec.setCountry(data[1]);
        rec.setYear(Integer.parseInt(data[2]));
        rec.setQuarter(Integer.parseInt(data[3]));
        rec.setManufacturer(data[4]);
        rec.setSalesUnits(Integer.parseInt(data[5]));

        return rec;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public int getSalesUnits() {
        return salesUnits;
    }

    public void setSalesUnits(int salesUnits) {
        this.salesUnits = salesUnits;
    }

    public String toCSV(){
        String result = recordId + "," +
                country + "," +
                year + "," +
                quarter + "," +
                manufacturer + "," +
                salesUnits + "\n";

        return result;
    }


}
