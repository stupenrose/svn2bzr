import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SvnTime{
	public final String year, month, day, hour, minute, seconds, millis, zone;
	public SvnTime(String text) {
		Matcher matcher = Pattern.compile("(....)-(..)-(..)T(..):(..):(..)\\.([0-9]*)(.*)").matcher(text);
		if(!matcher.find()){
			throw new RuntimeException("Invalid date: " + text);
		}
		
		this.year = matcher.group(1);
		this.month = matcher.group(2);
		this.day = matcher.group(3);
		this.hour = matcher.group(4);
		this.minute = matcher.group(5);
		this.seconds = matcher.group(6);
		this.millis = matcher.group(7);
		this.zone = matcher.group(8);
		
		
	}
}