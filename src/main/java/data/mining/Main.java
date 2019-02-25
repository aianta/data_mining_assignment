package data.mining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String dataFileIn = "Car_Sales_Data_Set.csv";
    private static final String dataFileOut = "Car_Sales_Data_Set_Sorted.csv";

    private static ArrayList<DataRecord> records = new ArrayList<>();
    private static ArrayList<Cell> data = new ArrayList<>();

    public static void main (String args []){

        File fin =  new File(dataFileIn);
        File fout =  new File(dataFileOut);

        try(FileReader fr = new FileReader(fin);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter(fout);
            BufferedWriter bw = new BufferedWriter(fw)
        ){

            String headers =  br.readLine();

            log.info("headers: {}", headers);

            String line = br.readLine();

            while(line != null){

                //log.info(line);
                records.add(DataRecord.fromCSVEntry(line));
                Cell c = new Cell();
                c.importValues(headers, line);
                data.add(c);

                line = br.readLine();
            }

            //Sort records
            records.sort(
                    (DataRecord r1, DataRecord r2) -> {
                        //If the records have different countries, sort by countries
                        if (!r1.getCountry().equals(r2.getCountry())){
                            return r1.getCountry().compareTo(r2.getCountry());
                        }else{
                            //Then if the records have different years, sort by years
                            if (r1.getYear() != r2.getYear()){
                                return ((Integer)r1.getYear()).compareTo((Integer)r2.getYear());
                            }else{
                                //Then if the records have different quarters, sort by quarters
                                return ((Integer)r1.getQuarter()).compareTo((Integer)r2.getQuarter());
                            }
                        }
                    }
            );

            //Re-assign record ids
            IntStream.range(0,records.size())
                    .forEach(i-> records.get(i).setRecordId(i+1));

            //Write out headers
            bw.write(headers + "\n");

            //Write out the records
            records.forEach(r->{
                try{
                    bw.write(r.toCSV());
                }catch (IOException e){
                    log.error("Error writing record to file!");
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            });

            bw.flush();
            fw.flush();

        }catch (IOException ioe){
            log.error("Error reading data file!");
            log.error(ioe.getMessage());
            ioe.printStackTrace();
        }

        /*
        //Create Dimensions
        List<Dimension> dims = new ArrayList<>();
        Dimension country =  new Dimension();
        country.setName("Country");
        dims.add(country);

        Dimension year = new Dimension();
        year.setName("Time_Year");
        dims.add(year);

        Dimension quarter = new Dimension();
        quarter.setName("Time_Quarter");
        dims.add(quarter);

        Dimension manufacturer = new Dimension();
        manufacturer.setName("Car_Manufacturer");
        dims.add(manufacturer);

        records.forEach(r->{
            country.addValue(r.getCountry());
            year.addValue(Integer.toString(r.getYear()));
            quarter.addValue(Integer.toString(r.getQuarter()));
            manufacturer.addValue(r.getManufacturer());
        });

        dims.forEach(d->log.info(d.toString()));

        System.out.println(String.format("%-15s%15s%15s", country.getName(), year.getName(), "Sales_Units"));
        for(String s: cuboid(records, country,year)){
            System.out.println(s);
        }

        */

        for(Cell c: data){
            System.out.print(c.toString());
        }
        log.info("Total Cells: {}", data.size());

        String query = "Country,Time_Quarter - Time_Year,Car_Manufacturer";

        Cuboid c = new Cuboid();
        c.extractDimensions(query);
        c.importCells(data);

        System.out.print(c.toString());

    }

    public static List<String> cuboid(ArrayList<DataRecord> records, Dimension country, Dimension year){

        List<String> results = new ArrayList<>();

        for (String c: country.getValues()){
            for (String y: year.getValues())
                results.add(String.format("%-15s%15s%15d",c,y,calculate(records, c, Integer.parseInt(y))));
        }

        return results;
    }

    public static int calculate(List<DataRecord> records, String country, int year, int quarter, String manufacturer){

        return records.stream()
                .filter(r-> r.getCountry().equals(country) &&
                        r.getYear() == year &&
                        r.getQuarter() == quarter &&
                        r.getManufacturer().equals(manufacturer))
                .mapToInt(r->r.getSalesUnits())
                .sum();

    }

    public static int calculate(List<DataRecord> records, String country, int year, int quarter){
        return  records.stream()
                .filter(r->r.getCountry().equals(country) &&
                        r.getYear() == year &&
                        r.getQuarter() == quarter)
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(List<DataRecord> records, String country, int year){
        return records.stream()
                .filter(r->r.getCountry().equals(country) &&
                        r.getYear() == year)
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(String country, List<DataRecord> records){
        return records.stream()
                .filter(r->r.getCountry().equals(country))
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(List<DataRecord> records){
        return records.stream()
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(List<DataRecord> records, int year){
        return records.stream()
                .filter(r->r.getYear() == year)
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(List<DataRecord> records, int year, int quarter){
        return records.stream()
                .filter(r->r.getYear() == year &&
                        r.getQuarter() == quarter)
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(List<DataRecord> records, String manufacturer){
        return records.stream()
                .filter(r->r.getManufacturer().equals(manufacturer))
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(List<DataRecord> records, String country, String manufacturer){
        return records.stream()
                .filter(r->r.getCountry().equals(country) &&
                        r.getManufacturer().equals(manufacturer))
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(List<DataRecord> records, int year, String manufacturer){
        return records.stream()
                .filter(r->r.getYear() == year &&
                        r.getManufacturer().equals(manufacturer))
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(List<DataRecord> records, int quarter, int year, String manufacturer){
        return records.stream()
                .filter(r->r.getQuarter() == quarter &&
                        r.getYear() == year &&
                        r.getManufacturer().equals(manufacturer))
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }

    public static int calculate(List<DataRecord> records, String country, int year, String manufacturer){
        return records.stream()
                .filter(r->r.getCountry().equals(country) &&
                        r.getYear() == year &&
                        r.getManufacturer().equals(manufacturer))
                .mapToInt(r->r.getSalesUnits())
                .sum();
    }



}
