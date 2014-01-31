import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class Main {
	private static void assertEqual(String expected, String actual){
		if(!expected.equals(actual)){
			throw new RuntimeException("expected " + expected + " but was " + actual);
		}
	}
	
	public void runTests(){

		SvnTime time = new SvnTime("2006-03-01T22:40:16.933875Z");
		
		assertEqual(time.year, "2006");
		assertEqual(time.month, "03");
		assertEqual(time.day, "01");
		assertEqual(time.hour, "22");
		assertEqual(time.minute, "40");
		assertEqual(time.seconds, "16");
		assertEqual(time.millis, "933875");
		assertEqual(time.zone, "Z");
		
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.set(
				Integer.parseInt(time.year), 
				Integer.parseInt(time.month)-1, 
				Integer.parseInt(time.day), 
				Integer.parseInt(time.hour),
				Integer.parseInt(time.minute),
				Integer.parseInt(time.seconds)
				);
		
		System.out.println(toBzrFormat(time));
	}
	
	public static void main(String[] args) throws Exception {
		
		final String path = args[0];
		final int lastRevision = Integer.parseInt(args[1]);
		
		final File dest = new File(args[2]);
		final String xml = exec( "svn", "log", path + "@" + lastRevision, "--stop-on-copy", "--xml");
		final SvnLog log = SvnLog.parse(xml);
		
		System.out.println(log.entries.size() + " entries");
		
		Collections.reverse(log.entries);
		
		final LogEntry first = first(log.entries);

		dest.mkdirs();
		exec(dest, "bzr", "init", ".");
		exec(dest, "bzr", "ignore", ".svn");
		exec("svn", "co" , path + "@" + first.revision, dest.getAbsolutePath());
		
		for(LogEntry entry : log.entries){
			System.out.println(entry.revision + ": " + entry.author + " (" + entry.date + ")");
			exec(dest, "svn", "update", "-r", entry.revision.toString());
			exec(dest, "bzr", "add");
			exec(dest, "bzr", "commit", "--unchanged", "-m", entry.msg, "--author=" + entry.author, "--commit-time=" + toBzrFormat(entry.timestamp()));
		}
		

	}

	private static String toBzrFormat(SvnTime time) {
		if(!time.zone.equals("Z")){
			throw new RuntimeException("Not sure how to deal with this timezone: " + time.zone);
		}
		
		String result = time.year + "-" +  time.month + "-" + time.day + " " + time.hour + ":" + time.minute + ":" + time.seconds + " +0000";
		return result;
	}
	
	private static <T> T first(List<T> items){
		return items.get(0);
	}

	private static String exec(File where, String ... command) throws Exception {
		return exec(new ProcessBuilder(command)
						.directory(where));
	}

	private static String exec(String ... command) throws Exception {
		return exec(new ProcessBuilder(command));
	}
	
	private static String exec(final ProcessBuilder pb) throws IOException,
			InterruptedException {
		Process p = pb.start();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		InputStream in = p.getInputStream();
		copy(out, in);
		out.close();
		in.close();
		final int exitCode = p.waitFor();

		if(exitCode!=0) {
			throw new RuntimeException("Process exited " + exitCode + ":\n" + toString(p.getErrorStream()) + "(" + pb.command() + ")");
		}
		
		return new String(out.toByteArray());
	}
	
	private static String toString(InputStream in){
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			copy(out, in);
			in.close();
			out.close();
			return new String(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void copy(ByteArrayOutputStream out, InputStream in){
		try {
			for(int b = in.read();b!=-1;b = in.read()){
				out.write(b);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
