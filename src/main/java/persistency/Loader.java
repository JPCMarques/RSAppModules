package persistency;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Created by joaop on 07/12/2016.
 */
public class Loader {
    protected Unmarshaller unmarshaller;
    protected String path;
    protected Class<?> targetClass;

    public Loader(String path, Class<?> targetClass){
        this.path = path;
        this.targetClass = targetClass;
    }

    public JAXBContext generateContext() throws JAXBException {
        return JAXBContext.newInstance(targetClass);
    }

    public File generateFile() {
        return new File(path);
    }

    public void generateUnmarshaller() throws JAXBException {
        unmarshaller = generateContext().createUnmarshaller();
    }

    public final Object load() throws JAXBException {
        File file = generateFile();
        generateUnmarshaller();
        return unmarshaller.unmarshal(file);
    }
}

