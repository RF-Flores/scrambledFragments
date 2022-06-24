import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Write a program to reassemble a given set of text fragments into their original sequence. For this
 * challenge your program should have a main method accepting one argument – the path to a well-formed
 * UTF-8 encoded text file. Each line in the file represents a test case of the main functionality
 * of your program: read it, process it and println to the console the corresponding defragmented output.
 *
 * Each line contains text fragments separated by a semicolon, ‘;’. You can assume that
 * every fragment has length at least 2.
 *
 * MAIN ASSUMPTIONS {
 *     1. No typos
 *     2. Each line will always lead to one final document
 *     3. No fragment is made up scrambled characters, a fragment always matches at least 1 other fragment
 * }
 *
 * PSEUDO CODE {
 *     1. split line into fragments
 *     2. remove duplicates
 *     3. retrieve the longest fragment
 *     4. go over each remaining fragment
 *     5. match them with the longest fragment
 *     6. see if they are prefix or suffix
 *     7. concat as prefix or suffix the one which has the longest match
 *     8. remove from list
 *     9. go over remaining fragments until none are left on the list
 * }
 *
 * ALGORITHM DETAILS {
 *    Examples
 *    i. "ABCDEF" and "DEFG" overlap with overlap length 3
 *    ii. "ABCDEF" and "XYZABC" overlap with overlap length 3
 *    iii. "ABCDEF" and "BCDE" overlap with overlap length 4
 *    iv. "ABCDEF" and "XCDEZ" do *not* overlap
 *
 *    Implementation remarks:
 *    1. The algorithm takes the largest fragment as the anchor and matches the smaller ones,
 *       this prevents strange matching behaviour
 *    2. i and ii are handled with string.regionMatches()
 *    3. iii is handled because the algorithm does not attempt to overlap with middle string,
 *       in similar examples no changes will be made when to anchor text if no changes are made suffix or prefix wise.
 *    4. iv it does not overlap as a suffix or prefix, handled by algorithm
 *    5. If at any point after looping over the remaining fragments the maximum matched characters is 0. This means
 *       list contains no more valid fragments to add as suffix or prefix. List is cleared.
 * }
 *
 * @author ricardoFlores
 *
 */
public class RicardoFlores {

    private final MatchData suffixData;
    private final MatchData prefixData;

    //Keep the maximally matched data
    private final MatchData maxMatchStrData;

    //String fragments list
    LinkedList<String> orderedSplitStrings;

    private RicardoFlores(LinkedList<String> orderedSplitStrings) {
        this.suffixData = new MatchData(true);
        this.prefixData = new MatchData(false);
        this.maxMatchStrData = new MatchData(false);
        this.orderedSplitStrings = orderedSplitStrings;
    }

