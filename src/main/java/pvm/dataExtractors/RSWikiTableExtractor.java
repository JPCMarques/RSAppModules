package pvm.dataExtractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.DataExtractor;
import util.exceptions.dataMiner.InvalidChunkingException;
import util.exceptions.dataMiner.InvalidDataChunkException;
import util.exceptions.dataMiner.InvalidInputException;

import java.io.IOException;

/**
 * Created by jpcmarques on 08-09-2016.
 */
public abstract class RSWikiTableExtractor<Output> extends DataExtractor<String, Elements, Element, Output> {
    private Document document;
    private final String classFilter;

    public RSWikiTableExtractor(String input, String classFilter) {
        super(input);
        this.classFilter = classFilter;
    }

    @Override
    protected void validateInput() throws InvalidInputException {
        try {
            document = Jsoup.connect(input).get();
        } catch (IOException e) {
            logger.e("caught exception during input validation: " + e.getMessage());
            throw new InvalidInputException();
        }
    }

    @Override
    protected void chunkData() throws InvalidChunkingException {
        logger.i("selecting all tables from document...");
        chunkedData = document.select("table");
    }

    @Override
    protected void validateDataChunk(Element chunk, int index) throws InvalidDataChunkException {
        boolean includesClass = false;
        if(classFilter.equals("")){
            logger.i("skipping validation (no filter)...");
            return;
        }
        logger.i("validating chunk: \n" + chunk.toString());
        logger.i("checking classes of \"" + chunk.nodeName() + "\"");
        includesClass = chunk.classNames().contains(classFilter);
        if(!includesClass){
            logger.e("\"" + chunk.nodeName() + "\" does not contain required class.");
            throw new InvalidDataChunkException("data chunk (table) does not contain the required class.");
        }
    }

    @Override
    protected void processChunks() throws InvalidDataChunkException {
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
