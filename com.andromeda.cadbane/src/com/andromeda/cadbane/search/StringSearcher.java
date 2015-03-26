package com.andromeda.cadbane.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchEvent;
import org.eclipse.search.ui.text.TextSearchQueryProvider;
import org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput;

import com.andromeda.utility.logging.WSConsole;
import com.andromeda.utility.utils.UtilResource;

/**
 * Holds methods to do a search. Currently supports only string resource. Should
 * make this more generic to support all sorts of resources
 * 
 * @author tsaravana
 *
 */
public class StringSearcher implements ISearchResultListener {

	public static final int REMOVE_MATCH_INITIALIZE = 0;
	public static final int REMOVE_MATCH_NOT_FOUND = 1;
	public static final int REMOVE_MATCH_FOUND = 2;

	/** the array that holds the results of the local search of id's */
	private Map<String, List<String>> results;

	/** current project that is used for getting the scope */
	private IProject project;

	/** the string to search */
	private String stringToSearch;

	/** the type of search, all occurrences or just in layout */
	private int searchType;

	public static final String R_ID = "R.id.";
	public static final String R_STRING = "R.string.";
	public static final String R_LAYOUT = "R.layout.";

	/** Used to match exact word. Ideal scenario will be to use a LOOK_BEHIND, but this works too.*/
	public static final String REGEX_WORD_END = "[^_\\w\\d]";
	
	/** Searches in both layout and java. Searches everywhere */
	public static final int FIND_ALL_OCCURRENCES = 0;
	/** Searches only in the layout */
	public static final int FIND_IN_LAYOUT = 1;

	public StringSearcher(String stringToSearch, IProject project, int searchType) {
		this.stringToSearch = stringToSearch;
		this.project = project;
		this.searchType = searchType;
	}

	/**
	 * Performs the internal search if searchType is All Occurrences, and then
	 * does a UI search.
	 */
	public void search() {

		// clear the result object when starting a search
		flushResults();

		FileTextSearchScope scope;

		// Don't perform the internal search if search type is Layout only.
		if (searchType == StringSearcher.FIND_ALL_OCCURRENCES) {
			WSConsole.d("searchType = FIND_ALL_OCCURRENCES");
			WSConsole.d("stringToSearch = " + stringToSearch);
			// Scope to search the id from string in layout
			scope = FileTextSearchScope.newSearchScope(new IResource[] { project }, new String[] { "*.xml" }, false);
			StringSearchQuery query = new StringSearchQuery(getResults(), scope, Pattern.compile(stringToSearch));
			query.run(null);
		}

		// Scope to search every java and xml file
		scope = FileTextSearchScope.newSearchScope(new IResource[] { project }, new String[] { "*.xml", "*.java" }, false);

		String expressionToSearch = getExpressionToSearch();
		WSConsole.d("expressionToSearch = " + expressionToSearch);

		runUISearch(scope, expressionToSearch);
	}

	public void search(String id, String layout) {

		// clear the result object when starting a search
		flushResults();

		WSConsole.d("search id = " + id);
		WSConsole.d("search layout = " + layout);

		// Scope to search every java and xml file
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(new IResource[] { project }, new String[] { "*.java" }, false);

		// Construct the result object for the searchResult listener
		ArrayList<String> list = new ArrayList<String>();
		list.add(layout);
		getResults().put(id, list);

		runUISearch(scope, escapeRegex(id)+REGEX_WORD_END);
	}

	/**
	 * Constructs the regEx expression that has to be searched finally using the
	 * default UI functionality
	 * 
	 * @return the regEx expression that has to be searched
	 */
	private String getExpressionToSearch() {
		StringBuilder builder = new StringBuilder();

		// TODO Should refactor this code to make it more efficient.
		if (searchType == FIND_IN_LAYOUT) {
			// Only the layouts
			builder.append("(").append("@string/").append(stringToSearch).append(REGEX_WORD_END).append(")");
		} else if (searchType == FIND_ALL_OCCURRENCES) {
			builder.append("(").append("@string/").append(stringToSearch).append(REGEX_WORD_END).append(")");
			// Add the Java version also
			builder.append("|").append("(").append(escapeRegex(R_STRING)).append(stringToSearch).append(REGEX_WORD_END).append(")");
			// Add the ID's also to the search bucket
			for (String id : getResults().keySet()) {
				builder.append("|").append("(").append(escapeRegex(id)).append(REGEX_WORD_END).append(")");
			}
		}

		return builder.toString();
	}

	/**
	 * Escapes all the regex operators in the string
	 * 
	 * @param rString
	 *            the string to change
	 * @return modified string
	 */
	private String escapeRegex(String rString) {
		return rString.replaceAll("\\.", "\\\\.");
	}

