package Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestMan {

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format
	
	public static void main(String args[]) throws ParseException{
		for (int i = 0; i<=24; i++){

			String hour = String.format("%02d", i);
			System.out.println(hour);

			Date targetdate = SDF_TS.parse("2008-10-01 "+ hour + ":00:00");
			System.out.println(targetdate);
		}
	}
}
