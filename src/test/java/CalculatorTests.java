import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CalculatorTests {

    @Test
    public void testRequirementOne(){
        String expectedString = "ABCDEFG";
        String inputString= "ABCDEF;DEFG";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void testRequirementTwo(){
        String expectedString = "XYZABCDEF";
        String inputString= "ABCDEF;XYZABC";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void testRequirementThree(){
        String expectedString = "ABCDEF";
        String inputString= "ABCDEF;BCDE";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void testRequirementFour(){
        String expectedString = "ABCDEF";
        String inputString= "ABCDEF;XCDEZ";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void testExampleOne(){
        String expectedString = "O draconian devil! Oh lame saint!";
        String inputString= "O draconia;conian devil! Oh la;h lame sa;saint!";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void testExampleTwo(){
        String expectedString = "Neque porro quisquam est," +
                " qui dolorem ipsum quia dolor sit amet," +
                " consectetur, adipisci velit," +
                " sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.";
        String inputString= "m quaerat voluptatem.;pora incidunt ut labore et d;, " +
                "consectetur, adipisci velit;olore magnam aliqua;idunt ut labore et dolore " +
                "magn;uptatem.;i dolorem ipsum qu;iquam quaerat vol;psum quia dolor sit amet, " +
                "consectetur, a;ia dolor sit amet, conse;squam est, qui do;Neque porro quisquam est, " +
                "qu;aerat voluptatem.;m eius modi tem;Neque porro qui;, sed quia non numquam ei;lorem " +
                "ipsum quia dolor sit amet;ctetur, adipisci velit, sed quia non numq;unt ut labore et " +
                "dolore magnam aliquam qu;dipisci velit, sed quia non numqua;us modi tempora incid;Neque " +
                "porro quisquam est, qui dolorem i;uam eius modi tem;pora inc;am al";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    //First try failed tests

    @Test
    public void failedTestOne() {
        String expectedString = "repeat, now repeat!";
        String inputString= "now repeat; repeat!;repeat, now";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }


    @Test
    public void failedTestTwo() {
        String expectedString = "abcdefghabk";
        String inputString= "abcdef;abcdef;fghab;habk";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void failedTestThree() {
        String expectedString = "abcccdefffccc";
        String inputString= "ab;bcccde;cccd;cdefff;fffccc";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void failedTestFour() {
        String expectedString = "abcdefghijkghi";
        String inputString= "abcde;bcdef;fghij;ghijk;jkghi";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void failedTestFive() {
        String expectedString = "target star cluster";
        String inputString= "star;target s;get star cluster";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void failedTestSix() {
        String expectedString = "He owns a clown";
        String inputString= "owns;He owns a;wns a clown";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }

    @Test
    public void failedTestSeven() {
        String expectedString = "xxefhijkabcdefh";
        String inputString= "jkabcdefh;xxefhi;efhijk";

        Assertions.assertEquals(expectedString,RicardoFlores.reassemble(inputString));
    }


}