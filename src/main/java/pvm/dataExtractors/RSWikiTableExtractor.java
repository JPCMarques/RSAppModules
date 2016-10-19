package pvm.dataExtractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.DataExtractor;
import util.exceptions.dataMiner.InvalidChunkingException;
import util.exceptions.dataMiner.InvalidDataChunkException;
import util.exceptions.dataMiner.InvalidInputException;
import util.exceptions.dataMiner.InvalidResultException;

import java.io.IOException;

/**
 * Created by jpcmarques on 08-09-2016.
 */
public abstract class RSWikiTableExtractor<Output> extends DataExtractor<String, Elements, Element, Output> {
    protected Document document;
    protected final String classFilter;

    public RSWikiTableExtractor(String input, String classFilter) {
        super(input);
        this.classFilter = classFilter;
    }

    @Override
    protected void validateInput() throws InvalidInputException {
        while(true){
            try {
                document = Jsoup.connect(input).get();
                break;
            } catch (IOException e) {
                try {
                    wait(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void chunkData() throws InvalidChunkingException {
        logger.i("selecting all tables from document with the supplied class: " + classFilter + "...");
        chunkedData = document.getElementsByClass(classFilter);
        if(chunkedData.size() == 0) throw new InvalidChunkingException("No valid chunks found.");
    }

    @Override
    protected void validateDataChunk(Element chunk, int index) throws InvalidDataChunkException {
        //Not needed, will be always valid as it contains the required class.
    }

    @Override
    protected void processChunks() throws InvalidDataChunkException, InvalidResultException, InvalidInputException, InvalidChunkingException {
        for(int i = 0; i < chunkedData.size(); i++){
            Element chunk = chunkedData.get(i);
            try{
                validateDataChunk(chunk, i);
                processDataChunk(chunk, i);
            }catch (InvalidDataChunkException idce){
                //Skip
            }
        }
    }


}
