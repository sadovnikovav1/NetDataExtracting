package ru.sstu.contractshandler.contracts.mmvb.futures;

import org.exolab.castor.types.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.sstu.contractshandler.db.services.ContentService;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class MMVBFuturesContract {
    private static final String URL = "https://www.moex.com/ru/contract.aspx?code=MXM8";
    private WebDriver driver;
    private WebDriverWait webDriverWait;
    private Date date;
    private double price;
    private double qualifying_Ratio;
    private ContentService service;

    public MMVBFuturesContract() {
        try {
            driver = new ChromeDriver();
        } catch (Exception ex) {
            driver = new FirefoxDriver();
        }

        driver.get(URL);
        webDriverWait = new WebDriverWait(driver, 20);
        setDate();
        setPrice();
        setQualifying_Ratio();
    }

    public double getPrice() {
        return this.price;
    }

    private void setPrice() {
        this.price = customDoubleParser(driver.findElement(By.cssSelector("td.ng-scope.ng-binding")).getText());
    }

    public Date getDate() {
        return this.date;
    }

    private void setDate() {
        if (this.date == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            try {
                date = new Date(sdf.parse(driver.findElement(By.cssSelector("#digest_refresh_time")).getText()).getTime());
            } catch (ParseException e) {
                date = new Date(Calendar.getInstance().getTimeInMillis());
            }
        }
    }

    //(long U div short U) div (long PHys div short PHys)
    public double getQualifying_Ratio() {
        return this.qualifying_Ratio;
    }

    private void setQualifying_Ratio() {
        double[] values = getMainTableValuesArray();
        this.qualifying_Ratio = (values[2] / values[3]) / (values[0] / values[1]);
    }

    private double customDoubleParser(String digit) {
        char[] characters = digit.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : characters) {
            if (c != ' ') {
                sb.append(c);
            }
        }
        return Double.parseDouble(sb.toString());
    }

    private List<WebElement> getOpenedPositionsTable() {
        WebElement table = driver.findElement(By.cssSelector("table.contract-open-positions.table1"));
        return table.findElements(By.cssSelector("td.text_right.ng-binding"));
    }

    private double[] getMainTableValuesArray() {
        List<WebElement> positions = getOpenedPositionsTable();
        double[] posArray = new double[positions.size()];
        int counter = 0;
        for (WebElement position : positions) {
            posArray[counter] = customDoubleParser(position.getText());
            counter++;
        }
        return posArray;
    }

    public void closeSession() {
        this.driver.close();
    }
}
