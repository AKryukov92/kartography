package org.lizaaler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lizaalert.InitialConfig;
import org.lizaalert.ValidConfig;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ValidConfigTest {
    File navigatorEmulation = new File("./target/navigator");
    File workingDirEmulation = new File("./2023-02-09_Omsk");

    @BeforeAll
    public void emulate() {
        File emulationContent = new File(navigatorEmulation.getAbsolutePath() + InitialConfig.gpxSubdirectory);
        navigatorEmulation.mkdirs();
        workingDirEmulation.mkdirs();
    }

    @Test
    public void checkOperationName() {
        ValidConfig conf;
        conf = new ValidConfig("2023-02-09_Omsk", navigatorEmulation, workingDirEmulation);
        assertEquals("2023-02-09_Omsk", conf.operationName);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new ValidConfig("", navigatorEmulation, workingDirEmulation)
        );
        assertEquals("", ex.getMessage());
    }
}
