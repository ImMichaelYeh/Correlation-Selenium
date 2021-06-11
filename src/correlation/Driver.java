/**
 * Get to the top of the leader board on guessthecorrelation.com!
 * See it running: gyazo.com/c16b5a7824635e471618e04e69e144fc
 * 
 * @author Michael Yeh
 * @date March 12th, 2021
 */

package correlation;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.*;
import java.util.*;

public class Driver {
    
    private static final String USERNAME = "183MY" + System.currentTimeMillis();
    private static final int MILLISECOND_PER_GUESS = 175;
    
    public static void main(String[] args) throws InterruptedException{  
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");      
        WebDriver driver = new ChromeDriver();  
          
        // Launch Website  
        driver.navigate().to("http://www.guessthecorrelation.com/");  
          
        // Start Game
        driver.findElement(By.id("new-game")).click();
        
        // Set new username each time
        Alert alert =  driver.switchTo().alert();
        alert.sendKeys(USERNAME);
        alert.accept();
        
        // Accept TOS
        waitForAlert(driver);
        alert = driver.switchTo().alert();
        alert.accept();
        
        // Mute the game
        driver.findElement(By.id("volume-control")).click();
        
        // Start game for real this time
        driver.findElement(By.id("new-game")).click();
        
        // Infinite loop for max points!
        for (int i = 0; true; i++){
            String correlation = getCorrelationAsString(driver);
            System.out.println("CORRELATION: 0." + correlation);
            
            // input correlation
            driver.findElement(By.id("guess-input")).sendKeys(correlation);
            
            // confirm
            driver.findElement(By.id("submit-btn")).click();
            waitForNext(driver);
            
            driver.findElement(By.id("next-btn")).click();
            Thread.sleep(MILLISECOND_PER_GUESS);
        }
    }
    
    /**
     * Pauses the program until the alert appears.
     */
    static void waitForAlert(WebDriver driver){
	while (true){
	    try{
                Alert alert = driver.switchTo().alert();
                break;
            } 
	    catch(NoAlertPresentException e){ continue; }
	}
    }
    
    /**
     * Waits for the next button to appear after guessing!
     */
    static void waitForNext(WebDriver driver){
	while (driver.findElement(By.id("next-btn")) == null){ /* loop */ }
    }
    
    /**
     * It will loop until the page contains all the plot points!
     */
    static String getPlotPageSource(WebDriver driver){
        // Checks that there were EQUALUPDATES number of pages with the same source before moving forwards
        while (!driver.getPageSource().contains("translate")){ /* loop */ }
	return driver.getPageSource();
    }
    
    /**
     * Returns the first 2 decimals of the R-value as a string
     */
    static String getCorrelationAsString(WebDriver driver){
	getPlotPageSource(driver); // waits for the right source to appear
	List<WebElement> points = driver.findElements(By.className("nv-point")); // get all points
	
	// Processes each point and adds x and y values to their lists
	double[] x = new double[points.size()];
	double[] y = new double[points.size()]; 
	for (int i = 0; i < points.size(); i++){
	    String element = points.get(i).getAttribute("transform");
	    element = element.substring("translate(".length(), element.length()-")".length()); 
	    x[i] = Double.parseDouble(element.split(",")[0]);
	    y[i] = Double.parseDouble(element.split(",")[1]);
	}
	
	String correlation = new Double(new PearsonsCorrelation().correlation(x, y)).toString();
	return correlation.substring(correlation.indexOf(".") + 1, correlation.indexOf(".") + 3); // To get first 2 decimal places
    }
}
