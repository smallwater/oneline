package com.smart.spider.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public final static String DefaultDateFormat = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 
	 * 获取当前系统的日期时间字符串，默认格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return 返回 yyyy-MM-dd HH:mm:ss 日期时间格式字符串
	 */
	public static String GetDateTime() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DefaultDateFormat);

		return simpleDateFormat.format(new Date());
	}

	/**
	 * 
	 * 将日期datetime字符串格式化为标准日期时间格式 format1 to format2
	 * 
	 * @param datetime
	 *            包含日期时间的字符串
	 * 
	 * @param format1
	 *            原始日期时间字符串格式
	 * 
	 * @param format2
	 *            目标日期时间字符串格式
	 * 
	 * @return 返回 yyyy-MM-dd HH:mm:ss 日期时间格式字符串
	 */
	public static String toDateTime(String datetime, String format1, String format2) {

		try {

			SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(format1);

			Date date = simpleDateFormat1.parse(datetime);

			SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(format2);

			return simpleDateFormat2.format(date);

		} catch (Exception e) {

			e.printStackTrace();

			return GetDateTime();
		}
	}

	/**
	 * 
	 * 将日期datetime字符串格式化为标准日期时间格式
	 * 
	 * @param datetime
	 *            包含日期时间的字符串
	 * 
	 * @param format1
	 *            字符串的日期时间格式
	 * 
	 * @return 返回 yyyy-MM-dd HH:mm:ss 日期时间格式字符串
	 */
	public static String toDateTime(String datetime, String format1) {

		return toDateTime(datetime, format1, DefaultDateFormat);

	}

	/**
	 * 
	 * 将日期datetime字符串格式化为标准日期时间格式
	 * 
	 * @param datetime
	 *            包含日期时间的字符串，默认日式格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return 返回 yyyy-MM-dd HH:mm:ss 日期时间格式字符串
	 */
	public static String toDateTime(String datetime) {

		return toDateTime(datetime, DefaultDateFormat, DefaultDateFormat);

	}

	/**
	 * 
	 * 根据正则表达式，将字符串格式化为标准日期时间类型
	 * 
	 * @param datetime
	 *            包含日期时间的字符串
	 * @param regex
	 *            提取日期时间的正则表达式
	 * @param format
	 *            增则表达式提取出来的日期时间格式
	 * @return 返回 yyyy-MM-dd HH:mm:ss 日期时间格式字符串
	 */
	public static String toRegexDateTime(String datetime, String regex, String format) {

		try {

			String dateRegexString = RegexUtil.Match(datetime, regex);

			if (dateRegexString == null) {
				throw new Exception("dateRegexString未实例化");
			}

			return toDateTime(dateRegexString, format);

		} catch (Exception e) {

			e.printStackTrace();

			return GetDateTime();
		}
	}

	/**
	 * 
	 * 根据正则表达式，将字符串格式化为标准日期时间类型
	 * 
	 * @param datetime
	 *            包含日期时间的字符串
	 * @param regex
	 *            提取日期时间的正则表达式，默认正则提出出来的日期时间格式为：yyyy-MM-dd HH:mm:ss
	 * @return 返回 yyyy-MM-dd HH:mm:ss 日期时间格式字符串
	 */
	public static String toRegexDateTime(String datetime, String regex) {

		return toRegexDateTime(datetime, regex, DefaultDateFormat);

	}

	/**
	 * 
	 * 根据默认正则表达式，返回格式化的日期时间字符串格式
	 * 
	 * 默认正则表达式：[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}
	 * 
	 * 默认正则表达式提取出来的日期时间格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param datetime
	 *            包含日期时间的字符串
	 * 
	 * @return 返回 yyyy-MM-dd HH:mm:ss 日期时间格式字符串
	 */
	public static String toRegexDateTime(String datetime) {

		return toRegexDateTime(datetime, "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}", DefaultDateFormat);

	}

	/**
	 * 
	 * datetime to timestamp
	 * 
	 * @param datetime
	 * @param format
	 * @return
	 */
	public static long toTimeStamp(String datetime, String format) {

		try {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

			Date date = simpleDateFormat.parse(datetime);

			return date.getTime();

		} catch (Exception e) {

			e.printStackTrace();

			return System.currentTimeMillis();

		}
	}

	public static long toTimeStamp(String datetime) {

		return toTimeStamp(datetime, DefaultDateFormat);

	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	/**
	 * @Title: GetDateTimeByPattern
	 * @Description: TODO
	 * @param @param
	 *            date 当前日期
	 * @param @param
	 *            pattern 格式
	 * @param @param
	 *            count 天数
	 * @param @param
	 *            type +or-
	 * @return String
	 */
	public static String GetDateTimeByPattern(Date date, String pattern, int count) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date dBefore = new Date();

		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -count); // 设置为前一天
		dBefore = calendar.getTime();
		return simpleDateFormat.format(dBefore);
	}

	/**
	 * 时间戳转换成时间戳，当传入参数的时分秒为00的场合转换为系统时间的时分秒
	 * 
	 * @param time
	 *            时间戳
	 * @return
	 */
	public static long FormatToTimeStamp(long time) {

		int hours = 0;
		int minute = 0;
		int second = 0;

		SimpleDateFormat f = new SimpleDateFormat(DefaultDateFormat);
		Date tempYMD = null;

		try {

			tempYMD = f.parse(f.format(time));
			Calendar cd = Calendar.getInstance();

			cd.setTime(tempYMD);
			// 时
			hours = cd.get(Calendar.HOUR_OF_DAY);
			// 分
			minute = cd.get(Calendar.MINUTE);
			// 秒
			second = cd.get(Calendar.SECOND);

			// 判断时分秒
			if (hours == 0 && minute == 0 && second == 0) {
				Calendar cd_now = Calendar.getInstance();
				cd_now.setTime(new Date());
				cd.set(Calendar.HOUR_OF_DAY, cd_now.get(Calendar.HOUR_OF_DAY));
				cd.set(Calendar.MINUTE, cd_now.get(Calendar.MINUTE));
				// cd.set(Calendar.SECOND, cd_now.get(Calendar.SECOND));
			}
			return cd.getTimeInMillis();

		} catch (ParseException e) {

			e.printStackTrace();
			return System.currentTimeMillis();

		} catch (Exception e) {

			e.printStackTrace();
			return System.currentTimeMillis();

		}

	}

	/**
	 * 时间戳转换成日期，当传入参数的时分秒为00的场合转换为系统时间的时分秒，日期格式为 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param time
	 * @return
	 */
	public static String FormatToDateTime(long time) {

		int hours = 0;
		int minute = 0;
		int second = 0;

		SimpleDateFormat f = new SimpleDateFormat(DefaultDateFormat);
		Date tempYMD = null;
		try {
			tempYMD = f.parse(f.format(time));
			Calendar cd = Calendar.getInstance();

			cd.setTime(tempYMD);

			// 时
			hours = cd.get(Calendar.HOUR_OF_DAY);

			// 分
			minute = cd.get(Calendar.MINUTE);

			// 秒
			second = cd.get(Calendar.SECOND);

			// 判断时分秒
			if (hours == 0 && minute == 0 && second == 0) {
				Calendar cd_now = Calendar.getInstance();
				cd_now.setTime(new Date());
				cd.set(Calendar.HOUR_OF_DAY, cd_now.get(Calendar.HOUR_OF_DAY));
				cd.set(Calendar.MINUTE, cd_now.get(Calendar.MINUTE));
				// cd.set(Calendar.SECOND, cd_now.get(Calendar.SECOND));
			}

			return f.format(cd.getTime());

		} catch (ParseException e) {

			e.printStackTrace();

			return GetDateTime();

		} catch (Exception e) {

			e.printStackTrace();

			return GetDateTime();

		}
	}
}
