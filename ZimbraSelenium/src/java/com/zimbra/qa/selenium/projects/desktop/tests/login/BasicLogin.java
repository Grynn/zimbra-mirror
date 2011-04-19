package com.zimbra.qa.selenium.projects.desktop.tests.login;

import org.testng.annotations.Test;

public class BasicLogin{
	
	public BasicLogin() {	
	}
	
	/**@Test(	description = "Login to the Ajax Client",
			groups = { "sanity" })
	public void BasicLogin01() throws HarnessException {
		
		// Login
		app.zPageLogin.zLogin(ZimbraAccount.AccountZMC());
		
		// Verify main page becomes active
		ZAssert.assertTrue(app.zPageMain.zIsActive(), "Verify that the account is logged in");
		
	}*/

	@Test(groups={"sanity"})
   public void Pos001() {
      System.out.println("pos001");
   }

   @Test(groups={"sanity", "non-desktop"})
   public void Pos002() {
      System.out.println("pos002");
   }

   @Test(groups={"sanity", "desktop-specific"})
   public void Pos003() {
      System.out.println("pos003");
   }

   @Test(groups={"smoke"})
   public void Pos004() {
      System.out.println("pos004");
   }

   @Test(groups={"functional"})
   public void Pos005() {
      System.out.println("pos005");
   }
}
