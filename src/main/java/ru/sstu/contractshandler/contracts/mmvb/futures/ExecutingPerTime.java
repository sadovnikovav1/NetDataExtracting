package ru.sstu.contractshandler.contracts.mmvb.futures;

import ru.sstu.contractshandler.db.models.Content;
import ru.sstu.contractshandler.db.services.ContentService;

import java.util.*;
import java.util.stream.Collectors;

public class ExecutingPerTime extends TimerTask {
    private static Calendar calendar = GregorianCalendar.getInstance();
    private ContentService service;
    private MMVBFuturesContract contract;
    private Content lastNote;

    public ExecutingPerTime(ContentService service) {
        this.service = service;
    }

    private static boolean isExecutePerHour(MMVBFuturesContract contract, Content lastNote) {
        calendar.setTime(contract.getDate());
        int contractHour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.setTime(lastNote.getDate());
        int lastNoteHour = calendar.get(Calendar.HOUR_OF_DAY);
        return !(contractHour - lastNoteHour == 0);
    }

    private static boolean isExecutePerDay(MMVBFuturesContract contract, Content lastNote) {
        calendar.setTime(contract.getDate());
        int contractDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(lastNote.getDate());
        int lastNoteDay = calendar.get(Calendar.DAY_OF_MONTH);
        return !(contractDay - lastNoteDay == 0);
    }

    public void run() {
        contract = new MMVBFuturesContract();
        try {
            lastNote = getLastMINUTENote();
        } catch (NoSuchElementException ex) {
            execute(contract, TimeFrame.MINUTE);
            contract.closeSession();
            return;
        }

        execute(contract, TimeFrame.MINUTE);

        if (isExecutePerHour(contract, lastNote)) {
            execute(contract, TimeFrame.HOUR);
        }

        if (isExecutePerDay(contract, lastNote)) {
            execute(contract, TimeFrame.DAY);
        }

        if (isExecutePerWeek(contract, lastNote)) {
            execute(contract, TimeFrame.WEEK);
        }

        if (isExecutePerMonth(contract, lastNote)) {
            execute(contract, TimeFrame.MONTH);
        }
        contract.closeSession();
    }

    private void execute(MMVBFuturesContract contract, TimeFrame timeFrame) {
        Content content = new Content();
        content.setPrice(contract.getPrice());
        content.setQualifyingRatio(contract.getQualifying_Ratio());
        content.setDate(contract.getDate());
        content.setTimeFrame(timeFrame.toString());
        content.setCorrelation(setCorrelationForContent(content, timeFrame));
        try {
            if (!content.equals(getLastMINUTENote())) {
                service.save(content);
            }
        } catch (NoSuchElementException ex) {
            service.save(content);
        }
    }

    private double setCorrelationForContent(Content content, TimeFrame timeFrame) {
        List<Content> all = service.getAll().stream()
                .filter(c -> c.getTimeFrame().equals(timeFrame.toString()))
                .collect(Collectors.toList());
        if (all.size() < 10) {
            return 0;
        }
        List<Content> last9;
        try {
            last9 = all.subList(all.size() - 9, all.size());
        } catch (IndexOutOfBoundsException ex) {
            return 0;
        }

        double temp = content.getQualifyingRatio();
        double temp2 = content.getPrice();
        for (Content item : last9) {
            temp += item.getQualifyingRatio();
            temp2 += item.getPrice();
        }
        double avgX = temp / 10;
        double avgY = temp2 / 10;

        temp = (content.getQualifyingRatio() - avgX) * (content.getPrice() - avgY);
        for (Content item : last9) {
            double x1 = item.getPrice() - avgY;
            double x2 = (item.getQualifyingRatio() - avgX) * Math.pow(10, 8);
            temp += x1 * x2;
        }
        double cov = temp / 10 / Math.pow(10, 8);

        temp = Math.pow((content.getQualifyingRatio() - avgX) * Math.pow(10, 8), 2);
        temp2 = Math.pow((content.getPrice() - avgY), 2);
        for (Content item : last9) {
            temp += Math.pow((item.getQualifyingRatio() - avgX) * Math.pow(10, 8), 2);
            temp2 += Math.pow(item.getPrice() - avgY, 2);
        }

        temp = temp / Math.pow(10, 16);
        double sigmaX, sigmaY;

        sigmaX = Math.sqrt(temp / 9);
        sigmaY = Math.sqrt(temp2 / 9);
        double correlation = cov / (sigmaX * sigmaY);

        return correlation;
    }

    private Content getLastMINUTENote() throws NoSuchElementException {
        return service.getAll().stream()
                .filter(c -> c.getTimeFrame().equals(TimeFrame.MINUTE.toString()))
                .reduce((first, second) -> second).get();
    }

    private boolean isExecutePerWeek(MMVBFuturesContract contract, Content lastNote) {
        calendar.setTime(contract.getDate());
        int contractWeek = calendar.get(Calendar.WEEK_OF_MONTH);
        calendar.setTime(lastNote.getDate());
        int lastNoteWeek = calendar.get(Calendar.WEEK_OF_MONTH);
        return !(contractWeek - lastNoteWeek == 0);
    }

    private boolean isExecutePerMonth(MMVBFuturesContract contract, Content lastNote) {
        calendar.setTime(contract.getDate());
        int contractMonth = calendar.get(Calendar.MONTH);
        calendar.setTime(lastNote.getDate());
        int lastNoteMonth = calendar.get(Calendar.MONTH);
        return !(contractMonth - lastNoteMonth == 0);
    }
}
