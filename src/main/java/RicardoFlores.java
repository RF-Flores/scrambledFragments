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
        orderedSplitStrings.sort(Comparator.comparingInt(String::length).reversed());
        //get anchor str
        String finalText = "";

        RicardoFlores algorithmEngine = new RicardoFlores(orderedSplitStrings);
        while(orderedSplitStrings.size() > 1) {
            finalText = algorithmEngine.execute();
        }
        return finalText;
    }

    private String execute(){
        //remove unnecessary smaller fragments from list
        removeUnnecessaryFragments();
        for (int currentStrIndex = orderedSplitStrings.size() - 1; currentStrIndex > 0; currentStrIndex--) {
            String currentString = orderedSplitStrings.get(currentStrIndex);
            for (int otherStrIndex = currentStrIndex - 1; otherStrIndex >= 0; otherStrIndex--) {
                String otherString = orderedSplitStrings.get(otherStrIndex);
                calculateAndUpdateMaxRegionMatch(currentString, otherString, currentStrIndex, otherStrIndex);
            }
        }
        updateListWithMaxMatch();
        maxMatchStrData.resetData();
        return orderedSplitStrings.getLast();
    }

    private void updateListWithMaxMatch() {
        if(orderedSplitStrings.size() == 1) return;
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
        String firstString = orderedSplitStrings.getFirst();
        //Despite any matching, bigger string reigns
        finalString = firstString.length() > finalString.length() ? firstString : finalString;
        orderedSplitStrings.clear();
        orderedSplitStrings.add(finalString);
    }

    private void calculateAndUpdateMaxRegionMatch(String currentString, String otherString, int currentStringIndex, int otherStringIndex) {
        prepareDataForNewStrIteration(suffixData, prefixData, currentString.length());
        calculatePrefixAndSuffixMatch(currentString,otherString);
        updateMaxMatchData(currentStringIndex, otherStringIndex);
    }

    private void updateMaxMatchData(int currentStrIndex, int otherStringIndex) {
        if(suffixData.hasLongerMatch(maxMatchStrData) && suffixData.hasLongerMatch(prefixData)) {
            suffixData.copyDataTo(maxMatchStrData);
            maxMatchStrData.setOtherStrIndexInList(otherStringIndex);
            maxMatchStrData.setCurrentStrIndexInList(currentStrIndex);
        } else if (prefixData.hasLongerMatch(suffixData)) {
            prefixData.copyDataTo(maxMatchStrData);
            maxMatchStrData.setOtherStrIndexInList(otherStringIndex);
            maxMatchStrData.setCurrentStrIndexInList(currentStrIndex);
        }
    }

    private void calculatePrefixAndSuffixMatch(String currentString, String otherString) {
        for(int characterIndex = 0; characterIndex < currentString.length(); characterIndex++) {
            //Expected matched length
            int numberOfCharactersToMatch = characterIndex + 1;

            //Calculate anchor region start index for suffix
            int anchorRegionMatchOffset = currentString.length() - 1 - characterIndex;
            //Suffix matching
            if(currentString.regionMatches(anchorRegionMatchOffset, otherString, 0, numberOfCharactersToMatch)) {
                suffixData.setMaxMatch(numberOfCharactersToMatch);
                //suffixData.setAnchorIdUpperSubstringIndex(anchorRegionMatchOffset);
            }

            //Calculate currentStr region start index for suffix
            int currentStrRegionMatchOffset = otherString.length() - 1 - characterIndex;
            //Prefix matching
            if(currentString.regionMatches(0, otherString, currentStrRegionMatchOffset, numberOfCharactersToMatch)) {
                prefixData.setMaxMatch(numberOfCharactersToMatch);
                //prefixData.setAnchorIdLowerSubstringIndex(numberOfCharactersToMatch);
            }
        }
    }

    private void removeUnnecessaryFragments() {
        for (int currentStrIndex = orderedSplitStrings.size() - 1; currentStrIndex > 0; currentStrIndex--) {
            String currentString = orderedSplitStrings.get(currentStrIndex);
            if(checkStringContainedInOtherString(currentString, currentStrIndex)) {
                orderedSplitStrings.remove(currentStrIndex);
            }
        }
    }

    private boolean checkStringContainedInOtherString(String currentString, int currentStrIndex) {
        for (int otherStrIndex = currentStrIndex - 1; otherStrIndex >= 0; otherStrIndex--) {
            String otherString = orderedSplitStrings.get(otherStrIndex);
            if (otherString.contains(currentString)) {
                return true;
            }
        }
        return false;
    }

    private void prepareDataForNewStrIteration(MatchData suffixData, MatchData prefixData, int currentStrLength) {
        suffixData.resetData();
        //Anchor text substring starting index for suffix
        suffixData.setAnchorIdLowerSubstringIndex(0);
        prefixData.resetData();
        //Anchor text substring ending index for prefix
        prefixData.setAnchorIdUpperSubstringIndex(currentStrLength);
    }

    private static class MatchData {
        private int maxMatch = 0;
        private int anchorIdUpperSubstringIndex = 0;
        private int anchorIdLowerSubstringIndex = 0;

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

        public int getAnchorIdUpperSubstringIndex() {
            return anchorIdUpperSubstringIndex;
        }

        public void setAnchorIdUpperSubstringIndex(int anchorIdUpperSubstringIndex) {
            this.anchorIdUpperSubstringIndex = anchorIdUpperSubstringIndex;
        }

        public int getAnchorIdLowerSubstringIndex() {
            return anchorIdLowerSubstringIndex;
        }

        public void setAnchorIdLowerSubstringIndex(int anchorIdLowerSubstringIndex) {
            this.anchorIdLowerSubstringIndex = anchorIdLowerSubstringIndex;
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
            this.anchorIdLowerSubstringIndex = 0;
            this.anchorIdUpperSubstringIndex = 0;
        }

        public void copyDataTo(MatchData otherData) {
            otherData.setMaxMatch(this.maxMatch);
            otherData.setAnchorIdLowerSubstringIndex(this.anchorIdLowerSubstringIndex);
            otherData.setAnchorIdUpperSubstringIndex(this.anchorIdUpperSubstringIndex);
            otherData.setSuffix(this.isSuffix);
        }
    }
}
