import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.sstu.contractshandler.contracts.mmvb.futures.ExecutingPerTime;
import ru.sstu.contractshandler.contracts.mmvb.futures.TimeFrame;
import ru.sstu.contractshandler.db.models.Content;
import ru.sstu.contractshandler.db.services.ContentService;
import ru.sstu.contractshandler.gui.MainFrame;

import javax.persistence.ManyToOne;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.stream.Collectors;

import static java.lang.Double.NaN;

public class ContractsHandlerApplication {
    private static Timer timer = new Timer();

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        ContentService service = (ContentService) context.getBean("storageService");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        List<Content> writes = service.getAll().stream().filter(s -> s.getTimeFrame().equals("DAY")).collect(Collectors.toList());
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        for (Content c : writes){
            sb1.append(c.getQualifyingRatio() + " ");

            sb2.append(c.getPrice()+ " ");

            sb3.append(c.getCorrelation()+ " ");
        }
        BufferedWriter b1 = new BufferedWriter(new FileWriter("correlation.txt"));
        b1.write(sb3.toString().trim());
        b1.close();
        BufferedWriter b2 = new BufferedWriter(new FileWriter("price.txt"));
        b2.write(sb2.toString().trim());
        b2.close();
        BufferedWriter b3 = new BufferedWriter(new FileWriter("UdivF.txt"));
        b3.write(sb1.toString().trim());
        b3.close();
        MainFrame frame = new MainFrame(service);
        frame.setVisible(true);
        ExecutingPerTime executingPerMinute = new ExecutingPerTime(service);
        timer.schedule(executingPerMinute, 0, 1000 * 60 * 60);
    }
}