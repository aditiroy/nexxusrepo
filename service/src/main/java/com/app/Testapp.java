package com.app;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.inr.JsonPath;

public class Testapp {

	public static void main(String[] args) {
		/*
		 * try { String billMonth = "202211"; String beginBillMonth = "202210"; Date
		 * date1 = new SimpleDateFormat("yyyyMM").parse(billMonth);
		 * 
		 * Date date2 = new SimpleDateFormat("yyyyMM").parse(beginBillMonth); int
		 * difInMonths = date1.getMonth() - date2.getMonth(); int diffInYears =
		 * date1.getYear() - date2.getYear(); int monthsBetween = diffInYears * 12 +
		 * (difInMonths+1); double monthlyFactor =(double)12/monthsBetween; int
		 * monthlyFactorResult = (int) Math.round(monthlyFactor);
		 * System.out.println("monthlyFactorResult is"+monthlyFactorResult); } catch
		 * (ParseException e) { // TODO Auto-generated catch block e.printStackTrace();
		 * }
		 */
		String data="OR'TTESTLINE";
		System.out.println("ckt val is :: "+String.valueOf(data).replaceAll("\\'", " "));
		Map<String,String> resultMap= new HashMap<>();
		if(resultMap.get("A")==null) {
	//		System.out.println("value is null....");
		}
	/*	BigDecimal bd = new BigDecimal(5690.8967);
		String s=String.valueOf(bd);
		if(stringHasValue(s) ) {
			Double d=Double.parseDouble(s);
			System.out.println("value of d is"+d);
		}else {
			System.out.println("value of d is null");
		}*/
		/*String ckt="DHEC712955ATI";
		String eplswanCkt=ckt.substring(0, 4);
		System.out.println("eplswanCkt is "+eplswanCkt);*/
		
		JsonPath jsonPath = new JsonPath("/accountDetails/design/portNumber");
	//	String res = args.nodeMap.get(jsonPath.parent().toString()).path(jsonPath.getFieldName()).asText();

		
	}
	
	public static boolean stringHasValue(String in) {
		if (in == null) {
			return false;
		}
		if (in.isEmpty()) {
			return false;
		}
		if ("null".equals(in)) {
			return false;
		}
		return true;
	}


}
