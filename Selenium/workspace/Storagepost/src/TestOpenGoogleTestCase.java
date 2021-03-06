import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.Message.RecipientType;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.http.HttpEntity;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.portable.InputStream;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

//import com.gargoylesoftware.htmlunit.javascript.host.file.File;


public class TestOpenGoogleTestCase {
  private WebDriver driver;
  private JavascriptExecutor js;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  int lineNumber; 
  int successfulTests = 0;
  int failedTests = 0;
  boolean sendResults = false;
  private BufferedWriter summaryFile;
 

  
  @Before
  public void setUp() throws Exception {
    baseUrl = "https://github.com/";
  }
  
  
  public static void main(String args[]) throws Exception{
	  new TestOpenGoogleTestCase().testCorrectResults(args[0], args[1]);
  }

 // @Test
  public void testCorrectResults(String tFolder, String sendmailDomain) throws Exception {




     

	  SimpleDateFormat dateForFilename = new SimpleDateFormat ("yyyy-MM-dd--hh-mm-ss");
	  String csvFile;
	  String testFolderName = tFolder;
	  String summaryFileName = tFolder + "/results.php";
	  String summaryMailUrl = sendmailDomain + "/results.php";
	  summaryFile = new BufferedWriter(new FileWriter(summaryFileName));
	  File folder = new File(testFolderName);
	  File[] listOfFiles = folder.listFiles();

	  String mailRecipient = "nisit@hotsaucestudios.com";
	  String mailSubject = "Selenium Test Results";
	  String message = "\"";
	  String headers = "\"";
	  
	  summaryFile.write("<?php");
	  summaryFile.newLine();
	  summaryFile.write("mail(\"" + mailRecipient + "\", \"" + mailSubject + "\", ");
	  summaryFile.newLine();


	  

	  for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {
          String thisFile = listOfFiles[i].getName();
          
          
          
//    	  do this with each test file
          if(thisFile.contains(".csv")) {
        	  String resultFolderName = thisFile.substring(0, thisFile.length()-4) + "_results";
        	  File testFolder = new File(testFolderName + "/" + resultFolderName);
        	  if( testFolder.isDirectory() == false) {
                  System.out.println("is not an existing dir");
        		  testFolder.mkdirs();
        	  }
        	  
        	  csvFile = testFolderName + "/" + listOfFiles[i].getName(); 
        	  Date date = new Date();
        	  String csvResultFile =  testFolderName + "/" + testFolder.getName() + "/" + thisFile.substring(0, thisFile.length()-4) + "--results-" + dateForFilename.format(date) + ".csv";
        	  System.out.println(csvResultFile);
        	  
        	  
              BufferedReader br = null;
              BufferedWriter bw = null;
              String line = "";
              String cvsSplitBy = ",";
              boolean abort = false; 

//              ProfilesIni prof = new ProfilesIni()				
//              FirefoxProfile ffProfile= prof.getProfile ("myProfile")
              driver = new ChromeDriver();
              //driver = new FirefoxDriver();
              //driver = new SafariDriver();
              js = (JavascriptExecutor)driver;
              try {

                  br = new BufferedReader(new FileReader(csvFile));
                  bw = new BufferedWriter(new FileWriter(csvResultFile, true));
                  message = addToMessage( message, "test file: " + csvFile);
                  message = addToMessage( message, "log file: " + csvResultFile);
                  message = addToMessage( message, " ");
                  successfulTests = 0;
                  failedTests = 0;
                  lineNumber = 0;
                  while ((line = br.readLine()) != null) {
                	  lineNumber++;
                	  String resultLine = "";
                	  int fails = failedTests;
                	  String[] thisLine = line.split(cvsSplitBy);
                	  int cols = thisLine.length; 
                	  String val2 = "";
                	  
                	  if( thisLine[0].substring(0, 2).equals("//") || thisLine[0].substring(0, 2).equals("")) {
                		  bw.write(thisLine[0]);
                          bw.newLine();          

                	  } else {

                    	  if(cols > 2) {
                    		  val2 = thisLine[2];
                    	  }
                          resultLine = runCommand(thisLine[0], thisLine[1], val2);
                          bw.write(resultLine);
                          bw.newLine();                    	  
                	  }
                	  if(fails<failedTests) {
            			  message = addToMessage( message, "-" + resultLine.replaceAll(",", ", "));
            			  message = addToMessage( message, " " );
                	  }
                  }
                  bw.close();
                  driver.quit();

              } catch (FileNotFoundException e) {
                  e.printStackTrace();
              } catch (IOException e) {
                  e.printStackTrace();
              } finally {
                  if (br != null) {
                      try {
                          br.close();
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
              }
        	  
              message = addToMessage( message, "successful tests: " + successfulTests + " / failed tests: " + failedTests);
              message = addToMessage( message, "????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
              message = addToMessage( message, " ");
              
        	  
        	  
          }
        }
      } // end for loop
	  
	  message +=  "\");";
	  summaryFile.write(message);
	  summaryFile.newLine();
	  summaryFile.write("?>");
	  summaryFile.close();
	  
//	  send the file
	  if(sendResults == true) {
		  driver = new ChromeDriver();
		  //driver = new FirefoxDriver();
		  //driver = new SafariDriver();
		  driver.get(summaryMailUrl);
		  driver.quit();  
	  }
  }


  
  
  public boolean testCondition(String condition) {
	  Boolean result = false; 
      try {
          ScriptEngineManager sem = new ScriptEngineManager();
          ScriptEngine se = sem.getEngineByName("JavaScript");
          result = (Boolean) se.eval(condition);
      } catch (ScriptException e) {
          System.out.println("Invalid Expression");
          e.printStackTrace();
      }
      return result;
  }
  private String addToMessage(String om, String addition ) {
	  om += addition;
	  om += "\\n";
	  return om; 
  }
  public String runCommand(String command, String value1, String value2) throws InterruptedException {
	  
	  value1 = value1.replaceAll("&comma;", ",");
	  value2 = value2.replaceAll("&comma;", ",");
	  
	  System.out.println("command: " + command + ", " + "value1: " + value1 + ", " + "value2: " + value2);
	  
	  
//	  Tests
	  if(command.contains("test")) {
		  boolean result = false;
		  String data = ""; 
		  
		  
		  
		  
		  if(command.contains("testUrl")) {
			 if(driver.getCurrentUrl().equals(value1) || driver.getCurrentUrl().equals(value1 + "/")) {
				 result = true; 
			 } else {
				 data = printSimilarity(driver.getCurrentUrl(), value1);
			 }
		  } else if (command.contains("testStringIn")) {
			 if(value1.equals("url")) {
				 String thisUrl = driver.getCurrentUrl();
				 if(thisUrl.contains(value2)) {
					 result = true;
				 } else {
					 data = "the url was: " + thisUrl; 
				 }
			 } else {
				 WebElement thisElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(value1)));
				 String thisElementText = thisElement.getText();
				 if(thisElementText.contains(value2)) {
					 result = true;
				 } else {
					 data = "element text was: " + thisElementText;
				 }
			 }
		  } else if (command.contains("testElExist")) {
			  if(command.contains(" -n ")) {
				  result = driver.findElements(By.cssSelector(value1)).size() == 0;  
			  } else {
				  result = driver.findElements(By.cssSelector(value1)).size() != 0;				  
			  }
		  } else if(command.contains("testForTextByEl")) {
//   		  driver.findElements(By.cssSelector(value1)).size();
//			  List<WebElement> elements = (List<WebElement>) (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(value1)));
//			  System.out.println(elements[3],getText());
//			  WebElement thisElement = driver.findElement(By.xpath("//*[text() = 'Bruckner Blvd.')]]"));
			  WebElement thisElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'Bruckner Blvd.')]")));
			  String thisElementClass = thisElement.getAttribute("class");
			  System.out.println(thisElementClass);
		  } else if(command.contains("testElText")) {

			  WebElement thisElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(value1)));
			  //WebElement thisElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(value1)));
			  String thisElementText = thisElement.getText();
			  
			  if(command.contains(" -i ")) {
				  result = thisElementText.equalsIgnoreCase(value2);
			  } else {
				  result = thisElementText.equals(value2);				  
			  }

			  if(result == false) {
				  data = "text was: " + thisElementText + "; " + printSimilarity(thisElementText, value2);
			  } 
			  
		  } else if (command.contains("testMatchLength")) {
			  
			  int numMatch = driver.findElements(By.cssSelector(value1)).size();
			  String testCond = value2.replaceAll("x", String.valueOf(numMatch));
			  result = testCondition(testCond);
			  if (result == false) {
				 int rq = driver.findElements(By.cssSelector(value1)).size();
				 data=Integer.toString(rq);
			  }
		  } else if (command.contains("testHoldUnitData")) {
			  
			  
			  String unitSizeSelector = value1.concat(" .unit-info h5 a");
			  String unitSizeDetailSelector = value1.concat(" .unit-size");
			  String unitPriceSelector = value1.concat(" .unit-price span");
			  String unitPromoSelector = value1.concat(" .unit-promo");
			  String holdUnitSelector = value1.concat(" .unit-hold .hold-button");
			  
			  WebElement unitSize = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(unitSizeSelector)));
			  String unitSizeText = unitSize.getText();
			  
			  WebElement unitSizeDetail = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(unitSizeDetailSelector)));
			  String unitSizeDetailText = unitSizeDetail.getText();
			  
			  WebElement unitPrice = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(unitPriceSelector)));
			  String unitPriceText = unitPrice.getText();
			  
	
			  WebElement unitPromo = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(unitPromoSelector)));
			  String unitPromoText = unitPromo.getText();
			  //WebElement thisElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(value1)));
			  
			  
			  //Click on Hold Unit Button
			  WebElement element = driver.findElement(By.cssSelector(holdUnitSelector));
			  element.click();
			  
			  
			  //Wait for element
			  WebElement element1 = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(value2)));
			  
			  String holdUnitSizeSelector = value2.concat(" .unit-size-info .unit-size-description");
			  String holdUnitSizeDetailSelector = value2.concat(" .unit-size-info h4");
			  String holdUnitPriceSelector = value2.concat(" .quoted-rate");
			  String holdUnitPromoSelector = value2.concat(" .unit-promo-message");
			  
			  
			  WebElement holdUnitSize = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(holdUnitSizeSelector)));
			  String holdUnitSizeText = holdUnitSize.getText();
			  
			  WebElement holdUnitSizeDetail = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(holdUnitSizeDetailSelector)));
			  String holdUnitSizeDetailText = holdUnitSizeDetail.getText();
			  
			  //will have to tweak SP site to add some span or something and also css to body for ajax thing
			  //holdUnitSizeDetailText = holdUnitSizeDetailText + "(See Others)";
			  
			  
			  WebElement holdUnitPrice = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(holdUnitPriceSelector)));
			  String holdUnitPriceText = holdUnitPrice.getText();
			  holdUnitPriceText += "/mo";
	
			  WebElement holdUnitPromo = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(holdUnitPromoSelector)));
			  String holdUnitPromoText = holdUnitPromo.getText();
			  
			  
			  
			  if(command.contains(" -i ")) {
				  result = (unitSizeText.equalsIgnoreCase(holdUnitSizeText) && unitSizeDetailText.equalsIgnoreCase(holdUnitSizeDetailText) && unitPriceText.equalsIgnoreCase(holdUnitPriceText) && unitPromoText.equalsIgnoreCase(holdUnitPromoText));
			  } else {	
				  result = (unitSizeText.equals(holdUnitSizeText) && unitSizeDetailText.equals(holdUnitSizeDetailText) && unitPriceText.equals(holdUnitPriceText) && unitPromoText.equals(holdUnitPromoText));
			  }

			  if(result == false) {
				  data = "Data on Listing Page: Size was: " + unitSizeText + ", Detail Size was: " + unitSizeDetailText + ", Price was: " + unitPriceText + ", Promo was: " + unitPromoText + " Data on Hold Unit Page: Size was: " + holdUnitSizeText + ", Detail Size was: " + holdUnitSizeDetailText + ", Price was: " + holdUnitPriceText + ", Promo was: " + holdUnitPromoText ;
			  } 
		  }

		  
		  

		  
		  