    public static void main(String[] args) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(args[0]))) {
            in.lines()
                    .map(RicardoFlores::reassemble)
                    .forEach(System.out::println);
        }

    }

    public static String reassemble(String line) {
        //Split by semicolon
        LinkedList<String> orderedSplitStrings = new LinkedList<>(Arrays.asList(line.split(";")));
        //order by length
        orderedSplitStrings.sort(Comparator.comparingInt(String::length));
        //get anchor str
        String finalText = "";

        RicardoFlores algorithmEngine = new RicardoFlores(orderedSplitStrings);
        algorithmEngine.removeUnnecessaryFragments();
        while(orderedSplitStrings.size() > 1) {
            finalText = algorithmEngine.execute();
        }
        return orderedSplitStrings.size() == 1 ? orderedSplitStrings.removeFirst() : finalText;
    }

    private String execute(){
        maxMatchStrData.resetData();
        for (int currentStrIndex = orderedSplitStrings.size() - 1; currentStrIndex > 0; currentStrIndex--) {
            String currentString = orderedSplitStrings.get(currentStrIndex);
            for (int otherStrIndex = currentStrIndex - 1; otherStrIndex >= 0; otherStrIndex--) {
                String otherString = orderedSplitStrings.get(otherStrIndex);
                if(maxMatchStrData.getMaxMatch() > currentString.length()) break;
                calculateAndUpdateMaxRegionMatch(currentString, otherString, currentStrIndex, otherStrIndex);
            }
        }
        updateListWithMaxMatch();
        return orderedSplitStrings.getLast();
    }

    private void updateListWithMaxMatch() {
        if(orderedSplitStrings.size() <= 1) return;
        if(maxMatchStrData.getMaxMatch() == 0) {
            noMatchesListUpdate();
            return;
        }
        String leftSideText;
        String rightSideText;
        if(!maxMatchStrData.isSuffix()) {
            leftSideText = orderedSplitStrings.remove(maxMatchStrData.getOtherStrIndexInList());
            leftSideText = leftSideText.substring(0,leftSideText.length() - maxMatchStrData.getMaxMatch());
            rightSideText = orderedSplitStrings.remove(maxMatchStrData.getCurrentStrIndexInList() - 1);
        } else {
            leftSideText = orderedSplitStrings.remove(maxMatchStrData.getCurrentStrIndexInList());
            leftSideText = leftSideText.substring(0,leftSideText.length() - maxMatchStrData.getMaxMatch());
            rightSideText = orderedSplitStrings.remove(maxMatchStrData.getOtherStrIndexInList());
        }
        orderedSplitStrings.add(leftSideText + rightSideText);
    }

    private void noMatchesListUpdate() {
        String finalString = orderedSplitStrings.removeLast();
        String biggestStringInList = orderedSplitStrings.getLast();
        //Despite any matching, biggest string reigns.
        finalString = biggestStringInList.length() > finalString.length() ? biggestStringInList : finalString;
        orderedSplitStrings.clear();
        orderedSplitStrings.add(finalString);
    }

    private void calculateAndUpdateMaxRegionMatch(String currentString, String otherString, int currentStringIndex, int otherStringIndex) {
        prepareDataForNewStrIteration(suffixData, prefixData);
        calculatePrefixAndSuffixMatch(currentString,otherString);
        updateMaxMatchData(currentStringIndex, otherStringIndex);
    }

    private void updateMaxMatchData(int currentStrIndex, int otherStringIndex) {
        if(suffixData.hasLongerMatch(maxMatchStrData) && suffixData.hasLongerMatch(prefixData)) {
            suffixData.copyDataTo(maxMatchStrData);
            maxMatchStrData.setOtherStrIndexInList(otherStringIndex);
            maxMatchStrData.setCurrentStrIndexInList(currentStrIndex);
        } else if (prefixData.hasLongerMatch(maxMatchStrData)) {
            prefixData.copyDataTo(maxMatchStrData);
            maxMatchStrData.setOtherStrIndexInList(otherStringIndex);
            maxMatchStrData.setCurrentStrIndexInList(currentStrIndex);
        }
    }

    private void calculatePrefixAndSuffixMatch(String currentString, String otherString) {
        for(int characterIndex = 0; characterIndex < currentString.length(); characterIndex++) {
            //Expected matched length
            int numberOfCharactersToMatch = characterIndex + 1;

            //Suffix matching
            //Calculate right side string start index for suffix
            int rightSideStringMatchOffset = currentString.length() - 1 - characterIndex;
            if(currentString.regionMatches(rightSideStringMatchOffset, otherString, 0, numberOfCharactersToMatch)) {
                suffixData.setMaxMatch(numberOfCharactersToMatch);
            }

            //Prefix matching
            //Calculate left side string start index for prefix
            int leftStringMatchOffset = otherString.length() - 1 - characterIndex;
            if(currentString.regionMatches(0, otherString, leftStringMatchOffset, numberOfCharactersToMatch)) {
                prefixData.setMaxMatch(numberOfCharactersToMatch);
            }
        }
    }

    private void removeUnnecessaryFragments() {
        for (int currentStrIndex = orderedSplitStrings.size() - 1; currentStrIndex > 0; currentStrIndex--) {
            String currentString = orderedSplitStrings.get(currentStrIndex);
            for(Iterator<String> iterator = orderedSplitStrings.iterator(); iterator.hasNext();) {
                String otherString = iterator.next();
                if(currentString.contains(otherString) && !currentString.equals(otherString)) {
                    iterator.remove();
                    currentStrIndex--;
                }
            }
        }
    }

    private void prepareDataForNewStrIteration(MatchData suffixData, MatchData prefixData) {
        suffixData.resetData();
        prefixData.resetData();
    }

    private static class MatchData {
        private int maxMatch = 0;

        private boolean isSuffix;

        //Only required for max Match
        private int otherStrIndexInList = 0;
        private int currentStrIndexInList = 0;

        public MatchData(boolean isSuffix) {
            this.isSuffix = isSuffix;
        }

        public int getMaxMatch() {
            return maxMatch;
        }

        public void setMaxMatch(int maxMatch) {
            this.maxMatch = maxMatch;
        }

        public boolean isSuffix() {
            return isSuffix;
        }

        public void setSuffix(boolean suffix) {
            isSuffix = suffix;
        }

        public int getOtherStrIndexInList() {
            return otherStrIndexInList;
        }

        public void setOtherStrIndexInList(int otherStrIndexInList) {
            this.otherStrIndexInList = otherStrIndexInList;
        }

        public int getCurrentStrIndexInList() {
            return currentStrIndexInList;
        }

        public void setCurrentStrIndexInList(int currentStrIndexInList) {
            this.currentStrIndexInList = currentStrIndexInList;
        }

        //Methods
        public boolean hasLongerMatch(MatchData otherData) {
            return this.maxMatch > otherData.getMaxMatch();
        }

        public void resetData() {
            this.maxMatch = 0;
        }

        public void copyDataTo(MatchData otherData) {
            otherData.setMaxMatch(this.maxMatch);
            otherData.setSuffix(this.isSuffix);
        }
    }
}
