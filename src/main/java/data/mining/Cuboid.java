package data.mining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Cuboid {

    private static final Logger log = LoggerFactory.getLogger(Cuboid.class);

    List<Cell> cells = new ArrayList<>();
    List<Dimension> dimensions = new ArrayList<>();
    boolean dimensionsExtracted = false;
    boolean splitIntoTable = false;
    boolean quarterYear = false;

    public void importCells(List<Cell> data){
        if(!dimensionsExtracted){
            log.error("Error importing cells, no dimensions set!");
        }else{


            computeDistincts(data);

            //Prune data
            for(Cell c: data){
                c.prune(dimensions);
            }

            generateCells(data);

            if(quarterYear){
                List<Dimension> newDimensions = new ArrayList<>();

                for(Dimension d: dimensions){
                    if(d.getName().equals("Time_Year")){
                        d.setName("Time_Quarter - Time_Year");
                    }
                    if(!d.getName().equals("Time_Year")&&!d.getName().equals("Time_Quarter")){
                        newDimensions.add(d);
                    }
                }

                this.dimensions = newDimensions;

                for(Cell c: cells){
                    c.mergeQuarterYear();
                }
            }

        }
    }


    /** Idea: Recurse through the dimension values generating appropriate predicates.
     * Should result in a list of predicates of size |A| * |B| * |C|
     * @return
     */
    private void generateCells(List<Cell> data){


            if (dimensions.size() == 0){
                //If we're computing all
                cells.add(Cell.computeCell(data));
            }

            if (dimensions.size() == 1){
                //If we're computing a single dimensional cuboid
                for (Predicate<Cell> p: dimensions.get(0).generatePredicates()){
                    cells.add(Cell.computeCell(p,data));
                }
            }

            if (dimensions.size() > 1){
                //If we're computing a higher dimensional cuboid, compute all appropriate combinations of predicates.
                List<Predicate<Cell>> predicates = dimensions.get(0).generatePredicates();
                for (int i = 1; i < dimensions.size(); i++){
                    predicates = dimensions.get(i).generatePredicates(predicates);
                }

                //log.info("Number of predicates generated: {}", predicates.size());

                //Now, for all generated predicates, compute a cell
                for (Predicate<Cell> p: predicates){
                    Cell c = Cell.computeCell(p,data);
                    if(c != null){
                        cells.add(Cell.computeCell(p,data));
                    }

                }
            }
    }

    private void computeDistincts(List<Cell> data){
        //Compute distinct dimension values
        for (Dimension d: dimensions){
            data.stream()
                    .map(c->c.get(d.getName()))
                    .distinct()
                    .forEach(d::addValue);
        }
    }

    public void extractDimensions(String query, int option){
        if(option == 11 || option == 12){
            splitIntoTable = true;
        }
        if(!query.equals("")){
            for(String s: query.split(",")){
                //Handle time levels stuff
                if (s.equals("Time_Quarter - Time_Year")){

                    quarterYear = true;

                    Dimension d1 = new Dimension();
                    d1.setName("Time_Year");
                    dimensions.add(d1);

                    Dimension d2 = new Dimension();
                    d2.setName("Time_Quarter");
                    dimensions.add(d2);
                }else{
                    Dimension d = new Dimension();
                    d.setName(s);
                    dimensions.add(d);
                }
            }
        }
        //Set flag
        dimensionsExtracted = true;
    }

    public String toString(){
        String result = "";

        //Handle special formatting case
        if (splitIntoTable){
            List<Cell> canadaCells = cells.stream().filter(c->c.get("Country").equals("Canada")).collect(Collectors.toList());
            List<Cell> usCells = cells.stream().filter(c->c.get("Country").equals("United States")).collect(Collectors.toList());

            result += "Canada:\n";
            for (Dimension d: dimensions){
                if(!d.getName().equals("Country")){
                    result += String.format("%25s", d.getName());
                }
            }

            //Add measure header
            result += String.format("%25s", "Sales_Units") + "\n";

            //Print cells
            for(Cell c: canadaCells){
                result += c.toNoCountryString();
            }

            result += "United States:\n";
            for (Dimension d: dimensions){
                if(!d.getName().equals("Country")){
                    result += String.format("%25s", d.getName());
                }
            }

            //Add measure header
            result += String.format("%25s", "Sales_Units") + "\n";

            //Print cells
            for(Cell c: usCells){
                result += c.toNoCountryString();
            }
        }else{
            for (Dimension d: dimensions){
                result += String.format("%25s", d.getName());
            }

            //Add measure header
            result += String.format("%25s", "Sales_Units") + "\n";

            //Print cells
            for(Cell c: cells){
                result += c.toString();
            }
        }

        return result;
    }

}
