package com.andromeda.cadbane.hyperlinks;

import java.util.ArrayList;
import java.util.regex.Matcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.andromeda.cadbane.search.StringSearcher;
import com.andromeda.utility.logging.WSConsole;

/**
 * HyperlinkDetector to detect string resource in strings.xml or any other xml.
 * 
 * @author tsaravana
 *
 */
public class StringHyperlinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		// Boiler code. Just the same thing over and over.
		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();

		IFile file = ((IFileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput())
				.getFile();

		IRegion lineRegion = null;
		String matchLine = null;

		try {
			lineRegion = document.getLineInformationOfOffset(offset);
			matchLine = document.get(lineRegion.getOffset(), lineRegion.getLength());
		} catch (BadLocationException e) {
			WSConsole.e(e);
		}

		// Matches the String tag
		Matcher matcher = StringHyperlink.patternStringHyperlink.matcher(matchLine);
		IHyperlink[] hyperlinks = getHyperlink(matcher, offset, file, lineRegion, matchLine);

		// Matches the Id tag
		if (hyperlinks == null) {
			matcher = matcher.usePattern(IdHyperlink.patternIdHyperlink);
			hyperlinks = getHyperlink(matcher, offset, file, lineRegion, matchLine);
		}

		return hyperlinks;

	}

	/**
	 * Get the array of hyperlinks to show
	 * 
	 * @param matcher
	 *            the matcher of the regex
	 * @param offset
	 *            the hyperlink region offset
	 * @param file
	 *            the file from which the hyperlink was invoked
	 * @param lineRegion
	 *            the lineRegion
	 * @param matchLine
	 *            the line that got matched
	 * @return the array of hyperlinks if matched, or null
	 */
	private IHyperlink[] getHyperlink(Matcher matcher, int offset, IFile file, IRegion lineRegion, String matchLine) {
		ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
		while (matcher.find()) {
			// Get the match.
			String name = matcher.group(1);

			// Boiler code. Same for all scenarios
			int index = matchLine.indexOf(name);
			IRegion targetRegion = new Region(lineRegion.getOffset() + index, name.length());
			if (targetRegion != null) {
				if ((targetRegion.getOffset() <= offset) && (targetRegion.getOffset() + targetRegion.getLength()) > offset) {
					String matchedRegexString = matcher.pattern().toString();
					WSConsole.d("Hyperlink matched : " + matchedRegexString);
					if (matchedRegexString.equalsIgnoreCase(StringHyperlink.patternStringHyperlink.toString())) {
						// Show two actions when user hovers over the link
						hyperlinks.add(new StringHyperlink(targetRegion, name, file.getProject(), StringSearcher.FIND_ALL_OCCURRENCES));
						hyperlinks.add(new StringHyperlink(targetRegion, name, file.getProject(), StringSearcher.FIND_IN_LAYOUT));
						return hyperlinks.toArray(new IHyperlink[2]);
					} else if (matchedRegexString.equalsIgnoreCase(IdHyperlink.patternIdHyperlink.toString())) {
						hyperlinks.add(new IdHyperlink(targetRegion, matcher.group(2), file.getProject(), file));
						return hyperlinks.toArray(new IHyperlink[1]);
					}
				}
			}
		}
		// Didn't match anything
		return null;
	}

}
