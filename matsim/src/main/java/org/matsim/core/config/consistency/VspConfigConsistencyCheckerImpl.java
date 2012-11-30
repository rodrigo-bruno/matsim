/* *********************************************************************** *
 * project: matsim
 * VspConfigConsistencyCheckerImpl.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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

package org.matsim.core.config.consistency;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.VspExperimentalConfigGroup;
import org.matsim.core.config.groups.ControlerConfigGroup.EventsFileFormat;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.config.groups.VspExperimentalConfigGroup.VspExperimentalConfigKey;

/**
 * @author nagel
 *
 */
public class VspConfigConsistencyCheckerImpl implements ConfigConsistencyChecker {
	private static Logger log = Logger.getLogger(VspConfigConsistencyCheckerImpl.class) ;
	
	public VspConfigConsistencyCheckerImpl() {
		// empty.  only here to find out where it is called.
	}

	@Override
	public void checkConsistency(Config config) {
		boolean problem = false ;

		if ( config.planCalcScore().getMonetaryDistanceCostRatePt() > 0 ) {
			problem = true ;
			log.warn("found monetary distance cost rate pt > 0.  You probably want a value < 0 here.  " +
					"This is a bug and may be changed eventually.  kai, jun'11") ;
		}
		
		if ( config.getQSimConfigGroup()!=null && config.getQSimConfigGroup().isRemoveStuckVehicles() ) {
			problem = true ;
			log.warn("found that the qsim is removing stuck vehicles.  vsp default is setting this to false.");
		}
		
		boolean found = false ;
		Collection<StrategySettings> settingsColl = config.strategy().getStrategySettings();
		for ( StrategySettings settings : settingsColl ) {
			if ( settings.getModuleName().equalsIgnoreCase("ChangeExpBeta") ) {
				found = true ;
			}
		}
		if ( !found ) {
			problem = true ;
			log.warn("found no strategy ChangeExpBeta. vsp default is using ChangeExpBeta.");
		}
		
		Set<EventsFileFormat> formats = config.controler().getEventsFileFormats();
		if ( !formats.contains(EventsFileFormat.xml) ) {
			problem = true ;
			log.warn("did not find xml as one of the events file formats. vsp default is using xml events.");
		}
		
		// added before nov'12
		if ( config.timeAllocationMutator().getMutationRange() < 7200 ) {
//			problem = true ;
			log.warn("timeAllocationMutator mutationRange < 7200; vsp default is 7200.  This will be more strictly" +
					" enforced in the future. This means you have to add the following lines to your config file: ") ;
			log.warn("<module name=\"TimeAllocationMutator\">");
			log.warn("	<param name=\"mutationRange\" value=\"7200.0\" />");
			log.warn("</module>");
		}
		
		// added before nov'12
		if ( !config.vspExperimental().isRemovingUnneccessaryPlanAttributes() ) {
//			problem = true ;
			log.warn("You are not removing unnecessary plan attributes; vsp default is to do that.  This will be more strictly" +
					" enforced in the future.") ;
		}
		
		// added before nov'12
		if (VspExperimentalConfigGroup.ActivityDurationInterpretation.endTimeOnly
				.compareTo(config.vspExperimental().getActivityDurationInterpretation()) == 0 
				&& config.scenario().isUseTransit()) {
			problem = true;
			log.error("You are using " + config.vspExperimental().getActivityDurationInterpretation() + " as activityDurationInterpretation in " +
			"conjunction with the matsim transit module. This is not working at all as pt interaction activities never have an end time and " +
			"thus will never end!");
		}
		
		// pseudo-pt über Distanz, nicht ptSpeedFactor
		// todo
		
		// use beta_brain=1 // added as of nov'12
		if ( config.planCalcScore().getBrainExpBeta() != 1. ) {
//			problem = true ;
			log.warn("You are using a brainExpBeta != 1; vsp default is 1.  (Different values may cause conceptual " +
					"problems during paper writing.) This will be more strictly "
					+ " enforced in the future. This means you have to add the following lines to your config file: ") ;
			log.warn("<module name=\"planCalcScore\">");
			log.warn("	<param name=\"BrainExpBeta\" value=\"1.0\" />");
			log.warn("</module>");
		}
		
		if ( problem && config.vspExperimental().getValue(VspExperimentalConfigKey.vspDefaultsCheckingLevel)
				.equals( VspExperimentalConfigGroup.ABORT ) ) {
			String str = "found a situation that leads to vsp-abort.  aborting ..." ; 
			log.fatal( str ) ; 
			throw new RuntimeException( str ) ;
		}
		
	}

}
