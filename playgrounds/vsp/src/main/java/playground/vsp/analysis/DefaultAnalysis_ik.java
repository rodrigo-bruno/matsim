/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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

package playground.vsp.analysis;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.groups.VspExperimentalConfigGroup;
import org.matsim.core.events.EventsReaderXMLv1;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.handler.EventHandler;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.scenario.ScenarioImpl;
import org.matsim.core.utils.io.UncheckedIOException;

import playground.vsp.analysis.modules.AbstractAnalyisModule;
import playground.vsp.analysis.modules.carDistance.CarDistanceAnalyzer;
import playground.vsp.analysis.modules.emissionsAnalyzer.EmissionsAnalyzer;
import playground.vsp.analysis.modules.emissionsWriter.EmissionEventsWriter;
import playground.vsp.analysis.modules.legModeDistanceDistribution.LegModeDistanceDistribution;
import playground.vsp.analysis.modules.level1.agentId2personEnterLeaveVehicle.AgentId2EnterLeaveVehicleAnalyzer;
import playground.vsp.analysis.modules.level1.delayAtStop.VehDelayAtStopAnalyzer;
import playground.vsp.analysis.modules.level1.delayAtStopHistogram.VehDelayAtStopHistogramAnalyzer;
import playground.vsp.analysis.modules.level1.departureDelayAtStop.DepartureDelayAtStopAnalyzer;
import playground.vsp.analysis.modules.level1.enterLeaveVehicle2Activity.EnterLeaveVehicle2ActivityAnalyzer;
import playground.vsp.analysis.modules.level1.ptTripTravelTime.PtTripTravelTimeTransfersAnalyzer;
import playground.vsp.analysis.modules.level1.stopId2lineId2pulk.StopId2LineId2PulkAnalyzer;
import playground.vsp.analysis.modules.level1.stopId2personEnterLeaveVehicle.StopId2PersonEnterLeaveVehicleAnalyzer;
import playground.vsp.analysis.modules.level1.stopId2personEnterLeaveVehicle.StopId2PersonEnterLeaveVehicleHandler;
import playground.vsp.analysis.modules.level1.stopId2routeId2DelayAtStop.StopId2RouteId2DelayAtStopAnalyzer;
import playground.vsp.analysis.modules.level1.vehicleId2personEnterLeaveVehicle.VehId2PersonEnterLeaveVehicleAnalyzer;
import playground.vsp.analysis.modules.ptOperator.PtOperatorAnalyzer;
import playground.vsp.analysis.modules.transferPayments.MonetaryPaymentsAnalyzer;
import playground.vsp.analysis.modules.travelTime.TravelTimeAnalyzer;
import playground.vsp.analysis.modules.userBenefits.UserBenefitsAnalyzer;
import playground.vsp.analysis.modules.waitingTimes.WaitingTimesAnalyzer;

/**
 * 
 * @author ikaddoura, aneumann
 *
 */
public class DefaultAnalysis_ik {
	
	private final static Logger log = Logger.getLogger(DefaultAnalysis_ik.class);
	
	private final String outputDir;
	private final String eventsFile;
	private final ScenarioImpl scenario;
	
	private final List<AbstractAnalyisModule> anaModules = new LinkedList<AbstractAnalyisModule>();

	public DefaultAnalysis_ik(ScenarioImpl scenario, String iterationOutputDir, String eventsFile) {
		this.outputDir = iterationOutputDir + "defaultAnalysis" + "/";
		this.eventsFile = eventsFile;
		this.scenario = scenario;
	}

