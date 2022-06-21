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

    public static void main(String[] args) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(args[0]))) {
            in.lines()
                    .map(RicardoFlores::reassemble)
                   .forEach(System.out::println);
        }

    }

    private static String reassemble(String line) {
        //Split by semicolon
        LinkedList<String> orderedSplitStrings = new LinkedList<>(Arrays.asList(line.split(";")));
        //order by length
        orderedSplitStrings.sort(Comparator.comparingInt(String::length));
        //get anchor str
        String anchorText = orderedSplitStrings.removeLast();

        RicardoFlores algorithmEngine = new RicardoFlores();
        while(!orderedSplitStrings.isEmpty()) {
            anchorText = algorithmEngine.execute(orderedSplitStrings,anchorText);
        }
        return anchorText;
    }

    public String execute(LinkedList<String> orderedSplitStrings, String anchorText) {
        //Keep the maximally matched data
        MatchData maxMatchStrData = new MatchData();

        //Matched data from the currentStr
        MatchData suffixData = new MatchData();
        MatchData prefixData = new MatchData();
        prefixData.setSuffix(false);

        int anchorLength;
        String currentStr;
        int currentStrLength;

        for (int currentStrIndex = 0; currentStrIndex < orderedSplitStrings.size(); currentStrIndex ++) {
            anchorLength = anchorText.length();
            currentStr = orderedSplitStrings.get(currentStrIndex);
            currentStrLength = currentStr.length();

            //Matched data reset
            prepareDataForNewStrIteration(suffixData, prefixData, anchorLength);
            //looping over the string to figure out the max match as suffix and prefix
            for(int characterIndex = 0; characterIndex < currentStrLength; characterIndex++) {
                //Expected matched length
                int numberOfCharactersToMatch = characterIndex + 1;

                //Calculate anchor region start index for suffix
                int anchorRegionMatchOffset = anchorLength - 1 - characterIndex;
                //Suffix matching
                if(anchorText.regionMatches(anchorRegionMatchOffset, currentStr, 0, numberOfCharactersToMatch)) {
                    suffixData.setMaxMatch(numberOfCharactersToMatch);
                    suffixData.setAnchorIdUpperSubstringIndex(anchorRegionMatchOffset);
                }

                //Calculate currentStr region start index for suffix
                int currentStrRegionMatchOffset = currentStrLength - 1 - characterIndex;
                //Prefix matching
                if(anchorText.regionMatches(0, currentStr, currentStrRegionMatchOffset, numberOfCharactersToMatch)) {
                    prefixData.setMaxMatch(numberOfCharactersToMatch);
                    prefixData.setAnchorIdLowerSubstringIndex(numberOfCharactersToMatch);
                }
            }
            if(suffixData.hasLongerMatch(maxMatchStrData)) {
                suffixData.copyDataTo(maxMatchStrData);
                maxMatchStrData.setStrIndexInList(currentStrIndex);
            }
            if(prefixData.hasLongerMatch(maxMatchStrData)) {
                prefixData.copyDataTo(maxMatchStrData);
                maxMatchStrData.setStrIndexInList(currentStrIndex);
            }
        }

        return concatMaxMatchAndSanitizeList(orderedSplitStrings,anchorText,maxMatchStrData);
    }

    private void prepareDataForNewStrIteration(MatchData suffixData, MatchData prefixData, int anchorLength) {
        suffixData.resetData();
        //Anchor text substring starting index for suffix
        suffixData.setAnchorIdLowerSubstringIndex(0);
        prefixData.resetData();
        //Anchor text substring ending index for prefix
        prefixData.setAnchorIdUpperSubstringIndex(anchorLength);
    }

    private String concatMaxMatchAndSanitizeList(LinkedList<String> orderedSplitStrings, String anchorText,
                                                 MatchData maxMatchStrData) {
        if(maxMatchStrData.getMaxMatch() == 0) {
            //Edge case, fragments left do not match anchor as suffix or prefix
            orderedSplitStrings.clear();
            return anchorText;
        }
        String textToAdd = orderedSplitStrings.remove(maxMatchStrData.getStrIndexInList());
        if(maxMatchStrData.getMaxMatch() == textToAdd.length()) {
            //Edge case, max matching fragment is already present in anchor, no changes needed to anchor
            return anchorText;
        }
        String cutAnchorText = anchorText.substring(
                maxMatchStrData.getAnchorIdLowerSubstringIndex(),
                maxMatchStrData.getAnchorIdUpperSubstringIndex());
        return maxMatchStrData.isSuffix() ? cutAnchorText + textToAdd : textToAdd + cutAnchorText;
    }

    private class MatchData {
        private int maxMatch = 0;
        private int anchorIdUpperSubstringIndex = 0;
        private int anchorIdLowerSubstringIndex = 0;

        private boolean isSuffix = true;

        //Only required for max Match
        private int strIndexInList = 0;

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

        public int getStrIndexInList() {
            return strIndexInList;
        }

        public void setStrIndexInList(int strIndexInList) {
            this.strIndexInList = strIndexInList;
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
