package org.lizaalert;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class InitialConfigTest {
    @Test
    public void shouldShowHelp() {
        String[] args = "-help".split(" ");
        InitialConfig conf = new InitialConfig(args);
        assertTrue(conf.showHelp);

        conf = new InitialConfig("-help -name 2023-02-08_Omsk".split(" "));
        assertTrue(conf.showHelp);

        conf = new InitialConfig("-name 2023-02-08_Omsk -help".split(" "));
        assertTrue(conf.showHelp);

        conf = new InitialConfig(new String[0]);
        assertTrue(conf.showHelp);

        conf = new InitialConfig(" ".split(" "));
        assertTrue(conf.showHelp);
    }

    @Test
    public void shouldFillOperationName() {
        InitialConfig conf;
        conf = new InitialConfig("-name 2023-02-08_Omsk".split(" "));
        assertEquals("2023-02-08_Omsk", conf.operationName);
        conf = new InitialConfig("-name 2023-02-08_Wei.rd_n'ame11".split(" "));
        assertEquals("2023-02-08_Wei.rd_n'ame11", conf.operationName);
    }

    @Test
    public void operationNameContainIllegalSymbol() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new InitialConfig("-name 2023-02-08".split(" "))
        );
    }

    @Test
    public void operationNameWasNotSpecified() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new InitialConfig("-name".split(" "))
        );
    }

    @Test
    public void operationNameWasMissing() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new InitialConfig("-name -disk".split(" "))
        );
    }

    @Test
    public void shouldFillDisk() {
        InitialConfig conf;
        conf = new InitialConfig("-name 2023-02-08_Omsk".split(" "));
        assertEquals(InitialConfig.DEFAULT_PARAM, conf.disk);
        assertTrue(conf.isDiskDefault());

        conf = new InitialConfig("-name 2023-02-08_Omsk -disk".split(" "));
        assertEquals(InitialConfig.DEFAULT_PARAM, conf.disk);
        assertTrue(conf.isDiskDefault());

        conf = new InitialConfig("-name 2023-02-08_Omsk -disk .".split(" "));
        assertEquals(".", conf.disk);
        assertFalse(conf.isDiskDefault());

        conf = new InitialConfig("-name 2023-02-08_Omsk -disk E".split(" "));
        assertEquals("E", conf.disk);
        assertFalse(conf.isDiskDefault());

        conf = new InitialConfig("-disk E -name 2023-02-08_Omsk".split(" "));
        assertEquals("E", conf.disk);
        assertFalse(conf.isDiskDefault());

        conf = new InitialConfig("-name 2023-02-08_Omsk".split(" "));
        assertEquals(InitialConfig.DEFAULT_PARAM, conf.disk);
        assertTrue(conf.isDiskDefault());

        conf = new InitialConfig("-disk E -name 2023-02-08_Omsk".split(" "));
        assertEquals("E", conf.disk);
        assertFalse(conf.isDiskDefault());
    }

    @Test
    public void shouldFillWorkdir() {
        InitialConfig conf;
        conf = new InitialConfig("-name 2023-02-08_Omsk".split(" "));
        assertEquals(InitialConfig.DEFAULT_PARAM, conf.workingDir);

        conf = new InitialConfig("-name 2023-02-08_Omsk -workdir".split(" "));
        assertEquals(InitialConfig.DEFAULT_PARAM, conf.workingDir);

        conf = new InitialConfig("-name 2023-02-08_Omsk -workdir .".split(" "));
        assertEquals(InitialConfig.DEFAULT_PARAM, conf.workingDir);

        conf = new InitialConfig("-name 2023-02-08_Omsk -workdir ./workdir".split(" "));
        assertEquals("./workdir", conf.workingDir);
    }

    @Test
    public void resolveWorkingDirTest() {
        String operationName = "2023-02-08_Omsk";
        InitialConfig conf;
        conf = new InitialConfig("-name 2023-02-08_Omsk -workdir .".split(" "));
        File here = new File("./" + operationName);
        assertEquals(here, conf.resolveWorkingDir());

        conf = new InitialConfig("-name 2023-02-08_Omsk -workdir ./target/workdir".split(" "));
        File workdirInTarget = new File("./target/workdir");
        workdirInTarget.mkdir();
        assertEquals(workdirInTarget, conf.resolveWorkingDir());
    }

    @Test
    public void resolveRealNavigatorDiskTest() {
        //тест работает только с подключенным навигатором. Возможно надо уточнить букву диска
        String operationName = "";
        InitialConfig conf;
        conf = new InitialConfig("-name 2023-02-08_Omsk".split(" "));
        File disk = new File("E:/");
        assertEquals(disk, conf.resolveDisk());
    }

    @Test
    public void resolveSpecifiedDisk() {
        File navigatorEmulation = new File("./target/navigator");
        File emulationContent = new File(navigatorEmulation.getAbsolutePath() + InitialConfig.gpxSubdirectory);
        navigatorEmulation.mkdirs();
        InitialConfig conf;
        conf = new InitialConfig("-name 2023-02-08_Omsk -disk ./target/navigator".split(" "));
        assertEquals(navigatorEmulation, conf.resolveDisk());
    }
}
