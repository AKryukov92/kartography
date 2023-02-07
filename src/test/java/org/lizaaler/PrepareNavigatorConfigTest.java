package org.lizaaler;

import org.junit.Assert;
import org.junit.Test;
import org.lizaalert.PrepareNavigatorConfig;

import java.io.File;

public class PrepareNavigatorConfigTest {
    @Test
    public void shouldShowHelp() {
        String[] args = "-help".split(" ");
        PrepareNavigatorConfig conf = new PrepareNavigatorConfig(args);
        Assert.assertTrue(conf.showHelp);

        conf = new PrepareNavigatorConfig("-help -name 2023-02-08_Omsk".split(" "));
        Assert.assertTrue(conf.showHelp);

        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk -help".split(" "));
        Assert.assertTrue(conf.showHelp);

        conf = new PrepareNavigatorConfig(new String[0]);
        Assert.assertTrue(conf.showHelp);

        conf = new PrepareNavigatorConfig(" ".split(" "));
        Assert.assertTrue(conf.showHelp);

        conf = new PrepareNavigatorConfig("2023-02-08_Omsk", "", "");
        Assert.assertFalse(conf.showHelp);
    }

    @Test
    public void shouldFillOperationName() {
        PrepareNavigatorConfig conf;
        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk".split(" "));
        Assert.assertEquals("2023-02-08_Omsk", conf.operationName);
        conf = new PrepareNavigatorConfig("-name 2023-02-08_Wei.rd_n'ame11".split(" "));
        Assert.assertEquals("2023-02-08_Wei.rd_n'ame11", conf.operationName);

        conf = new PrepareNavigatorConfig("2023-02-08_Omsk", "", "");
        Assert.assertEquals("2023-02-08_Omsk", conf.operationName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void operationNameContainIllegalSymbol() {
        new PrepareNavigatorConfig("-name 2023-02-08".split(" "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void operationNameWasNotSpecified() {
        new PrepareNavigatorConfig("-name".split(" "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void operationNameWasMissing() {
        new PrepareNavigatorConfig("-name -disk".split(" "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void operationNameWasIncorrect() {
        new PrepareNavigatorConfig("-disk", "", "");
    }

    @Test
    public void shouldFillDisk() {
        PrepareNavigatorConfig conf;
        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk".split(" "));
        Assert.assertEquals(PrepareNavigatorConfig.DEFAULT_PARAM, conf.disk);
        Assert.assertTrue(conf.isDiskDefault());

        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk -disk".split(" "));
        Assert.assertEquals(PrepareNavigatorConfig.DEFAULT_PARAM, conf.disk);
        Assert.assertTrue(conf.isDiskDefault());

        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk -disk .".split(" "));
        Assert.assertEquals(".", conf.disk);
        Assert.assertFalse(conf.isDiskDefault());

        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk -disk E".split(" "));
        Assert.assertEquals("E", conf.disk);
        Assert.assertFalse(conf.isDiskDefault());

        conf = new PrepareNavigatorConfig("-disk E -name 2023-02-08_Omsk".split(" "));
        Assert.assertEquals("E", conf.disk);
        Assert.assertFalse(conf.isDiskDefault());

        conf = new PrepareNavigatorConfig("2023-02-08_Omsk", "", "");
        Assert.assertEquals(PrepareNavigatorConfig.DEFAULT_PARAM, conf.disk);
        Assert.assertTrue(conf.isDiskDefault());

        conf = new PrepareNavigatorConfig("2023-02-08_Omsk", "E", "");
        Assert.assertEquals("E", conf.disk);
        Assert.assertFalse(conf.isDiskDefault());
    }

    @Test
    public void shouldFillWorkdir() {
        PrepareNavigatorConfig conf;
        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk".split(" "));
        Assert.assertEquals(PrepareNavigatorConfig.DEFAULT_PARAM, conf.workingDir);

        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk -workdir".split(" "));
        Assert.assertEquals(PrepareNavigatorConfig.DEFAULT_PARAM, conf.workingDir);

        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk -workdir .".split(" "));
        Assert.assertEquals(PrepareNavigatorConfig.DEFAULT_PARAM, conf.workingDir);

        conf = new PrepareNavigatorConfig("-name 2023-02-08_Omsk -workdir ./workdir".split(" "));
        Assert.assertEquals("./workdir", conf.workingDir);
    }

    @Test
    public void resolveWorkingDirTest() {
        String operationName = "2023-02-08_Omsk";
        PrepareNavigatorConfig conf;
        conf = new PrepareNavigatorConfig(operationName, "", ".");
        File here = new File("./" + operationName);
        Assert.assertEquals(here.getAbsolutePath(), conf.resolveWorkingDir());

        conf = new PrepareNavigatorConfig(operationName, "", "./target/workdir");
        File workdirInTarget = new File("./target/workdir");
        workdirInTarget.mkdir();
        Assert.assertEquals(workdirInTarget.getAbsolutePath(), conf.resolveWorkingDir());
    }


    @Test
    public void resolveDiskTest() {
        String operationName = "2023-02-08_Omsk";
        PrepareNavigatorConfig conf;
        conf = new PrepareNavigatorConfig(operationName, "", "");
        File disk = new File("E:/");
        Assert.assertEquals(disk.getAbsolutePath(), conf.resolveDisk());
    }
}
