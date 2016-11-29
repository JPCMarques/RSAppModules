package util.converters;

/**
 * Created by jpcmarques on 09-09-2016.
 */
public abstract class NumberConverter {
    public static double toDouble(String data){
        if(data.contains(";") || data.contains(",")) return multipleRangeConversion(data);
        else return singleRangeConversion(data);
    }

    public static double multipleRangeConversion(String data){
        String[] ranges = data.contains(",") ? data.split(",") : data.split(";");
        double avg = 0;
        for(String range: ranges){
            avg += singleRangeConversion(range);
        }
        avg /= ranges.length;
        return avg;
    }

    public static double singleRangeConversion(String data){
        data = data.replace(",", "");
        double avg = 0;
        if(data.contains("-")){
            String[] sides = data.split("-");
            avg = (Double.parseDouble(sides[0]) + Double.parseDouble(sides[1]))/2;
        }
        else avg = Double.parseDouble(data);
        return avg;
    }

}
