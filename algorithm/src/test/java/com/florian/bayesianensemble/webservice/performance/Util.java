package com.florian.bayesianensemble.webservice.performance;

import com.florian.nscalarproduct.data.Attribute;
import com.florian.nscalarproduct.data.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.florian.vertibayes.util.PrintingPress.printARFF;

public class Util {
    public static void dataToArff(Data d, String path) {
        List<String> data = new ArrayList<>();
        String s = "@Relation genericBIFF";
        data.add(s);

        List<List<Attribute>> attributes = d.getData();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute a = attributes.get(i).get(0);
            s = "";
            s += "@Attribute";
            s += " " + a.getAttributeName() + " ";
            if (a.getType() == Attribute.AttributeType.string) {
                s += "{";
                int count = 0;
                Set<String> uniqueValues =
                        Data.getUniqueValues(d.getAttributeValues(a.getAttributeName()));
                for (String unique : uniqueValues) {
                    if (!unique.equals("?")) {
                        //only print valid values here, otherwiseweka will think ? is also valid.
                        if (count > 0) {
                            s += ",";
                        }
                        count++;
                        s += unique;
                    }
                }
                s += "}";
            } else {
                s += a.getType();
            }
            data.add(s);
        }
        

        data.add("");
        data.add("@DATA");

        for (int j = 0; j < d.getNumberOfIndividuals(); j++) {
            String ind = "";
            for (int i = 0; i < attributes.size(); i++) {

                if (ind.length() > 0) {
                    ind += ",";
                }
                ind += attributes.get(i).get(j).getValue();

            }
            data.add(ind);
        }

        printARFF(data, path);
    }
}
