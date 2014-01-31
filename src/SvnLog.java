import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="log")
public class SvnLog {
	
	public static SvnLog parse(String xml){
		try {
			JAXBContext jaxb = JAXBContext.newInstance(SvnLog.class);
			return (SvnLog) jaxb.createUnmarshaller().unmarshal(new StringReader(xml));
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	@XmlElements(
			@XmlElement(name="logentry")
			)
	public List<LogEntry> entries = new ArrayList<LogEntry>();
}
