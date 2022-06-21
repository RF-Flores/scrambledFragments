import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class CalculatorTests {
    private final RicardoFlores _algorithmEngine = new RicardoFlores();
    private LinkedList<String> orderedSplitStrings;

    @Test
    public void testExampleOne(){
        String expectedString = "ABCDEFG";
        String inputString= "ABCDEF;DEFG";

        String anchorText = setupFragmentList(inputString);
        Assertions.assertEquals(expectedString,_algorithmEngine.execute(orderedSplitStrings,anchorText));
    }

    @Test
    public void testExampleTwo(){
        String expectedString = "XYZABCDEF";
        String inputString= "ABCDEF;XYZABC";

        String anchorText = setupFragmentList(inputString);
        Assertions.assertEquals(expectedString,_algorithmEngine.execute(orderedSplitStrings,anchorText));
    }

    @Test
    public void testExampleThree(){
        String expectedString = "ABCDEF";
        String inputString= "ABCDEF;BCDE";

        String anchorText = setupFragmentList(inputString);
        Assertions.assertEquals(expectedString,_algorithmEngine.execute(orderedSplitStrings,anchorText));
    }

    @Test
    public void testExampleFour(){
        String expectedString = "ABCDEF";
        String inputString= "ABCDEF;XCDEZ";

        String anchorText = setupFragmentList(inputString);
        Assertions.assertEquals(expectedString,_algorithmEngine.execute(orderedSplitStrings,anchorText));
    }


    private String setupFragmentList(String inputString) {
        orderedSplitStrings = new LinkedList<>(Arrays.asList(inputString.split(";")));
        orderedSplitStrings.sort(Comparator.comparingInt(String::length));
        return orderedSplitStrings.removeLast();
    }
}