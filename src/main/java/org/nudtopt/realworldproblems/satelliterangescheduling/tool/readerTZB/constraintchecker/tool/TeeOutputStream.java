package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.tool;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Xu Shilong
 * @version 1.0
 */
public class TeeOutputStream extends OutputStream {
    private final OutputStream out1;
    private final OutputStream out2;

    public TeeOutputStream(OutputStream out1, OutputStream out2) {
        this.out1 = out1;
        this.out2 = out2;
    }

    @Override
    public void write(int b) throws IOException {
        out1.write(b);
        out2.write(b);
    }
}
