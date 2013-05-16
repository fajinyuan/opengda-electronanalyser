package org.opengda.detector.electronanalyser.scan;

import gda.data.PathConstructor;
import gda.scan.ScanPositionProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opengda.detector.electronanalyser.model.regiondefinition.api.Region;
import org.opengda.detector.electronanalyser.utils.OsUtil;
import org.opengda.detector.electronanalyser.utils.RegionDefinitionResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionPositionProvider implements ScanPositionProvider {
	List<Region> points=new ArrayList<Region>();
	RegionDefinitionResourceUtil regionResourceutil;
	private static final Logger logger=LoggerFactory.getLogger(RegionPositionProvider.class);


	public RegionPositionProvider(String filename) {

		String xmldir;
		if ((OsUtil.isUnix() && !filename.startsWith("/")) || (OsUtil.isWindows() && !filename.matches("^[a-zA-Z]:\\*"))) {
			xmldir = PathConstructor.createFromDefaultProperty()+File.pathSeparator+"xml";
			filename=xmldir+File.pathSeparator+filename;
		}
		try {
			this.points=regionResourceutil.getRegions(filename);
		} catch (Exception e) {
			logger.error("");
		}
	}
	@Override
	public Object get(int index) {
		return points.get(index);
	}
	
	@Override
	public int size() {
		return points.size();
	}
}