	/**
	 * Searches for the regEx expression in the scope provided using the default
	 * Eclipse File search (UI search)
	 * 
	 * @param scope
	 *            The scope of search
	 * @param expressionToSearch
	 *            the regEx expression that has to be searched
	 */
	private void runUISearch(final FileTextSearchScope scope, final String expressionToSearch) {
		WSConsole.d("UI Search is running");
		WSConsole.d("expressionToSearch = " + expressionToSearch);
		// Run the UI search query
		try {
			TextSearchQueryProvider preferred = TextSearchQueryProvider.getPreferred();
			TextSearchInput input = new TextSearchInput() {

				@Override
				public boolean isRegExSearch() {
					return true;
				}

				@Override
				public boolean isCaseSensitiveSearch() {
					return false;
				}

				@Override
				public String getSearchText() {
					return expressionToSearch;
				}

				@Override
				public FileTextSearchScope getScope() {
					return scope;
				}
			};

			ISearchQuery createQuery = preferred.createQuery(input);

			// Add listener for detect change in the search result
			createQuery.getSearchResult().addListener(this);
			// Run the search
			NewSearchUI.runQueryInBackground(createQuery);

		} catch (IllegalArgumentException e) {
			WSConsole.e(e);
		} catch (CoreException e) {
			WSConsole.e(e);
		}
	}

	/**
	 * To obtain the results array, both to send as input and to fetch
	 * information back
	 * 
	 * @return the results array
	 */
	public Map<String, List<String>> getResults() {
		if (results == null) {
			WSConsole.d("Result object created");
			results = new HashMap<String, List<String>>();
		}
		return results;
	}

	public void flushResults() {
		WSConsole.d("Results are flused");
		if (results != null) {
			results.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.search.ui.ISearchResultListener#searchResultChanged(org.eclipse
	 * .search.ui.SearchResultEvent)
	 * 
	 * This method gets called each time the search result changes. So do the
	 * filtration here. Not sure if this is the optimal way to do this, so keep
	 * exploring for a better way.
	 */
	@Override
	public void searchResultChanged(SearchResultEvent e) {

		// Get a reference to the search result so we can remove the matches
		AbstractTextSearchResult result = null;
		ISearchResult searchResult = e.getSearchResult();
		if (searchResult instanceof AbstractTextSearchResult) {
			result = ((AbstractTextSearchResult) searchResult);
		}

		if (e instanceof MatchEvent) {
			// Get the matches
			Match[] matches = ((MatchEvent) e).getMatches();
			for (Match match : matches) {

				// Get the file
				IFile file = null;
				Object element = match.getElement();
				if (element instanceof IFile) {
					file = ((IFile) element);
				} else {
					return;
				}

				// Get the matched Line
				String lineElement = getLineElement(match);
				// Should not occur. Something's wrong
				if (lineElement == null) {
					WSConsole.e("lineElement should not be null");
					WSConsole.e("Match Offset = " + match.getOffset());
					WSConsole.e("Match Length = " + match.getLength());
					WSConsole.e("Match FilePath = " + file.getFullPath());
					return;
				}

				// If the matchedLine is a R.id type, then check if the fileName
				// is contained in the file as a R.layout also.
				Map<String, List<String>> resultMap = getResults();
				List<String> layoutStrings = resultMap.get(lineElement);
				int shouldRemoveMatch = REMOVE_MATCH_INITIALIZE;
				if (layoutStrings != null) {
					shouldRemoveMatch = REMOVE_MATCH_NOT_FOUND;
					for (String layoutString : layoutStrings) {
						if (UtilResource.searchFile(layoutString, file.getLocation().toFile())) {
							shouldRemoveMatch = REMOVE_MATCH_FOUND;
						}
					}
				}
				if (shouldRemoveMatch == REMOVE_MATCH_NOT_FOUND) {
					WSConsole.d("Match is removed");
					result.removeMatch(match);
				}
			}
		}
	}

	/**
	 * Get the line that is matched from the match object
	 * 
	 * @param match
	 *            the match from the search result
	 * @return The matched line
	 */
	private String getLineElement(Match match) {
		String lineContent = null;

		int length = match.getLength();
		Object element = match.getElement();
		if (element instanceof IFile) {
			IPath location = ((IFile) element).getLocation();

			File file = null;
			if (location != null) {
				file = location.toFile();
			}

			if (file != null) {
				RandomAccessFile rFile = null;
				try {
					rFile = new RandomAccessFile(file, "r");
					rFile.seek(match.getOffset());
					byte[] bytes = new byte[length];
					rFile.read(bytes, 0, length);
					lineContent = new String(bytes);
				} catch (FileNotFoundException e) {
					WSConsole.e(e);
				} catch (IOException e) {
					WSConsole.e(e);
				} finally {
					if (rFile != null) {
						try {
							rFile.close();
						} catch (IOException e) {
							WSConsole.e(e);
						}
					}
				}

			}
		}
		return lineContent;
	}
}
