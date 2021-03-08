package com.ahasan.rest.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AppUtils {
	private static final Logger logger = LoggerFactory.getLogger(AppUtils.class);

	@Autowired
	ResourceLoader resourceLoader;

	public static Pageable getPageable(Integer offset, Integer limit) {
		Pageable pageable;
		if (offset == null || limit == null) {
			offset = 0;
			limit = 10;
			pageable = PageRequest.of(offset, limit);
		} else {
			pageable = PageRequest.of(offset, limit);
		}
		return pageable;
	}

	public static Pageable pageableWithSort(Integer offset, Integer limit, Sort sort) {
		Pageable pageable;
		if (offset == null || limit == null) {
			offset = 0;
			limit = 10;
			pageable = PageRequest.of(offset, limit, sort);
		} else {
			pageable = PageRequest.of(offset, limit, sort);
		}
		return pageable;
	}

	public static Date atStartOfDay(Date date) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
		return localDateTimeToDate(startOfDay);
	}

	public static Date atEndOfDay(Date date) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
		return localDateTimeToDate(endOfDay);
	}

	public static Date subTractFromCurrentDate(Date date, Long days) {
		LocalDateTime localDateTime = dateToLocalDateTime(date).minusDays(days);
		LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
		return localDateTimeToDate(endOfDay);
	}

	private static LocalDateTime dateToLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	private static Date localDateTimeToDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static String calculateAgeYearMonthDayfromDOB(Date birthDate) {
		// direct age calculation
		String ageYearMonthDay = null;
		if (birthDate != null) {

			Calendar c = Calendar.getInstance();
			c.setTime(birthDate);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;
			int date = c.get(Calendar.DATE);
			LocalDate l1 = LocalDate.of(year, month, date);
			LocalDate now1 = LocalDate.now();
			Period diff = Period.between(l1, now1);
			ageYearMonthDay = diff.getYears() + "Y " + diff.getMonths() + "M " + diff.getDays() + "D ";
		}
		return ageYearMonthDay;
	}

	public static Integer calculateAgeInYearsfromDOB(Date birthDate) {
		// direct age calculation
		Integer ageInYears = null;
		if (birthDate != null) {

			Calendar c = Calendar.getInstance();
			c.setTime(birthDate);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;
			int date = c.get(Calendar.DATE);
			LocalDate l1 = LocalDate.of(year, month, date);
			LocalDate now1 = LocalDate.now();
			Period diff = Period.between(l1, now1);
			ageInYears = diff.getYears();
		}
		return ageInYears;
	}

	public static String getDateWithoutTime(Date date) {
		if (date != null) {
			try {
				SimpleDateFormat formatterWithOutTime = new SimpleDateFormat("dd/MM/yyyy");
				return formatterWithOutTime.format(date);
			} catch (Exception ex) {
			}
		}
		return null;
	}

	public static String getDateWithTimeAMorPM(Date date) {

		if (date != null) {
			try {
				SimpleDateFormat formatterWithTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				return formatterWithTime.format(date);
			} catch (Exception ex) {
			}

		}
		return null;
	}

	public static String convertDateToLocalDateTime(Date date) {
		try {
			/* output is 17-02-2019 05:33 PM */
			return new SimpleDateFormat("dd-MM-yyyy hh:mm aaa").format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date convertLocalDateTimeToDate(String string) {
		try {
			/* 17-02-2019 */
			return new SimpleDateFormat("dd-MM-yyyy hh:mm aaa").parse(string);
		} catch (Exception e) {
			return null;
		}
	}

	public static String convertOnlyDateToString(Date date) {
		if (date != null) {
			try {
				SimpleDateFormat formatterWithOutTime = new SimpleDateFormat("dd-MM-yyyy");
				return formatterWithOutTime.format(date);
			} catch (Exception ex) {
			}
		}
		return null;
	}

	public static Date convertOnlyStringToDate(String string) {
		try {
			/* 17-02-2019 */
			return new SimpleDateFormat("dd-MM-yyyy").parse(string);
		} catch (Exception e) {
			return null;
		}
	}

	public static String convertDateToString(Date date, String pattern) {
		if (date != null) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(pattern);
				return formatter.format(date);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static Date dateFromStringDDMMYY(String date) {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(date);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Date convertStringToDate(String dateStr, String pattern) {
		try {
			return new SimpleDateFormat(pattern).parse(dateStr);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Date addHourMinutesSeconds(int hour, int minute, int second, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		calendar.add(Calendar.HOUR_OF_DAY, hour);
		calendar.add(Calendar.MINUTE, minute);
		calendar.add(Calendar.SECOND, second);

		return calendar.getTime();
	}

	public static Date yesterday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public static String getRandomUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static boolean isStringMatchWithDateFormat(String input) {

		boolean checkFormat;

		checkFormat = input.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})");
		return checkFormat;
	}

	/**
	 * @param filePath
	 * @return
	 */
	public String getResoucePath(String filePath) {
		Resource resource = new ClassPathResource(filePath);
		try {
			return resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param filePath
	 * @return
	 */
	public String getResouceAbsolutePath(String filePath) {
		Resource resource = resourceLoader.getResource(filePath);
		try {
			return resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JasperReportExportFormat printFormat(String printFormat) {

		if (printFormat == null) {
			return JasperReportExportFormat.PDF_FORMAT;
		}

		if (printFormat.startsWith("PDF")) {
			return JasperReportExportFormat.PDF_FORMAT;
		} else if (printFormat.startsWith("HTML")) {
			return JasperReportExportFormat.HTML_FORMAT;
		} else if (printFormat.startsWith("DOCX")) {
			return JasperReportExportFormat.DOCX_FORMAT;
		}

		if (printFormat.startsWith("XLSX")) {
			return JasperReportExportFormat.XLSX_FORMAT;
		}

		return JasperReportExportFormat.PDF_FORMAT;
	}

	public static Long stringToLong(String input) {
		long longValue = 0;
		try {
			longValue = Long.parseLong(input);
		} catch (Exception e) {
			logger.info("Error while convert String to long :" + e);
		}
		return longValue;
	}

	public static BigInteger stringToBigInteger(String input) {
		BigInteger bigIntegerValue = null;
		try {
			bigIntegerValue = new BigInteger(input);
		} catch (Exception e) {
			logger.info("Error while convert String to BigInteger :" + e);
		}
		return bigIntegerValue;
	}

	public static BigInteger integerToBiginteger(Integer input) {
		BigInteger bigIntegerValue = null;
		try {
			bigIntegerValue = BigInteger.valueOf(input);
		} catch (Exception e) {
			logger.info("Error while convert Integer to BigInteger :" + e);
		}
		return bigIntegerValue;
	}

	public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
		return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
	}

	public static Date monthStartDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	public static Date monthEndDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return c.getTime();
	}

	public static int getWeekOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public static Date convertToDateViaSqlDate(LocalDate dateToConvert) {
		return java.sql.Date.valueOf(dateToConvert);
	}

	public static Date convertToDateViaInstant(LocalDate dateToConvert) {
		return java.util.Date.from(dateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static <E extends Enum<E>> E lookupEnum(Class<E> e, String id) {
		E r;
		try {
			r = Enum.valueOf(e, id);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException("Invalid value for enum " + e.getSimpleName() + ": " + id);
		}
		return r;
	}

	public static Long providePrimaryKeyByMaxId(Long primaryKey) {
		return primaryKey == null ? 1 : primaryKey + 1;
	}

	public static JsonNode provideBodyFrmStringResponse(ResponseEntity<String> responseEntity) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jasonbody = objectMapper.readTree(responseEntity.getBody()).get("data");
		return jasonbody;
	}

	public static URI provideRequestUrl(String delegatorUri, String appBasePath, String apiUri)
			throws URISyntaxException {
		String url = delegatorUri + appBasePath + apiUri;
		return new URI(url);
	}

	// Non api
	public void finallyOutputStream(ByteArrayOutputStream baos) {

		if (baos != null) {
			try {
				baos.close();
			} catch (IOException e) {
				logger.error("An error occurred during ByteArrayOutputStream conversion:" + e);

			}
		}
	}
	
	
}
