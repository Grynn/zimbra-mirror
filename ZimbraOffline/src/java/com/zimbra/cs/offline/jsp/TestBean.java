package com.zimbra.cs.offline.jsp;

public class TestBean {

	public class MyObj {
		public String getName() {
			return "foo";
		}
		
		public String getNumber() {
			return "123";
		}
	}
	
	private static final long serialVersionUID = 4353338296407164096L;
	
	String foo;
	
	public TestBean() {}
	
	public boolean validate() {
		if (error != null) {
			error += " (validated)";
			return false;
		}
		return true;
	}
	
	public void setFoo(String foo) {
		this.foo = foo;
	}
	
	public String getFoo() {
		return foo;
	}
	
	enum Gender {
		male, female
	}
	
	Gender g;
	String genderInput;
	
	String error;
	
	public void setGender(String gender) throws Throwable {
		genderInput = gender;
		try {
			g = Gender.valueOf(gender);
		} catch (Throwable t) {
			error = "Invalid gender type: " + gender;
		}
	}
	
	public String getGender() {
		return g != null ? g.toString() : genderInput;
	}
	
	public String getError() {
		return error;
	}
	
	public MyObj getMyObj() {
		return new MyObj();
	}
}
