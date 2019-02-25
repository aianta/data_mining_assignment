package data.mining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Dimension {

    private static final Logger log = LoggerFactory.getLogger(Dimension.class);

    private String name;
    private List<String> values = new ArrayList<>();

    public void setName(String s){
        this.name = s;
    }

    public String getName(){
        return name;
    }

    public void addValue(String v){

        if (!exists(v)){
            values.add(v);
        }
    }

    public List<String> getValues(){
        return values;
    }

    private boolean exists(String v){
        for (String s: values){
            if(v.equals(s)){
                return true;
            }
        }

        return false;
    }

    public String toString(){
        String result = "";

        result += getName() + "\n";

        for(String v: values){
            result += v + "\n";
        }

        return result;
    }

    /** Generates predicates for all distinct values of this dimension
     *
     * @return
     */
    public List<Predicate<Cell>> generatePredicates(){

        List<Predicate<Cell>> result = new ArrayList<>();

        for (String v: values){
            Predicate<Cell> predicate = p->p.get(this.getName()).equals(v);
            result.add(predicate);
        }

        return result;

    }

    /**Generates a list of predicates by concatenating all of this dimension's predicates
     * to each predicate in the input list.
     *
     * @param input predicates to concatenate
     * @return
     */
    public List<Predicate<Cell>> generatePredicates(List<Predicate<Cell>> input){

        List<Predicate<Cell>> result = new ArrayList<>();

        for (Predicate<Cell> inputPredicate: input){
            for (Predicate<Cell> myPredicate: generatePredicates()){
                result.add(inputPredicate.and(myPredicate));
            }
        }

        return result;
    }
}
