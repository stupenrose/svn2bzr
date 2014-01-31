import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class LogEntry {
	@XmlAttribute
	public Integer revision;
	@XmlElement
	public String msg, author, date;
	
	public SvnTime timestamp(){
		return new SvnTime(date);
	}
	
}
