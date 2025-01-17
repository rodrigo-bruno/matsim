/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.contrib.taxi.run;

import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

public class RunTaxiScenarioTestIT {
	@Rule
	public final MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void testRunMielecLowDemandLowSupply() {
		runMielec("plans_taxi_1.0.xml.gz", "taxis-25.xml");
	}

	@Test
	public void testRunMielecHighDemandLowSupply() {
		runMielec("plans_taxi_4.0.xml.gz", "taxis-25.xml");
	}

	private void runMielec(String plansFile, String taxisFile) {
		URL configUrl = IOUtils.newUrl(ExamplesUtils.getTestScenarioURL("mielec"), "mielec_taxi_config.xml");
		TaxiConfigGroup taxiCfg = new TaxiConfigGroup();
		Config config = ConfigUtils.loadConfig(configUrl, taxiCfg, new DvrpConfigGroup(), new OTFVisConfigGroup());
		config.plans().setInputFile(plansFile);
		taxiCfg.setTaxisFile(taxisFile);
		config.controler().setOutputDirectory(utils.getOutputDirectory());
		config.controler().setDumpDataAtEnd(false);
		TaxiControlerCreator.createControlerWithSingleModeDrt(config, false).run();
	}
}
