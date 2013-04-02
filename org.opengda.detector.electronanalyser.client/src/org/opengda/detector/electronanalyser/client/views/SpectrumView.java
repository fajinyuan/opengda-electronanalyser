package org.opengda.detector.electronanalyser.client.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.opengda.detector.electronanalyser.server.VGScientaAnalyser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectrumView extends ViewPart {

	private static final Logger logger=LoggerFactory .getLogger(SpectrumView.class);
	private VGScientaAnalyser analyser;

	public SpectrumView() {
		setTitleToolTip("live display of integrated spectrum");
		// setContentDescription("A view for displaying integrated spectrum.");
		setPartName("Spectrum");
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout());

		try {
			SpectrumPlotComposite spectrumPlotComposite = new SpectrumPlotComposite(this, rootComposite, SWT.None);
			spectrumPlotComposite.setAnalyser(getAnalyser());
		} catch (Exception e) {
			logger.error("Cannot create spectrum plot composite.", e);
		}
	}

	@Override
	public void setFocus() {

	}

	public VGScientaAnalyser getAnalyser() {
		return analyser;
	}

	public void setAnalyser(VGScientaAnalyser analyser) {
		this.analyser = analyser;
	}

	public void setViewPartName(String viewPartName) {
		setPartName(viewPartName);

	}

}
