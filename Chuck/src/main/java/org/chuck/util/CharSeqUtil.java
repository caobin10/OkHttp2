package org.chuck.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Log;

import org.chuck.bean.IdentNum;

public class CharSeqUtil {
	public static boolean isNullOrEmpty(CharSequence str){
		if(str==null||isEmpty(str)){
			return true;
		}else {
			return false;
		}
	}

	public static boolean isEmpty(CharSequence c){
		int len = c.length();
		int start = 0;
		while (start < len && c.charAt(start) <= ' ') {
			start++;
		}
		return len==start;
	}

	/**
	 * 验证是否含有汉字
	 * @param str
	 * @return
	 */
	public static boolean isHaveChinese(CharSequence str){
		Pattern pattern=Pattern.compile("[\u4e00-\u9fa5]");
		Matcher matcher=pattern.matcher(str);
		if(matcher.find()){
			return true;
		}
		return false;
	}

	/**
	 * 验证是否为整数
	 * @param str
	 * @return
	 */
	public static boolean isNumber(CharSequence str){
		Pattern pattern = Pattern.compile("^[0-9]*$");
	    Matcher matcher = pattern.matcher(str);
	    return matcher.matches();
	}

	/**
	 * 验证是否为正整数
	 * @param str
	 * @return
	 */
	public static boolean isPositiveNumber(CharSequence str){
		Pattern pattern = Pattern.compile("^[1-9]{1}[0-9]*$");
	    Matcher matcher = pattern.matcher(str);
	    return matcher.matches();
	}

