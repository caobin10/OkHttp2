package org.chuck.bean;

public class IdentNum {
	private int sex;
	private int year;
	private int month;
	private int day;
	private String checkBit;
	private String birthdate;
	private String yearStr;
	private String monthStr;
	private String dayStr;
	private int currYear;
	
	public IdentNum(int currYear,String identNum) {
		if(identNum.length()==15){
//			identNum=iden15ToIden18(identNum);
		}
		this.currYear=currYear;
		this.birthdate=identNum.substring(6, 14);
		this.checkBit=identNum.substring(17);
		this.yearStr=birthdate.substring(0, 4);
		this.monthStr=birthdate.substring(4, 6);
		this.dayStr=birthdate.substring(6, 8);	
		this.year=Integer.parseInt(yearStr);
		this.month=Integer.parseInt(monthStr);
		this.day=Integer.parseInt(dayStr);		
		this.sex=Integer.parseInt(identNum.substring(16, 17));
	}

	public int getSex() {
		return sex; 
	}
	public int getYear() {
		return year;
	}
	public int getMonth() {
		return month;
	}
	public int getDay() {
		return day;
	}
	public String getCheckBit() {
		return checkBit;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public int getAge(){
//		Calendar calendar=Calendar.getInstance();
//		int currYear=calendar.get(Calendar.YEAR);
		return currYear-getYear();
	}
	public String getYearStr() {
		return yearStr;
	}
	public String getMonthStr() {
		return monthStr;
	}
	public String getDayStr(){
		return dayStr;
	}
	
	public String getBithdateStr(){
		return getYearStr()+"-"+getMonthStr()+"-"+getDayStr();
	}
	
	public String iden15ToIden18(String iden15){
		return "";
	}

//	public void getWorkNetAge(){
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					URL url = new URL("http://www.baidu.com");
//					URLConnection uc = url.openConnection();//生成连接对象
//					uc.connect(); //发出连接
//					long ld = uc.getDate(); //取得网站日期时间
//					SimpleDateFormat dffs = new SimpleDateFormat("yyyy");
//					String time = dffs.format(new Date(ld));
//					if(time != null){
//						final int age =  Integer.parseInt(time) - getYear();
//
//						((Activity)context).runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								//
//								Toast.makeText(context,"年龄："+age,Toast.LENGTH_SHORT).show();
//							}
//						});
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}

}
