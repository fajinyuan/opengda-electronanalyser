package org.opengda.detector.electronanalyser.client.plot;

import gda.rcp.views.FindableExecutableExtension;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.opengda.detector.electronanalyser.server.VGScientaAnalyser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectrumViewFactory implements FindableExecutableExtension {
	private static final Logger logger=LoggerFactory.getLogger(SpectrumViewFactory.class);
	private String viewPartName;
	private String name;
	private VGScientaAnalyser analyser;
	@Override
	public Object create() throws CoreException {
		logger.info("Creating Spectrum plot view");
		SpectrumView spectrumView=new SpectrumView();
		spectrumView.setViewPartName(viewPartName);
		if (analyser != null) spectrumView.setAnalyser(analyser);
		
		return spectrumView;
	}

	@Override
	public void setName(String name) {
		this.name=name;		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (analyser == null ) {
			throw new IllegalArgumentException("analyser cannot be null in Spectrum View.");
		}
		
	}

	public VGScientaAnalyser getAnalyser() {
		return analyser;
	}

	public void setAnalyser(VGScientaAnalyser analyser) {
		this.analyser = analyser;
	}

	public String getViewPartName() {
		return viewPartName;
	}

	public void setViewPartName(String viewPartName) {
		this.viewPartName = viewPartName;
	}

}
