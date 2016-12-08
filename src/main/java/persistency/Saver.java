package persistency;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.OutputStream;

/**
 * Created by joaop on 30/11/2016.
 */
public class Saver {
    protected Object rawData;
    protected Marshaller marshaller;

    public Saver(Object rawData){
        this.rawData = rawData;
    }

    public JAXBContext generateContext() throws JAXBException {
        return JAXBContext.newInstance(rawData.getClass());
    }

    public void editMarshaller() throws PropertyException {
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    public void generateMarshaller() throws JAXBException {
        marshaller = generateContext().createMarshaller();
        editMarshaller();
    }

    public final void save(OutputStream outputStream) throws JAXBException {
        generateMarshaller();
        marshaller.marshal(rawData, outputStream);
    }
}
