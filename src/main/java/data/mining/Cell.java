package data.mining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Cell {

    private static final Logger log = LoggerFactory.getLogger(Cell.class);

    ArrayList<CellValue> values = new ArrayList<>();

    public static Cell computeCell(List<Cell> data){
        log.info("WHO THE FUCK IS CALLING ME>!");
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

                        log.info("data cell to copy: \n{}", c.toDebugString());
                        log.info("result cell before copy: \n{}", result.toDebugString());

                        //Copy the dimensionality of the data
                        for (CellValue cv: c.getValues()){
                            //Exclude measure value
                            if(!cv.getName().equals("Sales_Units")){
                                result.addValue(cv);
                            }

                        }

                        log.info("result cell after copy: \n{}", result.toDebugString());
                    }



                    //Pass the data cell along for aggregation
                    return c;
                })
                .mapToInt(c->Integer.parseInt(c.get("Sales_Units")))
                .sum();

        CellValue measureValue = new CellValue();
        measureValue.setName("Sales_Units");
        measureValue.setValue(Integer.toString(measure));
        result.addValue(measureValue);

        log.info("ComputeCell Result:\n{}", result.toDebugString());

        return result;
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
            result += String.format("%20s", cv.getValue());
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

}
