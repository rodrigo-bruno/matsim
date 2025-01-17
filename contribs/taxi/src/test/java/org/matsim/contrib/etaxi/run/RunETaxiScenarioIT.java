/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2018 by the members listed in the COPYING,        *
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

package org.matsim.contrib.etaxi.run;

import java.net.URL;

import org.junit.Test;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;

/**
 * @author michalm
 */
public class RunETaxiScenarioIT {
	@Test
	public void testOneTaxi() {
		URL configUrl = IOUtils.newUrl(ExamplesUtils.getTestScenarioURL("dvrp-grid"), "one_etaxi_config.xml");
		RunETaxiScenario.run(configUrl, false);
	}

	@Test
	public void testRuleBased() {
		URL configUrl = IOUtils.newUrl(ExamplesUtils.getTestScenarioURL("mielec"), "mielec_etaxi_config.xml");
		RunETaxiScenario.run(configUrl, false);
	}

	@Test
	public void testAssignment() {
		URL configUrl = IOUtils.newUrl(ExamplesUtils.getTestScenarioURL("mielec"),
				"mielec_etaxi_config_assignment.xml");
		RunETaxiScenario.run(configUrl, false);
	}
}
