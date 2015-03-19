package com.andromeda.cadbane.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;

/**
 * Class that holds methods to run the local headless search
 * 
 * @author tsaravana
 *
 */
public class StringSearchQuery {

	/** the array that holds the results of the local search of id's */
	private final List<String> results;

	/** The scope to search in */
	private FileTextSearchScope scope;

	/** The regEx pattern that has to be searched */
	private Pattern pattern;

	public StringSearchQuery(List<String> results, FileTextSearchScope scope, Pattern pattern) {
		this.results = results;
		this.scope = scope;
		this.pattern = pattern;
	}

	/**
	 * The SearchRequestor class that contains all callbacks, and the one that
	 * actually loads the search results in the provided array
	 * 
	 * @author tsaravana
	 *
	 */
	private final static class StringTextSearchResultCollector extends TextSearchRequestor {

		private static final String STRINGS = "strings.xml";
		private static final Pattern PATTERN_TO_FIND = Pattern.compile("android:id=\"@(?:\\+)?id/([^\"]*)\"");

		/** the array that is manipulated till the search ends */
		private List<String> cachedMatches;

		/**
		 * the actual array that is filled with the contents from the cached
		 * array once search ends
		 */
		private List<String> results;

		private StringTextSearchResultCollector(List<String> results) {
			this.results = results;
		}

		public boolean acceptFile(IFile file) throws CoreException {
			// Omit all the files that shouldn't be checked: strings.xml
			// Search only the .xml files, inside the layout ?? What if there is
			// a reference is other xml like animator or styles
			if (STRINGS.equals(file.getName())) {
				return false;
			}
			return true;
		}

		public boolean acceptPatternMatch(TextSearchMatchAccess matchRequestor) throws CoreException {
			// Find the "id" from the view
			int startIndex = indexOfChar(matchRequestor, '>', true);
			int endIndex = indexOfChar(matchRequestor, '<', false);
			String contents = getContents(matchRequestor, startIndex, endIndex);

			String idString = findPatternFromString(contents, PATTERN_TO_FIND);
			if (idString != null && idString.length() != 0) {
				// Process the idResults to add the R.id substring
				cachedMatches.add(processIdString(idString));
			}
			return true;
		}

		/**
		 * Adds the R.id to the given string.
		 * 
		 * @param idString
		 *            the string
		 * @return returns the ID of the string
		 */
		private String processIdString(String idString) {
			return "R.id." + idString;
		}

		private String findPatternFromString(String contents, Pattern patternToFind) {
			Matcher matcher = patternToFind.matcher(contents);
			String group = null;
			if (matcher.find()) {
				group = matcher.group(1);
			}
			return group;
		}

		private int indexOfChar(TextSearchMatchAccess matchRequestor, char charToFind, boolean shouldSearchBackward) {
			int matchOffset = matchRequestor.getMatchOffset();
			while (charToFind != matchRequestor.getFileContentChar(matchOffset)) {
				if (shouldSearchBackward) {
					matchOffset--;
				} else {
					matchOffset++;
				}
			}
			return matchOffset;
		}

		/**
		 * I didn't write this method. Took from the source code.
		 * 
		 * @param matchRequestor
		 *            the requestor
		 * @param start
		 *            startIndex
		 * @param end
		 *            endIndex
		 * @return the String
		 */
		private static String getContents(TextSearchMatchAccess matchRequestor, int start, int end) {
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
			cachedMatches = new ArrayList<String>();

		}

		public void endReporting() {
			results.addAll(cachedMatches);
			cachedMatches.clear();
			cachedMatches = null;
		}

	}

	/**
	 * Runs the headless search and fills the result in the results array
	 * 
	 * @param monitor
	 *            the progress monitor
	 */
	public void run(IProgressMonitor monitor) {
		// clear the previous results
		results.clear();
		TextSearchRequestor requestor = new StringTextSearchResultCollector(results);
		// The code that does the headless search
		TextSearchEngine.create().search(scope, requestor, pattern, monitor);
	}
}