//		  Prepare the return/log string
		  
		  if (result == true || data=="") {
			  successfulTests += 1;
			  return command + "," + value1 + "," + value2 + "," + "result = " + result;
		  } else {
			  sendResults = true;
			  failedTests += 1;
			  String failMessage = command + "," + value1 + "," + value2 + "," + "result = " + result + "," + "data: " + data;
			  return failMessage;
		  }
	  }

	  
	  
	  
	  
	  
	  
//	  Other commands
	  else {

		  if(command.equals("openUrl")) {
			  driver.get(value1);
		  } else if (command.equals("sleep") ) {
			  int timeVal = Integer.parseInt(value1);
			  Thread.sleep(timeVal);
		  } else if (command.equals("waitForEl") ) {
		    	WebElement element = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(value1)));		  
		  } else if (command.equals("removeEl") ) {
//			  	js.executeScript("document.getElementsByClassName("+value1+").remove();");
		  } else if (command.equals("sendKeys") ) {
			  System.out.println("sendKeys command running");
			  if (driver.findElements(By.cssSelector(value1)).size()>0) {
				  WebElement element = driver.findElement(By.cssSelector(value1));
				  if(value2.equals("{ENTER}")) {
					  element.sendKeys(Keys.ENTER);
				  } else {
					  element.sendKeys(value2);
				  }
			  }
		  } else if (command.equals("click")) {
			  WebElement element = driver.findElement(By.cssSelector(value1));
			  element.click();
		  } else if (command.equals("changeSelectValue")) {
			  Select select = new Select(driver.findElement(By.cssSelector(value1)));
//			  select.deselectAll();
			  select.selectByVisibleText(value2);
		  } else if (command.equals("executeJS")) {
			  js.executeScript(value1);
		  } else {
			  System.out.println("none of those: it was >> " + command);
		  }
		  return command + "," + value1 + "," + value2;		  
	  }
	  

  }
  
  @After
  public void tearDown() throws Exception {
//    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }
  


  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
  public static double similarity(String s1, String s2) {
	    String longer = s1, shorter = s2;
	    if (s1.length() < s2.length()) { // longer should always have greater length
	      longer = s2; shorter = s1;
	    }
	    int longerLength = longer.length();
	    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
	    /* // If you have StringUtils, you can use it to calculate the edit distance:
	    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
	                               (double) longerLength; */
	    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	  }

	  // Example implementation of the Levenshtein Edit Distance
	  // See http://rosettacode.org/wiki/Levenshtein_distance#Java
	  public static int editDistance(String s1, String s2) {
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();

	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	      int lastValue = i;
	      for (int j = 0; j <= s2.length(); j++) {
	        if (i == 0)
	          costs[j] = j;
	        else {
	          if (j > 0) {
	            int newValue = costs[j - 1];
	            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	              newValue = Math.min(Math.min(newValue, lastValue),
	                  costs[j]) + 1;
	            costs[j - 1] = lastValue;
	            lastValue = newValue;
	          }
	        }
	      }
	      if (i > 0)
	        costs[s2.length()] = lastValue;
	    }
	    return costs[s2.length()];
	  }

	  public static String printSimilarity(String s, String t) {
	    return(String.format("%.3f is the similarity between '%s' and '%s'", similarity(s, t), s, t));
	  }
}



