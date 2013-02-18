package org.opengda.detector.electronanalyser.client.sequenceeditor;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.opengda.detector.electronanalyser.client.ElectronAnalyserClientPlugin;
import org.opengda.detector.electronanalyser.client.ImageConstants;
import org.opengda.detector.electronanalyser.model.regiondefinition.api.Region;

public class SequenceViewLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableColorProvider {
	private double xRaySourceEnergyLimit = 2100.0; // must be in eV
	private boolean sourceSelectable = false;

	public double getXRaySourceEnergyLimit() {
		return xRaySourceEnergyLimit;
	}

	public void setXRaySourceEnergyLimit(double xRaySourceEnergyLimit) {
		this.xRaySourceEnergyLimit = xRaySourceEnergyLimit;
	}

	public boolean isSourceSelectable() {
		return sourceSelectable;
	}

	public void setSourceSelectable(boolean sourceSelectable) {
		this.sourceSelectable = sourceSelectable;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		if (columnIndex == SequenceTableConstants.COL_STATUS) {
			return ColorConstants.red;
		}
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		if (columnIndex == SequenceTableConstants.COL_STATUS) {
			return ColorConstants.green;
		}
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof Region) {
			Region region = (Region) element;
			if (columnIndex == SequenceTableConstants.COL_ENABLED) {
				if (region.isEnabled()) {
					return ElectronAnalyserClientPlugin.getDefault()
							.getImageRegistry()
							.get(ImageConstants.ICON_CHECKED_STATE);
				} else {
					return ElectronAnalyserClientPlugin.getDefault()
							.getImageRegistry()
							.get(ImageConstants.ICON_UNCHECKED_STATE);
				}
			}
			else if (columnIndex == SequenceTableConstants.COL_STATUS) {
				if (region.isEnabled()) {
					return ElectronAnalyserClientPlugin.getDefault()
							.getImageRegistry()
							.get(ImageConstants.ICON_RUN_READY);
				} 
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof Region) {
			Region region = (Region) element;
			switch (columnIndex) {
			case SequenceTableConstants.COL_STATUS:
				return "";

			case SequenceTableConstants.COL_ENABLED:
				return "";
			case SequenceTableConstants.COL_REGION_NAME:
				return region.getName();
			case SequenceTableConstants.COL_LENS_MODE:
				return region.getLensMode().getLiteral();
			case SequenceTableConstants.COL_PASS_ENERGY:
				return region.getPassEnergy().getLiteral();
			case SequenceTableConstants.COL_X_RAY_SOURCE:
				if (isSourceSelectable()) {
					if (region.getExcitationEnergy() < xRaySourceEnergyLimit)
						return "Soft";
					return "Hard";
				}
				return Double.toString(region.getExcitationEnergy());
			case SequenceTableConstants.COL_ENERGY_MODE:
				return region.getEnergyMode().getLiteral();
			case SequenceTableConstants.COL_LOW_ENERGY:
				return Double.toString(region.getLowEnergy());
			case SequenceTableConstants.COL_HIGH_ENERGY:
				return Double.toString(region.getHighEnergy());
			case SequenceTableConstants.COL_ENERGY_STEP:
				return Double.toString(region.getEnergyStep());
			case SequenceTableConstants.COL_STEP_TIME:
				// FIXME - getSetpTime()
				return Double.toString(region.getSetpTime());
			case SequenceTableConstants.COL_STEPS:
				// FIXME
				return Integer.toString(1);
			case SequenceTableConstants.COL_TOTAL_TIME:
				// FIXME
				return Double.toString(100.00);
			case SequenceTableConstants.COL_X_CHANNEL_FROM:
				return Integer.toString(region.getFirstXChannel());
			case SequenceTableConstants.COL_X_CHANNEL_TO:
				return Integer.toString(region.getLastXChannel());
			case SequenceTableConstants.COL_Y_CHANNEL_FROM:
				return Integer.toString(region.getFirstYChannel());
			case SequenceTableConstants.COL_Y_CHANNEL_TO:
				return Integer.toString(region.getLastYChannel());
			case SequenceTableConstants.COL_SLICES:
				return Integer.toString(region.getSlices());
			case SequenceTableConstants.COL_MODE:
				return region.getDetectorMode().getLiteral();

			}
		}
		return null;
	}
}