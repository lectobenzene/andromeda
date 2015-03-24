package com.andromeda.cadbane.hyperlinks;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;

import com.andromeda.cadbane.search.StringSearcher;

/**
 * Hyperlink when string resource is clicked in string.xml or any other xml
 * 
 * @author tsaravana
 *
 */
public class StringHyperlink extends AbstractHyperlink {

	public static final Pattern patternStringHyperlink = Pattern.compile("<string name=\"([^\"]*)\">");
	
	private final IRegion targetRegion;

	/** the string to search */
	private final String stringName;
	private final IProject project;

	/** the type of hyperlink, whether layout or all occurrences */
	private int hyperlinkType;

	public StringHyperlink(IRegion region, String stringName, IProject project, int hyperlinkType) {
		targetRegion = region;
		this.project = project;
		this.stringName = stringName;
		this.hyperlinkType = hyperlinkType;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return targetRegion;
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public String getHyperlinkText() {
		switch (hyperlinkType) {
		case StringSearcher.FIND_ALL_OCCURRENCES:
			return "Find all occurrences";

		case StringSearcher.FIND_IN_LAYOUT:
			return "Find in layout";

		default:
			return null;
		}
	}

	@Override
	public void open() {
		StringSearcher searcher = new StringSearcher(stringName, project, hyperlinkType);
		searcher.search();
	}

}
