package com.andromeda.utility.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * The preference initializer
 * 
 * @author tsaravana
 *
 */
public class AndromedaPreferenceInitializer extends AbstractPreferenceInitializer {

	public AndromedaPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		// Nothing to initialize so far. By default all the log switches are
		// turned off, so as not to spoil the performance
	}

}