	public void init(String aasRunnerConfigFile){
		log.info("This is currently not implemented. Initializing all modules with defaults...");
		
		String ptDriverPrefix = "pt_"; //TODO: replace by analysis module that finds the ptDriverPrefix
		
		// END of configuration file
		
//		LegModeDistanceDistribution distAna = new LegModeDistanceDistribution(ptDriverPrefix);
//		distAna.init(this.scenario);
//		this.anaModules.add(distAna);
//		
//		UserBenefitsAnalyzer userBenefitsAna = new UserBenefitsAnalyzer(ptDriverPrefix);
//		userBenefitsAna.init(this.scenario);
//		this.anaModules.add(userBenefitsAna);
//		
//		MonetaryPaymentsAnalyzer moneyAna = new MonetaryPaymentsAnalyzer(ptDriverPrefix);
//		moneyAna.init(this.scenario);
//		this.anaModules.add(moneyAna);
//		
//		CarDistanceAnalyzer carDistAna = new CarDistanceAnalyzer(ptDriverPrefix);
//		carDistAna.init(scenario);
//		this.anaModules.add(carDistAna);
//		
//		TravelTimeAnalyzer ttAna = new TravelTimeAnalyzer(ptDriverPrefix);
//		ttAna.init(scenario);
//		this.anaModules.add(ttAna);
		
//		EmissionEventsWriter emiWriter = new EmissionEventsWriter(ptDriverPrefix, this.outputDir);
//		// additional files, required for this analysis module:
//		String emissionVehicleFile = "/Users/Ihab/Documents/workspace/shared-svn/projects/detailedEval/pop/merged/emissionVehicles_1pct.xml.gz";
//		String emissionInputPath = "/Users/Ihab/Documents/workspace/shared-svn/projects/detailedEval/emissions/hbefaForMatsim/";
//		String roadTypeMappingFile = emissionInputPath + "roadTypeMapping.txt";
//		String averageFleetWarmEmissionFactorsFile = emissionInputPath + "EFA_HOT_vehcat_2005average.txt";
//		String averageFleetColdEmissionFactorsFile = emissionInputPath + "EFA_ColdStart_vehcat_2005average.txt";
//		boolean isUsingDetailedEmissionCalculation = true;
//		String detailedWarmEmissionFactorsFile = emissionInputPath + "EFA_HOT_SubSegm_2005detailed.txt";
//		String detailedColdEmissionFactorsFile = emissionInputPath + "EFA_ColdStart_SubSegm_2005detailed.txt";
//		VspExperimentalConfigGroup vcg = scenario.getConfig().vspExperimental() ;
//		vcg.setEmissionRoadTypeMappingFile(roadTypeMappingFile);
//		vcg.setEmissionVehicleFile(emissionVehicleFile);
//		vcg.setAverageWarmEmissionFactorsFile(averageFleetWarmEmissionFactorsFile);
//		vcg.setAverageColdEmissionFactorsFile(averageFleetColdEmissionFactorsFile);
//		vcg.setIsUsingDetailedEmissionCalculation(isUsingDetailedEmissionCalculation);
//		vcg.setDetailedWarmEmissionFactorsFile(detailedWarmEmissionFactorsFile);
//		vcg.setDetailedColdEmissionFactorsFile(detailedColdEmissionFactorsFile);
//		emiWriter.init(scenario);
//		this.anaModules.add(emiWriter);
		
//		// emission events file required for this analysis module:
//		String emissionsEventsFile = "/Users/Ihab/Documents/workspace/shared-svn/studies/ihab/test/output/test_fakePt/ITERS/it.0/analysis/EmissionEventsWriter/emission.events.xml";
//		EmissionsAnalyzer emiAna = new EmissionsAnalyzer(ptDriverPrefix, emissionsEventsFile);
//		emiAna.init(scenario);
//		this.anaModules.add(emiAna);
		
//		PtOperatorAnalyzer ptOperatorAna = new PtOperatorAnalyzer(ptDriverPrefix);
//		ptOperatorAna.init(scenario);
//		this.anaModules.add(ptOperatorAna);
//		
//		WaitingTimesAnalyzer waitAna = new WaitingTimesAnalyzer(ptDriverPrefix);
//		waitAna.init(scenario);
//		this.anaModules.add(waitAna);
		
//		DepartureDelayAtStopAnalyzer delayAna = new DepartureDelayAtStopAnalyzer(ptDriverPrefix);
//		delayAna.init(scenario);
//		this.anaModules.add(delayAna);
		
//		AgentId2EnterLeaveVehicleAnalyzer agent2EnterLeaveAna = new AgentId2EnterLeaveVehicleAnalyzer(ptDriverPrefix);
//		agent2EnterLeaveAna.init(scenario);
//		this.anaModules.add(agent2EnterLeaveAna);
		
//		PtTripTravelTimeTransfersAnalyzer ptTTAna = new PtTripTravelTimeTransfersAnalyzer(ptDriverPrefix);
//		ptTTAna.init(scenario);
//		this.anaModules.add(ptTTAna);
		
//		EnterLeaveVehicle2ActivityAnalyzer enterLeaveActAna = new EnterLeaveVehicle2ActivityAnalyzer(ptDriverPrefix);
//		enterLeaveActAna.init(scenario);
//		this.anaModules.add(enterLeaveActAna);
//		
//		StopId2LineId2PulkAnalyzer pulkAna = new StopId2LineId2PulkAnalyzer(ptDriverPrefix);
//		pulkAna.init(scenario);
//		this.anaModules.add(pulkAna);
		
//		StopId2PersonEnterLeaveVehicleAnalyzer stopAna = new StopId2PersonEnterLeaveVehicleAnalyzer(ptDriverPrefix);
//		stopAna.init(scenario);
//		this.anaModules.add(stopAna);
		
//		StopId2RouteId2DelayAtStopAnalyzer delayAna = new StopId2RouteId2DelayAtStopAnalyzer(ptDriverPrefix);
//		delayAna.init(scenario);
//		this.anaModules.add(delayAna);
		
//		// number of detailed slots required for this analysis module
//		int numberOfDetailedSlots = 5;
//		VehDelayAtStopHistogramAnalyzer delayHistogramAna = new VehDelayAtStopHistogramAnalyzer(ptDriverPrefix, numberOfDetailedSlots);
//		delayHistogramAna.init(scenario);
//		this.anaModules.add(delayHistogramAna);

//		VehDelayAtStopAnalyzer vehDelayAna = new VehDelayAtStopAnalyzer(ptDriverPrefix);
//		vehDelayAna.init(scenario);
//		this.anaModules.add(vehDelayAna);
//		
		VehId2PersonEnterLeaveVehicleAnalyzer vehIdEnterLeaveAna = new VehId2PersonEnterLeaveVehicleAnalyzer(ptDriverPrefix);
		vehIdEnterLeaveAna.init(scenario);
		this.anaModules.add(vehIdEnterLeaveAna);
		
		// END ugly code - Initialization needs to be configurable
		
		for (Person person : this.scenario.getPopulation().getPersons().values()) {
			if (person.getId().toString().startsWith(ptDriverPrefix)){
				throw new RuntimeException("Person " + person.getId() + " has the same prefix as the pt Driver: " + ptDriverPrefix + ". Aborting...");
			}
		}
		
		log.info("Initializing all modules with defaults... done.");
	}

