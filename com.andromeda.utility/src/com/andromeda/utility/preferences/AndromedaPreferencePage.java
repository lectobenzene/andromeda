package com.andromeda.utility.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wb.swt.SWTResourceManager;

import com.andromeda.utility.Activator;
import com.andromeda.utility.logging.WSConsole;

/**
 * The preference page that is the home page and the one that hold the error
 * logging switches.
 * 
 * @author tsaravana
 *
 */
public class AndromedaPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private static final String PREFERENCE_DESCRIPTION = "Set the global preferences.";
	private static final String PREFERENCE_TITLE = "Andromeda Preferences";
	private Group grpDebugger;
	private Button btnDebug;
	private Button btnError;
	private Button btnInfo;

	public AndromedaPreferencePage() {
		setTitle(PREFERENCE_TITLE);
		setDescription(PREFERENCE_DESCRIPTION);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite cmpRoot = new Composite(parent, SWT.NONE);
		GridLayout gl_cmpRoot = new GridLayout(1, false);
		gl_cmpRoot.marginWidth = 0;
		cmpRoot.setLayout(gl_cmpRoot);

		grpDebugger = new Group(cmpRoot, SWT.NONE);
		grpDebugger.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		GridLayout gl_grpDebugger = new GridLayout(1, false);
		gl_grpDebugger.marginHeight = 7;
		gl_grpDebugger.marginWidth = 15;
		grpDebugger.setLayout(gl_grpDebugger);
		grpDebugger.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpDebugger.setText("Debugger");

		btnDebug = new Button(grpDebugger, SWT.CHECK);
		btnDebug.setToolTipText("Enable Debug logs for reporting");
		btnDebug.setText("DEBUG");

		btnError = new Button(grpDebugger, SWT.CHECK);
		btnError.setToolTipText("Enable Error logs for reporting");
		btnError.setText("ERROR");

		btnInfo = new Button(grpDebugger, SWT.CHECK);
		btnInfo.setToolTipText("Enable Info logs for reporting");
		btnInfo.setText("INFO");

		// Initializes the fields with data from the preference
		initialize();
		return cmpRoot;
	}

	@Override
	public boolean performOk() {
		applyPreferences();
		return super.performOk();
	}

	@Override
	protected void performApply() {
		applyPreferences();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		restoreDefaults();
	}

	/**
	 * Apply the preferences. All newly added preferences should go here
	 */
	private void applyPreferences() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setValue(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_DEBUG, btnDebug.getSelection());
		preferenceStore.setValue(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_INFO, btnInfo.getSelection());
		preferenceStore.setValue(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_ERROR, btnError.getSelection());

		/*
		 * Sets the preferences to the static fields. This is to improve
		 * performance. Instead of checking from the preference every time, the
		 * preference is checked only when it is modified.
		 */
		WSConsole.setLoggingPreferences();
	}

	/**
	 * Restores the default state for the preferences
	 */
	private void restoreDefaults() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		btnDebug.setSelection(preferenceStore.getDefaultBoolean(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_DEBUG));
		btnInfo.setSelection(preferenceStore.getDefaultBoolean(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_INFO));
		btnError.setSelection(preferenceStore.getDefaultBoolean(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_ERROR));

	}

	/**
	 * Initialize the states of preferences when the preferences is opened
	 */
	private void initialize() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		btnDebug.setSelection(preferenceStore.getBoolean(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_DEBUG));
		btnInfo.setSelection(preferenceStore.getBoolean(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_INFO));
		btnError.setSelection(preferenceStore.getBoolean(AndromedaPreferenceConstants.PREF_BOOLEAN_LOGGING_ERROR));

	}

}
