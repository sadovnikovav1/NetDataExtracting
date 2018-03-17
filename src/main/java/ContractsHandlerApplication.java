import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.sstu.contractshandler.contracts.mmvb.futures.ExecutingPerTime;
import ru.sstu.contractshandler.db.services.ContentService;
import ru.sstu.contractshandler.gui.MainFrame;

import javax.persistence.ManyToOne;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class ContractsHandlerApplication {
    private static Timer timer = new Timer();

    public static void main(String[] args) {
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
        MainFrame frame = new MainFrame(service);
        frame.setVisible(true);
        ExecutingPerTime executingPerMinute = new ExecutingPerTime(service);
        timer.schedule(executingPerMinute, 0, 1000 * 60);
    }
}