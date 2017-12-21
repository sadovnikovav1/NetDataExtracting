package ru.sstu.contractshandler.gui;

import ru.sstu.contractshandler.contracts.mmvb.futures.GraphicsDrawing;
import ru.sstu.contractshandler.contracts.mmvb.futures.TimeFrame;
import ru.sstu.contractshandler.db.models.Content;
import ru.sstu.contractshandler.db.services.ContentService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.NoSuchElementException;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane mainTabbedPane;
    private JButton showGraphicsBtn;
    private JPanel mx12_17Panel;
    private JPanel graphicsPanel;
    private JButton refreshBtn;
    private JLabel udivphLbl;
    private JLabel priceLbl;
    private JLabel correlationLbl;
    private JLabel udivphValueLbl;
    private JLabel priceValueLbl;
    private JLabel correlationValueLbl;
    private JLabel dateLbl;
    private JLabel dateValueLbl;
    private JLabel timeFramveLbl;
    private JComboBox timeFrameCmbox;
    private JPanel graphicsDrawingPanel;

    private Font textFont = this.getFont();

    private ContentService service;

    public MainFrame(ContentService service) {
        super();
        this.service = service;
        this.setSize(800, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.add(mainPanel);

        this.timeFrameCmbox.addItem("выберите тайм фрейм");
        this.timeFrameCmbox.addItem("1 min");
        this.timeFrameCmbox.addItem("1 hour");
        this.timeFrameCmbox.addItem("day");
        this.timeFrameCmbox.addItem("week");
        this.timeFrameCmbox.addItem("month");
        this.timeFrameCmbox.setSelectedIndex(0);

        showGraphicsBtn.addActionListener(e -> {
            try {
                GraphicsDrawing.drawAllGraphics(graphicsDrawingPanel, service, getTimeFrameFromCmbBox());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        refreshBtn.addActionListener(e -> fillValueLabels());


        priceLbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    GraphicsDrawing.drawGraphicForSubject(graphicsDrawingPanel, service, "Цена", getTimeFrameFromCmbBox());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        udivphLbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    GraphicsDrawing.drawGraphicForSubject(graphicsDrawingPanel, service, "Ю : Ф", getTimeFrameFromCmbBox());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        correlationLbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    GraphicsDrawing.drawGraphicForSubject(graphicsDrawingPanel, service, "Корреляция", getTimeFrameFromCmbBox());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        setLinkedLabelFont(priceLbl);
        setLinkedLabelFont(udivphLbl);
        setLinkedLabelFont(correlationLbl);

        fillValueLabels();
    }

    private void fillValueLabels() {
        try {
            Content content = getLastElement();
            udivphValueLbl.setText(String.valueOf(content.getQualifyingRatio()));
            priceValueLbl.setText(String.valueOf(content.getPrice()));
            correlationValueLbl.setText(String.valueOf(content.getCorrelation()));
            dateValueLbl.setText(content.getDate().toString());

        } catch (NoSuchElementException ex) {
            String noValue = "no value";
            udivphValueLbl.setText(noValue);
            priceValueLbl.setText(noValue);
            correlationValueLbl.setText(noValue);
            dateValueLbl.setText(noValue);
        }
    }

    private Content getLastElement() throws NoSuchElementException {
        return service.getAll().stream().reduce((first, second) -> second).get();
    }

    private void setLinkedLabelFont(JLabel linkedLabel) {
        linkedLabel.setOpaque(true);
        linkedLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Font font = linkedLabel.getFont();
                Map attributes = font.getAttributes();
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                linkedLabel.setFont(font.deriveFont(attributes));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                linkedLabel.setFont(textFont);
            }
        });
    }

    private TimeFrame getTimeFrameFromCmbBox() {
        switch (timeFrameCmbox.getSelectedIndex()) {
            case 1:
                return TimeFrame.MINUTE;
            case 2:
                return TimeFrame.HOUR;
            case 3:
                return TimeFrame.DAY;
            case 4:
                return TimeFrame.WEEK;
            case 5:
                return TimeFrame.MONTH;
        }
        throw new IllegalArgumentException("Wrong time frame");
    }
}