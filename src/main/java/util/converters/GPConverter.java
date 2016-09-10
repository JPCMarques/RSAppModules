package util.converters;

/**
 * Created by jpcmarques on 27-06-2016.
 */
public abstract class GPConverter {
    public static double convert(String rsGPString){
        double value;
        if(rsGPString.contains(",")) rsGPString = rsGPString.replace(",", "");

        if(rsGPString.contains("m"))
            rsGPString = rsGPString.replace("m", "000000");
        else if(rsGPString.contains("k"))
            rsGPString = rsGPString.replace("k", "000");
        else if(rsGPString.contains("b"))
            rsGPString = rsGPString.replace("b", "000000000");

        value = Double.parseDouble(rsGPString);
        return value;
    }
}
