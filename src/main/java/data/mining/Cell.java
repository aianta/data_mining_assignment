package data.mining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Cell {

    private static final Logger log = LoggerFactory.getLogger(Cell.class);

    List<CellValue> values = new ArrayList<>();

    public static Cell computeCell(List<Cell> data){

        Cell result = new Cell();
        int measure = data.stream()
                .mapToInt(c->Integer.parseInt(c.get("Sales_Units")))
                .sum();

        CellValue measureValue = new CellValue();
        measureValue.setName("Sales_Units");
        measureValue.setValue(Integer.toString(measure));
        result.addValue(measureValue);

        return result;
    }

    public static Cell computeCell(Predicate<Cell> predicate, List<Cell> data){

        Cell result = new Cell();

        int measure = data.stream()
                .filter(predicate)
                .map(c->{

                    //If we haven't set up our cell's dimensions yet
                    if(result.getValues().size() == 0){

                        //Copy the dimensionality of the data
                        for (CellValue cv: c.getValues()){
                            //Exclude measure value
                            if(!cv.getName().equals("Sales_Units")){
                                result.addValue(cv);
                            }

                        }

                    }



                    //Pass the data cell along for aggregation
                    return c;
                })
                .mapToInt(c->Integer.parseInt(c.get("Sales_Units")))
                .sum();

        //Ignore 0 sale results
        if(measure != 0){

            CellValue measureValue = new CellValue();
            measureValue.setName("Sales_Units");
            measureValue.setValue(Integer.toString(measure));
            result.addValue(measureValue);

            return result;
        }
        return null;
    }


    public void addValue(CellValue v){
        values.add(v);
    }

    /**Imports data from a CSV record
     *
     * @param headers of the CSV file
     * @param csvLine line of CSV data
     */
    public void importValues(String headers,String csvLine){

        //Clear previous values
        values = new ArrayList<>();

        //Get header and value arrays
        String [] headersArray = headers.split(",");
        String [] valuesArray = csvLine.split(",");

        //Create appropriate cell values and store them
        for (int i = 0; i < headers.split(",").length; i++){
            String header = headersArray[i];
            String value = valuesArray[i];

            //Ignore RecordID field
            if(!header.equals("Record_ID")){

                CellValue cv = new CellValue();
                cv.setName(header);
                cv.setValue(value);

                values.add(cv);
            }


        }

    }

    public List<CellValue> getValues(){
        return values;
    }

    public String get(String name){
        for (CellValue v: values){
            if(v.getName().equals(name)){
                return v.getValue();
            }
        }

        return null;
    }

    public String toString(){
        String result = "";

        for(CellValue cv: getValues()){
            result += String.format("%25s", cv.getValue());
        }

        return result + "\n";
    }

    public String toNoCountryString(){
        String result = "";

        for(CellValue cv: getValues()){
            if(!cv.getName().equals("Country")){
                result += String.format("%25s", cv.getValue());
            }
        }

        return result + "\n";
    }

    public String toDebugString(){
        String result = "";

        for (CellValue cv: getValues()){
            result += cv.getName() + ": " + cv.getValue() + " ";
        }

        return result + "\n";
    }

    /** Prune data outside the dimension list
     *
     * @param dims only data from these dimensions will remain
     */

    public void prune(List<Dimension> dims){

        List<CellValue> newValues = new ArrayList<>();

        if (dims.size() == 0){
            for(CellValue cv: values){
                if(cv.getName().equals("Sales_Units")){
                    newValues.add(cv);
                }
            }
        }else{
            for (CellValue cv: values){
                for(Dimension d: dims){
                    if(d.getName().equals(cv.getName()) || cv.getName().equals("Sales_Units")){
                        newValues.add(cv);
                    }
                }

            }
        }
        this.values = newValues;
    }

    public void mergeQuarterYear(){
        for(CellValue cv: values){
            if(cv.getName().equals("Time_Year")){
                cv.setName("Time_Quarter - Time_Year");
                cv.setValue(this.get("Time_Quarter")+"-"+cv.getValue());
            }
        }

        List<CellValue> newValues = new ArrayList<>();

        for(CellValue cv: values){
            if(!cv.getName().equals("Time_Quarter")){
                newValues.add(cv);
            }
        }

        values = newValues;
    }
}
