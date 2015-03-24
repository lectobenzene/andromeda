package com.andromeda.cadbane.hyperlinks;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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

	/** the content to show as a hyperlink */
	private String stringName;

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		// Boiler code. Just the same thing over and over.
		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();

		// Get the project for the search scope
		IFile file = ((IFileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput())
				.getFile();
		IProject project = file.getProject();

		IRegion lineRegion = null;
		String matchLine = null;

		try {
			lineRegion = document.getLineInformationOfOffset(offset);
			matchLine = document.get(lineRegion.getOffset(), lineRegion.getLength());
		} catch (BadLocationException e) {
			WSConsole.e(e);
		}

		ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();

		// Matches the String tag
		Matcher matcher = StringHyperlink.patternStringHyperlink.matcher(matchLine);

		while (matcher.find()) {
			// Get the match.
			stringName = matcher.group(1);

			// Boiler code. Same for all scenarios
			int index = matchLine.indexOf(stringName);
			IRegion targetRegion = new Region(lineRegion.getOffset() + index, stringName.length());
			if (targetRegion != null) {
				if ((targetRegion.getOffset() <= offset) && (targetRegion.getOffset() + targetRegion.getLength()) > offset) {
					// Show two actions when user hovers over the link
					hyperlinks.add(new StringHyperlink(targetRegion, stringName, project, StringSearcher.FIND_ALL_OCCURRENCES));
					hyperlinks.add(new StringHyperlink(targetRegion, stringName, project, StringSearcher.FIND_IN_LAYOUT));
					return hyperlinks.toArray(new IHyperlink[2]);
				}
			}
		}
		
		// Matches the Id tag
		matcher.usePattern(IdHyperlink.patternIdHyperlink);
		while (matcher.find()) {
			// Get the match.
			stringName = matcher.group(1);

			// Boiler code. Same for all scenarios
			int index = matchLine.indexOf(stringName);
			IRegion targetRegion = new Region(lineRegion.getOffset() + index, stringName.length());
			if (targetRegion != null) {
				if ((targetRegion.getOffset() <= offset) && (targetRegion.getOffset() + targetRegion.getLength()) > offset) {
					hyperlinks.add(new IdHyperlink(targetRegion, matcher.group(2), project, file));
					return hyperlinks.toArray(new IHyperlink[1]);
				}
			}
		}
		
		// Nothing got matched
		return null;

		
	}

}
