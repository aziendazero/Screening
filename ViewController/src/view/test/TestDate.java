package view.test;

import oracle.jbo.domain.Date;

public class TestDate {
	public static void main(String[] args) {
		TestDate testDate = new TestDate();
	}
	
	public TestDate() {
		java.util.Date javaDate = new java.util.Date();
	    System.out.println("java.util.Date=" + javaDate);
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(javaDate.getTime());
		System.out.println("java.sql.Timestamp=" + sqlTimestamp);
		Date jboDate = new Date(sqlTimestamp);
		System.out.println("oracle.jbo.domain.Date=" + jboDate);
		
	    Date now = new Date(new java.sql.Timestamp(new java.util.Date().getTime()));
	    System.out.println("now=" + now);
	}
}
