package ru.sstu.contractshandler.contracts.mmvb.futures;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.*;
import ru.sstu.contractshandler.db.models.Content;
import ru.sstu.contractshandler.db.services.ContentService;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class GraphicsDrawing {

    public static void drawAllGraphics(JPanel graphicsDrawingPanel, ContentService service, TimeFrame frame) {
        TimeSeriesCollection priceDataset = createDataset(service, "Цена", frame);
        TimeSeriesCollection qualRatioDataset = createDataset(service, "Ю : Ф", frame);
        TimeSeriesCollection correlationDataset = createDataset(service, "Корреляция", frame);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Цена, Ю:Ф, Корреляция от времени",
                frame.toString(),
                "Цена, Ю:Ф, Корреляция", priceDataset, true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDataset(0, priceDataset);
        plot.setRenderer(0, new StandardXYItemRenderer());
        plot.setDataset(1, qualRatioDataset);
        plot.setRenderer(1, new StandardXYItemRenderer());
        plot.setDataset(2, correlationDataset);
        plot.setRenderer(2, new StandardXYItemRenderer());

        ChartPanel chartPanel = new ChartPanel(chart);
        JScrollPane graphicsScrollPane = new JScrollPane(chartPanel);
        graphicsScrollPane.setPreferredSize(new Dimension(500, 500));
        graphicsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        graphicsDrawingPanel.removeAll();
        graphicsDrawingPanel.setLayout(new java.awt.BorderLayout());
        graphicsDrawingPanel.add(graphicsScrollPane);
        graphicsDrawingPanel.revalidate();
        graphicsDrawingPanel.repaint();
    }

    public static void drawGraphicForSubject(JPanel graphicsDrawingPanel, ContentService service,
                                             String subject, TimeFrame frame) {
        JFreeChart chart = createChart(createDataset(service, subject, frame), subject, frame);
        ChartPanel chartPanel = new ChartPanel(chart);
        JScrollPane graphicsScrollPane = new JScrollPane(chartPanel);
        graphicsScrollPane.setPreferredSize(new Dimension(500, 500));
        graphicsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        graphicsDrawingPanel.removeAll();
        graphicsDrawingPanel.setLayout(new java.awt.BorderLayout());
        graphicsDrawingPanel.add(graphicsScrollPane);
        graphicsDrawingPanel.revalidate();
        graphicsDrawingPanel.repaint();
    }

    private static JFreeChart createChart(TimeSeriesCollection dataset, String subject, TimeFrame frame) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                subject + " от времени",
                frame.toString(),
                subject, dataset, true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        axis.setVerticalTickLabels(true);
        return chart;
    }

    private static TimeSeriesCollection createDataset(ContentService service, String subject, TimeFrame frame) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        switch (subject) {
            case "Цена":
                return createDatasetForPrice(service, frame);
            case "Ю : Ф":
                return createDatasetForQualifRatio(service, frame);
            case "Корреляция":
                return getDatasetForCorrelation(service, frame);
        }

        return dataset;
    }

    private static TimeSeriesCollection createDatasetForPrice(ContentService service, TimeFrame frame) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries timeSeries = new TimeSeries("Цена");
        switch (frame) {
            case MINUTE:
                for (Content c : getDataByTimeFrame(service, TimeFrame.MINUTE)) {
                    timeSeries.addOrUpdate(new Minute(c.getDate()), c.getPrice());
                }
                break;
            case HOUR:
                for (Content c : getDataByTimeFrame(service, TimeFrame.HOUR)) {
                    timeSeries.addOrUpdate(new Hour(c.getDate()), c.getPrice());
                }
                break;

            case DAY:
                for (Content c : getDataByTimeFrame(service, TimeFrame.DAY)) {
                    timeSeries.addOrUpdate(new Day(c.getDate()), c.getPrice());
                }
                break;

            case WEEK:
                for (Content c : getDataByTimeFrame(service, TimeFrame.WEEK)) {
                    timeSeries.addOrUpdate(new Week(c.getDate()), c.getPrice());
                }
                break;

            case MONTH:
                for (Content c : getDataByTimeFrame(service, TimeFrame.MONTH)) {
                    timeSeries.addOrUpdate(new Month(c.getDate()), c.getPrice());
                }
                break;
        }
        dataset.addSeries(timeSeries);
        return dataset;
    }

    private static TimeSeriesCollection createDatasetForQualifRatio(ContentService service, TimeFrame frame) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries timeSeries = new TimeSeries("Ю : Ф");
        switch (frame) {
            case MINUTE:
                for (Content c : getDataByTimeFrame(service, TimeFrame.MINUTE)) {
                    timeSeries.addOrUpdate(new Minute(c.getDate()), c.getQualifyingRatio() * 182000);
                }
                break;
            case HOUR:
                for (Content c : getDataByTimeFrame(service, TimeFrame.HOUR)) {
                    timeSeries.addOrUpdate(new Hour(c.getDate()), c.getQualifyingRatio() * 182000);
                }
                break;

            case DAY:
                for (Content c : getDataByTimeFrame(service, TimeFrame.DAY)) {
                    timeSeries.addOrUpdate(new Day(c.getDate()), c.getQualifyingRatio() * 182000);
                }
                break;

            case WEEK:
                for (Content c : getDataByTimeFrame(service, TimeFrame.WEEK)) {
                    timeSeries.addOrUpdate(new Week(c.getDate()), c.getQualifyingRatio() * 182000);
                }
                break;

            case MONTH:
                for (Content c : getDataByTimeFrame(service, TimeFrame.MONTH)) {
                    timeSeries.addOrUpdate(new Month(c.getDate()), c.getQualifyingRatio() * 182000);
                }
                break;
        }
        dataset.addSeries(timeSeries);
        return dataset;
    }

    private static TimeSeriesCollection getDatasetForCorrelation(ContentService service, TimeFrame frame) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries timeSeries = new TimeSeries("Корреляция");
        switch (frame) {
            case MINUTE:
                for (Content c : getDataByTimeFrame(service, TimeFrame.MINUTE)) {
                    timeSeries.addOrUpdate(new Minute(c.getDate()), c.getCorrelation() * 200000);
                }
                break;
            case HOUR:
                for (Content c : getDataByTimeFrame(service, TimeFrame.HOUR)) {
                    timeSeries.addOrUpdate(new Hour(c.getDate()), c.getCorrelation() * 200000);
                }
                break;

            case DAY:
                for (Content c : getDataByTimeFrame(service, TimeFrame.DAY)) {
                    timeSeries.addOrUpdate(new Day(c.getDate()), c.getCorrelation() * 200000);
                }
                break;

            case WEEK:
                for (Content c : getDataByTimeFrame(service, TimeFrame.WEEK)) {
                    timeSeries.addOrUpdate(new Week(c.getDate()), c.getCorrelation() * 200000);
                }
                break;

            case MONTH:
                for (Content c : getDataByTimeFrame(service, TimeFrame.MONTH)) {
                    timeSeries.addOrUpdate(new Month(c.getDate()), c.getCorrelation() * 200000);
                }
                break;
        }
        dataset.addSeries(timeSeries);
        return dataset;
    }

    private static List<Content> getDataByTimeFrame(ContentService service, TimeFrame frame) {
        return service.getAll()
                .stream()
                .filter(content -> content.getTimeFrame()
                        .equals(frame.toString()))
                .collect(Collectors.toList());
    }
}
