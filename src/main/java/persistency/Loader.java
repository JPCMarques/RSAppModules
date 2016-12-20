package persistency;

import jdk.internal.util.xml.impl.Input;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;

/**
 * Created by joaop on 07/12/2016.
 */
public class Loader {
    protected Unmarshaller unmarshaller;
    protected InputStream inputStream;
    protected Class<?> targetClass;

    public Loader(InputStream inputStream, Class<?> targetClass){
        this.inputStream = inputStream;
        this.targetClass = targetClass;
    }

    public JAXBContext generateContext() throws JAXBException {
        return JAXBContext.newInstance(targetClass);
    }

    public void generateUnmarshaller() throws JAXBException {
        unmarshaller = generateContext().createUnmarshaller();
    }

    public final Object load() throws JAXBException {
        generateUnmarshaller();
        return unmarshaller.unmarshal(inputStream);
    }

}

