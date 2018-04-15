import ru.sstu.contractshandler.contracts.mmvb.futures.TimeFrame;
import ru.sstu.contractshandler.db.models.Content;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// 5 Значений добавить
public class CorrelationTest {
    public static void main(String[] args) throws IOException {

        List<Double> prices = getValues("E:\\Jaba_projects\\futures-contracts-handler\\price.txt");
        List<Double> qulifRatios = getValues("E:\\Jaba_projects\\futures-contracts-handler\\UdivF.txt");

        for (int j = 0; j < 12; j ++) {
            List<CustomContent> contents = new ArrayList<>();
            List<Double> corrs = new ArrayList<>();
            for (int i = 0; i < prices.size() - j; i++) {
                double corr = evaluateCorrelation(contents, new CustomContent(qulifRatios.get(i), prices.get(i + j), 0));
                corrs.add(corr);
                contents.add(new CustomContent(qulifRatios.get(i), prices.get(i), corr));
            }
            writeCorrelationsToFile(corrs, "E:\\Jaba_projects\\futures-contracts-handler\\Смещение по цене на " + j + ".txt");
        }
    }

    private static List<Double> getValues(String filePath) {
        List<Double> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String finalString = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            finalString = sb.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] strings = finalString.split(" ");
        for (String s : strings) {
            list.add(Double.parseDouble(s.trim()));
        }

        return list;
    }

    private static double evaluateCorrelation(List<CustomContent> contents, CustomContent current) {
        List<CustomContent> all = contents;
        if (all.size() < 10) {
            return 0;
        }
        List<CustomContent> last9;
        try {
            last9 = all.subList(all.size() - 9, all.size());
        } catch (IndexOutOfBoundsException ex) {
            return 0;
        }

        double temp = current.getQualifyingRatio();
        double temp2 = current.getPrice();
        for (CustomContent item : last9) {
            temp += item.getQualifyingRatio();
            temp2 += item.getPrice();
        }
        double avgX = temp / 10;
        double avgY = temp2 / 10;

        temp = (current.getQualifyingRatio() - avgX) * (current.getPrice() - avgY);
        for (CustomContent item : last9) {
            double x1 = item.getPrice() - avgY;
            double x2 = (item.getQualifyingRatio() - avgX) * Math.pow(10, 8);
            temp += x1 * x2;
        }
        double cov = temp / 10 / Math.pow(10, 8);

        temp = Math.pow((current.getQualifyingRatio() - avgX) * Math.pow(10, 8), 2);
        temp2 = Math.pow((current.getPrice() - avgY), 2);
        for (CustomContent item : last9) {
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

    private static void writeCorrelationsToFile(List<Double> correlations, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (double corr : correlations) {
            sb.append(corr + " ");
        }
        BufferedWriter b1 = new BufferedWriter(new FileWriter(fileName));
        b1.write(sb.toString().trim());
        b1.close();
    }
}

class CustomContent {
    private double qualRatio;
    private double price;
    private double correlation;

    public CustomContent(double qualRatio, double price, double correlation) {
        this.qualRatio = qualRatio;
        this.price = price;
        this.correlation = correlation;
    }

    public double getQualifyingRatio() {
        return qualRatio;
    }

    public void setQualifyingRatio(double qualRatio) {
        this.qualRatio = qualRatio;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }
}
