package AdventureModel;

import java.util.ArrayList;
import java.util.Iterator;

public class keyIterator {

    Iterator<String> iterator;

    public Boolean matchKey(ArrayList<String> inventory_name,String ObjName) {
        this.iterator = inventory_name.iterator();

        while (this.iterator.hasNext()){
            String name = this.iterator.next();
            if (name.equals(ObjName)) {
                return true;
            }

        }
        return false;

    }
}
