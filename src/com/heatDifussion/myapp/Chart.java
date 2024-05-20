package com.heatDifussion.myapp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Chart extends JPanel {
    private final double[][] temperature;
    private int WIDTH;
    private int HEIGHT;
    private int nTiles;
    private int tileSize;
    private BufferedImage tempScale;
    private BufferedImage bufferedImage;

    public Chart(double[][] temperature) {
        this.temperature = temperature;
        this.nTiles = 100;
        this.tileSize = 5; // Set the size of each tile
        this.WIDTH = nTiles * tileSize; // Adjust the width to fit the tiles exactly
        this.HEIGHT = nTiles * tileSize; // Adjust the height to fit the tiles exactly
        this.setPreferredSize(new Dimension(WIDTH + 160, HEIGHT + 50)); // Set the preferred size of the component
        this.setSize(new Dimension(WIDTH, HEIGHT));

        this.tempScale = new BufferedImage(50, HEIGHT, BufferedImage.TYPE_INT_RGB);
        fillTempBuffer();
        this.bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        temperatureToImage();
        g2d.drawImage(bufferedImage, 40, 15, null);

        fillTempBuffer();
        g2d.drawImage(tempScale, 40 + WIDTH + 20, 15, null);

        drawMesh(40, 15, g2d);
    }

    private void drawMesh(int startX, int startY, Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        int padding = 20; // space between x-axis and labels
        int space = 20; // space between the mesh and the rectangle
        int rectWidth = 50; // width of the rectangle
        int labelSpacing = 50; // vertical spacing between labels on the right

        // Draw vertical lines and labels along the x-axis for every 5th tile
        for (int i = 0; i <= nTiles; i++) {
            int x = startX + i * tileSize;
            g2d.drawLine(x, startY, x, startY + HEIGHT);

            if (i % 5 == 0 && i != 0) { // Skip label '0'
                String label = Integer.toString(i);
                int labelWidth = fm.stringWidth(label);
                g2d.drawString(label, x - labelWidth / 2 - 7, startY + HEIGHT + padding - 3); // Position labels below x-axis
            }
        }

        // Draw horizontal lines and labels along the y-axis for every 5th tile
        for (int i = 0; i <= nTiles; i++) {
            int y = startY + i * tileSize;
            g2d.drawLine(startX, y, startX + WIDTH, y);

            if (i % 5 == 0 && nTiles - i != 0) { // Skip label '0'
                String label = Integer.toString(nTiles - i);
                int labelWidth = fm.stringWidth(label);
                g2d.drawString(label, startX - labelWidth - 5, y + 7); // Adjust x position by width of string
            }
        }

        // Draw rectangle on the right side of the mesh
        g2d.drawRect(startX + WIDTH + space, startY, 50, HEIGHT);

        // Draw labels on the right side of the rectangle
        int rectStartX = startX + WIDTH + space + rectWidth;
        for (int i = 0; i * labelSpacing <= HEIGHT; i++) {
            String label = Integer.toString(i * 100); // Now increments by 100
            int y = HEIGHT - i * labelSpacing; // adjust y for bottom-up labels
            g2d.drawString(label, rectStartX + 5, y + 20);
        }
    }

    private void fillTempBuffer() {
        Graphics2D g2d = (Graphics2D) tempScale.getGraphics();
        for (int i = 0; i < HEIGHT; i += tileSize) {
            float hue = 0.66f * ((float) i / HEIGHT); // Interpolating from 0 (red/hot) to 0.66 (blue/cold)
            int rgb = Color.HSBtoRGB(hue, 1.0f, 1.0f);

            g2d.setColor(new Color(rgb));
            g2d.fillRect(0, i, 50, tileSize);
        }
    }

    private void temperatureToImage() {
        double min = 0.0;
        double max = 1.0;
        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
        for (int i = 0; i < temperature.length; i++) {
            for (int j = 0; j < temperature[0].length; j++) {
                double temp = temperature[i][j];
                float hue = (float) (0.66 * (1 - (temp - min) / (max - min))); // Interpolating from 0 (red/hot) to 0.66 (blue/cold)
                int rgb = Color.HSBtoRGB(hue, 1.0f, 1.0f);

                g2d.setColor(new Color(rgb));
                g2d.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }
    }
}
