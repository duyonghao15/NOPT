package org.nudtopt.api.tool.gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TangoColorFactory {
    public static final Color CHAMELEON_1 = new Color(138, 226, 52);
    public static final Color CHAMELEON_2 = new Color(115, 210, 22);
    public static final Color CHAMELEON_3 = new Color(78, 154, 6);
    public static final Color BUTTER_1 = new Color(252, 233, 79);
    public static final Color BUTTER_2 = new Color(237, 212, 0);
    public static final Color BUTTER_3 = new Color(196, 160, 0);
    public static final Color SKY_BLUE_1 = new Color(114, 159, 207);
    public static final Color SKY_BLUE_2 = new Color(52, 101, 164);
    public static final Color SKY_BLUE_3 = new Color(32, 74, 135);
    public static final Color CHOCOLATE_1 = new Color(233, 185, 110);
    public static final Color CHOCOLATE_2 = new Color(193, 125, 17);
    public static final Color CHOCOLATE_3 = new Color(143, 89, 2);
    public static final Color PLUM_1 = new Color(173, 127, 168);
    public static final Color PLUM_2 = new Color(117, 80, 123);
    public static final Color PLUM_3 = new Color(92, 53, 102);
    public static final Color SCARLET_1 = new Color(239, 41, 41);
    public static final Color SCARLET_2 = new Color(204, 0, 0);
    public static final Color SCARLET_3 = new Color(164, 0, 0);
    public static final Color ORANGE_1 = new Color(252, 175, 62);
    public static final Color ORANGE_2 = new Color(245, 121, 0);
    public static final Color ORANGE_3 = new Color(206, 92, 0);
    public static final Color ALUMINIUM_1 = new Color(238, 238, 236);
    public static final Color ALUMINIUM_2 = new Color(211, 215, 207);
    public static final Color ALUMINIUM_3 = new Color(186, 189, 182);
    public static final Color ALUMINIUM_4 = new Color(136, 138, 133);
    public static final Color ALUMINIUM_5 = new Color(85, 87, 83);
    public static final Color ALUMINIUM_6 = new Color(46, 52, 54);
    public static final Color[] SEQUENCE_1;
    public static final Color[] SEQUENCE_2;
    public static final Color[] SEQUENCE_3;
    public static final Stroke THICK_STROKE;
    public static final Stroke NORMAL_STROKE;
    public static final Stroke FAT_DASHED_STROKE;
    public static final Stroke DASHED_STROKE;
    public static final Stroke LIGHT_DASHED_STROKE;
    private Map<Object, Color> colorMap = new HashMap();
    private int nextColorCount = 0;

    public static Color buildPercentageColor(Color floorColor, Color ceilColor, double shadePercentage) {
        return new Color(floorColor.getRed() + (int)(shadePercentage * (double)(ceilColor.getRed() - floorColor.getRed())), floorColor.getGreen() + (int)(shadePercentage * (double)(ceilColor.getGreen() - floorColor.getGreen())), floorColor.getBlue() + (int)(shadePercentage * (double)(ceilColor.getBlue() - floorColor.getBlue())));
    }

    public TangoColorFactory() {
    }

    public Color pickColor(Object object) {
        return (Color)this.colorMap.computeIfAbsent(object, (k) -> {
            return this.nextColor();
        });
    }

    private Color nextColor() {
        int colorIndex = this.nextColorCount % SEQUENCE_1.length;
        int shadeIndex = this.nextColorCount / SEQUENCE_1.length;
        Color color;
        if (shadeIndex == 0) {
            color = SEQUENCE_1[colorIndex];
        } else if (shadeIndex == 1) {
            color = SEQUENCE_2[colorIndex];
        } else if (shadeIndex == 2) {
            color = SEQUENCE_3[colorIndex];
        } else {
            shadeIndex -= 3;
            Color floorColor;
            Color ceilColor;
            if (shadeIndex % 2 == 0) {
                floorColor = SEQUENCE_2[colorIndex];
                ceilColor = SEQUENCE_1[colorIndex];
            } else {
                floorColor = SEQUENCE_3[colorIndex];
                ceilColor = SEQUENCE_2[colorIndex];
            }

            int base = shadeIndex / 2 + 1;

            int divisor;
            for(divisor = 2; base >= divisor; divisor *= 2) {
                ;
            }

            base = base * 2 - divisor + 1;
            double shadePercentage = (double)base / (double)divisor;
            color = buildPercentageColor(floorColor, ceilColor, shadePercentage);
        }

        ++this.nextColorCount;
        return color;
    }

    static {
        SEQUENCE_1 = new Color[]{CHAMELEON_1, BUTTER_1, SKY_BLUE_1, CHOCOLATE_1, PLUM_1};
        SEQUENCE_2 = new Color[]{CHAMELEON_2, BUTTER_2, SKY_BLUE_2, CHOCOLATE_2, PLUM_2};
        SEQUENCE_3 = new Color[]{CHAMELEON_3, BUTTER_3, SKY_BLUE_3, CHOCOLATE_3, PLUM_3};
        THICK_STROKE = new BasicStroke(2.0F);
        NORMAL_STROKE = new BasicStroke();
        FAT_DASHED_STROKE = new BasicStroke(1.0F, 1, 1, 1.0F, new float[]{7.0F, 3.0F}, 0.0F);
        DASHED_STROKE = new BasicStroke(1.0F, 1, 1, 1.0F, new float[]{4.0F, 4.0F}, 0.0F);
        LIGHT_DASHED_STROKE = new BasicStroke(1.0F, 1, 1, 1.0F, new float[]{3.0F, 7.0F}, 0.0F);
    }
}