	public void preProcess(){
		log.info("Preprocessing all modules...");
		for (AbstractAnalyisModule module : this.anaModules) {
			module.preProcessData();
		}
		log.info("Preprocessing all modules... done.");
	}
	
	public void run(){
		EventsManager eventsManager = EventsUtils.createEventsManager();
		for (AbstractAnalyisModule module : this.anaModules) {
			for (EventHandler handler : module.getEventHandler()) {
				eventsManager.addHandler(handler);
			}
		}
		try {
			log.info("Trying to parse eventsFile " + this.eventsFile);
			EventsReaderXMLv1 reader = new EventsReaderXMLv1(eventsManager);
			reader.parse(this.eventsFile);
		} catch (UncheckedIOException e) {
			log.warn("Failed parsing " + this.eventsFile + ". Skipping events handling...");
		}
	}
	
	public void postProcess(){
		log.info("Postprocessing all modules...");
		for (AbstractAnalyisModule module : this.anaModules) {
			module.postProcessData();
		}
		log.info("Postprocessing all modules... done.");
	}
	
	public void writeResults(){
		log.info("Writing results for all modules...");
		log.info("Generating output directory " + this.outputDir);
		new File(this.outputDir).mkdir();
		
		for (AbstractAnalyisModule module : this.anaModules) {
			String moduleOutputDir = this.outputDir + module.getName() + "/";
			log.info("Writing results of module " + module.getName() + " to " + moduleOutputDir + "...");
			new File(moduleOutputDir).mkdir();
			module.writeResults(moduleOutputDir);
			log.info("Writing results of module " + module.getName() + " to " + moduleOutputDir + "... done.");
		}
		log.info("Writing results for all modules... done.");

		Gbl.printElapsedTime();
		Gbl.printMemoryUsage();
	}
}
