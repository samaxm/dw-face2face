package online.decentworld.face2face.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormater {
	private static SimpleDateFormat formater=new SimpleDateFormat("yyMMddHHmmss");
	private static SimpleDateFormat wxformater=new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat readable_formater=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String format(Date date){
		return formater.format(date);
	}
	public static Date paras(String str) throws ParseException{
		return formater.parse(str);
	}
	public static Date parseReadableString(String str) throws ParseException{
		return readable_formater.parse(str);
	}
	
	public static String formatWX(Date date){
		return wxformater.format(date);
	}
	public static String formatReadableString(Date date){
		return readable_formater.format(date);
	}
	
	
}
