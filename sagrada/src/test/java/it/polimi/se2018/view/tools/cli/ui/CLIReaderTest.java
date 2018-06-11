package it.polimi.se2018.view.tools.cli.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

/**
 * Tests for CLIReader class
 *
 * @author Mathyas Giudici
 */

public class CLIReaderTest {

    private final String enter = System.lineSeparator();

    private CLIReader cliReader;

    private CLIPrinter cliPrinter;

    @Before
    public void testInit() {
        cliPrinter = new CLIPrinter();
    }

    @Test
    public void testRead() throws Exception{
        String message = "Test";
        System.setIn(new ByteArrayInputStream((message + enter).getBytes()));

        this.cliReader = new CLIReader(this.cliPrinter);
        String returnString = this.cliReader.read();

        assertEquals(message, returnString);
    }

    @Test
    public void testReadInt() throws Exception{
        int message = 1;
        System.setIn(new ByteArrayInputStream((message + enter).getBytes()));

        this.cliReader = new CLIReader(this.cliPrinter);
        int returnNumber = this.cliReader.readInt();

        assertEquals(message, returnNumber);
    }

    @Test
    public void testReadIntFail() throws Exception{
        String message = "fail" + enter + "11";
        System.setIn(new ByteArrayInputStream((message + enter).getBytes()));

        this.cliReader = new CLIReader(this.cliPrinter);
        int returnNumber = this.cliReader.readInt();

        assertEquals(11, returnNumber);
    }

    @Test
    public void testChooseYes() throws Exception{
        String message = "a" + enter + "y" + enter;
        System.setIn(new ByteArrayInputStream(message.getBytes()));

        this.cliReader = new CLIReader(this.cliPrinter);
        boolean returnValue = this.cliReader.chooseYes();

        assertTrue(returnValue);
    }

    @Test
    public void testChooseNo() throws Exception{
        String message = "n";
        System.setIn(new ByteArrayInputStream((message + enter).getBytes()));

        this.cliReader = new CLIReader(this.cliPrinter);
        boolean returnValue = this.cliReader.chooseYes();

        assertFalse(returnValue);
    }


    @Test
    public void testChooseInRangeFirst() throws Exception{
        String numberString = "0";
        System.setIn(new ByteArrayInputStream((numberString + enter).getBytes()));

        this.cliReader = new CLIReader(this.cliPrinter);
        int returnValue = this.cliReader.chooseInRange(0, 3);

        assertEquals(0, returnValue);
    }

    @Test
    public void testChooseInRangeSecond() throws Exception{
        String numberString = "0" + enter + "4" + enter + "2";
        System.setIn(new ByteArrayInputStream((numberString + enter).getBytes()));

        this.cliReader = new CLIReader(this.cliPrinter);
        int returnValue = this.cliReader.chooseInRange(1, 3);

        assertEquals(2, returnValue);
    }

    @Test
    public void testChooseBetweenFirstSecond() throws Exception{
        String numberString = "1";
        System.setIn(new ByteArrayInputStream((numberString + enter).getBytes()));

        this.cliReader = new CLIReader(this.cliPrinter);
        int returnValue = this.cliReader.chooseBetweenTwo(1, 2);

        assertEquals(1, returnValue);
    }

    @Test
    public void testChooseBetweenTwoSecond() throws Exception{
        String numberString = "0" + enter + "2";
        System.setIn(new ByteArrayInputStream((numberString + enter).getBytes()));

        this.cliReader = new CLIReader(this.cliPrinter);
        int returnValue = this.cliReader.chooseBetweenTwo(1, 2);

        assertEquals(2, returnValue);
    }

    @Test
    public void testInterrupt(){
        this.cliReader = new CLIReader(this.cliPrinter);
        this.cliReader.interrupt();
    }

    @After
    public void testCloseOperation() {
        System.setIn(System.in);
        cliReader.close();
    }
}