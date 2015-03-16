package com.andromeda.cadbane.search;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;

public class StringSearchQuery {

	public StringSearchQuery() {

	}

	public TextSearchRequestor getRequestor() {
		return new TextSearchResultCollector();
	}

	private final static class TextSearchResultCollector extends
			TextSearchRequestor {

		private static final String STRINGS = "strings.xml";
		private ArrayList cachedMatches;

		private TextSearchResultCollector() {

		}

		public boolean acceptFile(IFile file) throws CoreException {
			// Omit all the files that shouldn't be checked: strings.xml
			// Search only the .xml files, inside the layout ?? What if there is
			// a reference is other xml like animator or styles
			if(STRINGS.equals(file.getName())){
				return false;
			}
			return true;
		}

		public boolean reportBinaryFile(IFile file) {
			// Don't care about the binary files
			return false;
		}

		public boolean acceptPatternMatch(TextSearchMatchAccess matchRequestor)
				throws CoreException {
			// Find the "id" from the view
			System.out.println("FileNAME : "+matchRequestor.getFile().getName());
			int matchOffset = matchRequestor.getMatchOffset();
			System.out.println("MatchOFFSET : "+matchOffset);
			System.out.println("MatchLENGTH : "+matchRequestor.getMatchLength());
			int startIndex = indexOfChar(matchRequestor, '>', true);
			int endIndex = indexOfChar(matchRequestor, '<', false);
			String contents = getContents(matchRequestor, startIndex, endIndex);
			System.out.println("CONTENTS\n"+contents);
			Pattern patternToFind = Pattern.compile("android:id=\"@(?:\\+)?id/([^\"]*)\"");
			String idString = findPatternFromString(contents, patternToFind);
			System.out.println("ID_STRING found :"+idString);
			return true;
		}

		private String findPatternFromString(String contents,
				Pattern patternToFind) {

			Matcher matcher = patternToFind.matcher(contents);
			String group= null;
			if(matcher.find()){
				group = matcher.group(1);				
			}
			
			return group;
		}

		private int indexOfChar(TextSearchMatchAccess matchRequestor, char charToFind, boolean shouldSearchBackward) {
			int matchOffset = matchRequestor.getMatchOffset();
			while(charToFind != matchRequestor.getFileContentChar(matchOffset)){
				if(shouldSearchBackward){
					matchOffset--;
				}else{
					matchOffset++;
				}
			}
			return matchOffset;
		}

		private static String getContents(TextSearchMatchAccess matchRequestor,
				int start, int end) {
			StringBuffer buf = new StringBuffer();
			for (int i = start; i < end; i++) {
				char ch = matchRequestor.getFileContentChar(i);
				if (Character.isWhitespace(ch) || Character.isISOControl(ch)) {
					buf.append(' ');
				} else {
					buf.append(ch);
				}
			}
			return buf.toString();
		}

		public void beginReporting() {
			// Clear everything
			System.out.println("Begin - Reporting");
		}

		public void endReporting() {
			System.out.println("End - Reporting");
		}

	}
}