	public static boolean isChLetterNum(CharSequence str){
		Pattern pattern = Pattern.compile("^[0-9a-zA-Z\u4e00-\u9fa5]*$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 *
	 * @param s
	 * @param defaultValue
	 * @return
	 */
	public static int parseInt(String s,int defaultValue){
		try{
			if(s==null||s.isEmpty()){
				return defaultValue;
			}
			return Integer.parseInt(s);
		}catch (Exception e){
//			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 *
	 * @param s
	 * @param defaultValue
	 * @return
	 */
	public static float parseFloat(String s, int defaultValue){
		try{
			if(s==null||s.isEmpty()){
				return defaultValue;
			}
			return Float.parseFloat(s);
		}catch (Exception e){
//			e.printStackTrace();
			return defaultValue;
		}
	}

	public static long parseLong(String s, long defaultValue) {
		try{
			if(s==null||s.isEmpty()){
				return defaultValue;
			}
			return Long.parseLong(s);
		}catch (Exception e){
//			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 * 中文验证
	 * @param str
	 * @return
     */
	public static boolean isAllChinese(String str){


		if(isNullOrEmpty(str))return true;

		String overseerInfo = "^([\\u4e00-\\u9fa5]|\\ue82d)+$";
		Pattern pattern=Pattern.compile(overseerInfo);
		Matcher matcher=pattern.matcher(str);
		if(!matcher.matches()){
			return false;
		}
		return true;

//		char[] charArr=str.toCharArray();
//		for(char c:charArr){
//			Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
//	        if (!(ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//	                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
//	                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
//	                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
//	                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
//	                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)) {
//	            return false;
//	        }
//		}
//		return true;
	}
	
	public static boolean isDate(CharSequence inputStr){
		String date="^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))?$";
		Pattern pattern = Pattern .compile(date); 
        Matcher matcher = pattern.matcher(inputStr); 
        if (!matcher.matches()) { 
            return false; 
        }  
		return true;
	}
	public static boolean isTelephone(CharSequence inputStr){
		String tele = "^((0[0-9]{2}\\-)+([2-9][0-9]{7}))|((0[0-9]{3}\\-)+([2-9][0-9]{6}))$";
		Pattern pattern=Pattern.compile(tele);
		Matcher matcher=pattern.matcher(inputStr);
		if(!matcher.matches()){
			return false;
		}
		return true;
	}
	public static boolean isMobilePhone(CharSequence inputStr){
//		String mobile = "^(((13[0-9]{1})|(14[0-9]{1})|(15[0-9]{1})|(17[0-9]{1})|(18[0-9]{1}))+\\d{8})$";
		String mobile = "^(1[3456789]+\\d{9})$";//最新的电话正则表达式与平台保持一致
		Pattern pattern=Pattern.compile(mobile);
		Matcher matcher=pattern.matcher(inputStr);
		if(!matcher.matches()){
			return false;
		}
		return true;
	}
	public static boolean isOverSeerSub(CharSequence inputStr){
		String overseerInfo = "^([\u4e00-\u9fa5]|[0-9]|[，,]|[。.]|[;；]|[:：]|[？?]|[！!]|[a-zA-Z]|[\\s]|[\\n])+$";
		Pattern pattern=Pattern.compile(overseerInfo);
		Matcher matcher=pattern.matcher(inputStr);
		if(!matcher.matches()){
			return false;
		}
		return true;
	}

	/**
	 * 只能输入中文，字母和数字
	 * @param inputStr
	 * @return
     */
	public static boolean isChineseChrnum(CharSequence inputStr){
		if(isNullOrEmpty(inputStr))return true;

		String overseerInfo = "^([\\u0391-\\uFFE5]|[a-zA-Z0-9]|[\\@\\%\\-\\+\\=\\.])+$";
				//"^([\u4e00-\u9fa5]|\ue82d|[\uFF10-\uFF19]|\uff0c|\u3002|[a-zA-Z0-9]+|\\-)+$";
		       // ^([\u4e00-\u9fa5]|\ue82d|[\uFF10-\uFF19]|\uff0c|\u3002|[a-zA-Z0-9]+|\-)+$
		Pattern pattern=Pattern.compile(overseerInfo);
		Matcher matcher=pattern.matcher(inputStr);
		if(!matcher.matches()){
			return false;
		}
		return true;
	}



	public static boolean isProhibitExpressionInput(CharSequence inputStr){
		//匹配非表情符号的正则表达式
		String reg ="^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";
		Pattern pattern=Pattern.compile(reg);
		Matcher matcher=pattern.matcher(inputStr);
		if(!matcher.matches()){
			return false;
		}
		return true;
	}

	public static boolean isTeleMobilePhone(CharSequence inputStr){
		if(isTelephone(inputStr)||isMobilePhone(inputStr)){
			return true;
		}
		return false;
	}

	/**
	 * 邮政编码验证
	 * @param inputStr
	 * @return
     */
	public static boolean isPostCode(CharSequence inputStr){

		if(isNullOrEmpty(inputStr))return true;

		String postCode = "[1-9]\\d{5}(?!\\d)";
		Pattern pattern=Pattern.compile(postCode);
		Matcher matcher=pattern.matcher(inputStr);
		if(!matcher.matches()){
			return false;
		}
		return true;
	}

	/**
	 *验证身份证
	 * @param identNum
	 * @return
     */
	public static boolean isIdentNum(String identNum) {
		//二代身份证的长度
		if (identNum.length() != 18) {
			return false;
		}
		//验证二代身份证的格式
		Pattern pattern = Pattern.compile("[0-9]{10}[0,1]{1}[0-9]{1}[0,1,2,3]{1}[0-9]{4}([0-9]|[X]){1}");
		if (!pattern.matcher(identNum).matches()) {
			return false;
		}
		int year = Integer.parseInt(identNum.substring(6, 10));
		int month = Integer.parseInt(identNum.substring(10, 12));
		int day = Integer.parseInt(identNum.substring(12, 14));
		//验证年份是否超出常规
		if (year < 1800 || year > 2100) {
			return false;
		}
		//验证月份是否有效
		if (month < 1 || month > 12) {
			return false;
		}
		//验证天数是否有效
		int[] monthDayNum;
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			monthDayNum = new int[] { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31,30, 31 };
		} else {
			monthDayNum = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,30, 31 };
		}
		if (day < 1 || day > monthDayNum[month - 1]) {
			return false;
		}
		//验证因子是否有效
		int[] factor = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,8, 4, 2, 1 };
		String[] parity = new String[] { "1", "0", "X", "9", "8", "7", "6","5", "4", "3", "2" };
		int sum = 0;
		for (int i = 0; i < 17; i++) {
			sum += Integer.parseInt(identNum.substring(i, i + 1)) * factor[i];
		}
		int bitIndex = sum % 11;
		String checkBit=identNum.substring(17);;
		if (!checkBit.equals(parity[bitIndex])) {
			return false;
		}
		return true;
	}

	
	public static boolean isLegalPassword(CharSequence str){
		Pattern pattern = Pattern.compile("^(?!\\D+$)(?!\\d+$)[a-zA-Z0-9]{6,16}$");
	    Matcher matcher = pattern.matcher(str);
		if(!matcher.matches()){
			return false;
		}
		return true;
	}

	public static boolean isPassword(CharSequence str){
		Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$");
		Matcher matcher = pattern.matcher(str);
		if(!matcher.matches()){
			return false;
		}
		return true;
	}

	public static boolean isAddress(CharSequence str){
//		Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5\\s]+$");
//		Matcher matcher = pattern.matcher(str);
//		if(!matcher.matches()){
//			return false;
//		}
//		return true;


		return isChineseChrnum(str);
	}

	/**
	 * 计算字符串orig中有多少个same
	 * 例如: ssss 字符串中有多少个ss 其结果是2而非3
	 * @param orig 要计算的字符串
	 * @param same 相同字符串
	 * @return
	 */
	public static int computeSame(String orig,String same){
		int count=0;
		if(!TextUtils.isEmpty(orig)&&!TextUtils.isEmpty(same)){
			int i=-1;
			while ((i=orig.indexOf(same))>=0){
				count++;
				orig=orig.substring(i+same.length());
			}
		}
		return count;
	}

	public static boolean isLegalBankNum(){

		return false;
	}

	public static boolean isEquals(String str, String value){

		if (CharSeqUtil.isNullOrEmpty(str) && CharSeqUtil.isNullOrEmpty(value))
		{
			return true;
		}
		String[] serCodeStr = value.split("\\|");

		for(String code:serCodeStr){

			if (code.equals(str) ) {
				return true;
			}
		}

		return false;
	}


}
