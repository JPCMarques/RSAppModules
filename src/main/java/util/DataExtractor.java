package util;

import util.exceptions.dataMiner.*;

/**
 * Created by jpcmarques on 07-09-2016.
 */
public abstract class DataExtractor<Input,
        DataChunkContainer extends Iterable<?>,
        DataChunk,
        MinedData>{

    protected Input input;
    protected DataChunkContainer chunkedData;
    protected MinedData unifiedData;
    protected static IDLogger logger = new IDLogger(DataExtractor.class, DataExtractor.class.getName());

    public DataExtractor(Input input){
        this.input = input;
    }

    protected abstract void init();
    protected abstract void validateInput() throws InvalidInputException;
    protected abstract void chunkData() throws InvalidChunkingException;
    protected abstract void validateDataChunk(DataChunk chunk, int index) throws InvalidDataChunkException;
    protected abstract void processDataChunk(DataChunk chunk, int index) throws InvalidDataChunkException, InvalidResultException, InvalidInputException, InvalidChunkingException;
    protected abstract void processChunks() throws InvalidDataChunkException, InvalidResultException, InvalidInputException, InvalidChunkingException;
    protected abstract void validateResult() throws InvalidResultException;
    protected MinedData getResult(){
        return unifiedData;
    };

    public MinedData mine() throws InvalidInputException, InvalidResultException, InvalidDataChunkException, InvalidChunkingException {
        logger.i("initializing data miner...");
        init();
        logger.i("done.");
        logger.i(
                " started data mining process");
        logger.i(" validating input...");
        validateInput();
        logger.i(" input successfully validated.");
        logger.i(" chunking data...");
        chunkData();
        logger.i(" data successfully chunked.");
        logger.i(" processing chunks...");
        processChunks();
        logger.i(" chunks successfully processed.");
        logger.i(" validating result...");
        validateResult();
        logger.i(" result successfully validated.");
        return getResult();
    }


}
