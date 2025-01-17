/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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

package org.matsim.contrib.drt.run;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.drt.optimizer.insertion.ParallelPathDataProvider;
import org.matsim.contrib.drt.optimizer.rebalancing.mincostflow.MinCostFlowRebalancingParams;
import org.matsim.contrib.dvrp.router.DvrpRoutingNetworkProvider;
import org.matsim.contrib.dvrp.run.Modal;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ReflectiveConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.utils.misc.Time;

public final class DrtConfigGroup extends ReflectiveConfigGroup implements Modal {
	private static final Logger log = Logger.getLogger(DrtConfigGroup.class);

	public static final String GROUP_NAME = "drt";

	@SuppressWarnings("deprecation")
	public static DrtConfigGroup get(Config config) {
		return (DrtConfigGroup)config.getModule(GROUP_NAME);
	}

	public static final String MODE = "mode";
	static final String MODE_EXP = "Mode which will be handled by PassengerEngine and VrpOptimizer "
			+ "(passengers'/customers' perspective)";

	public static final String USE_MODE_FILTERED_SUBNETWORK = "useModeFilteredSubnetwork";
	static final String USE_MODE_FILTERED_SUBNETWORK_EXP =
			"Limit the operation of vehicles to links (of the 'dvrp_routing'"
					+ " network) with 'allowedModes' containing this 'mode'."
					+ " For backward compatibility, the value is set to false by default"
					+ " - this means that the vehicles are allowed to operate on all links of the 'dvrp_routing' network."
					+ " The 'dvrp_routing' is defined by DvrpConfigGroup.networkModes)";

	public static final String STOP_DURATION = "stopDuration";
	static final String STOP_DURATION_EXP = "Bus stop duration. Must be positive.";

	public static final String MAX_WAIT_TIME = "maxWaitTime";
	static final String MAX_WAIT_TIME_EXP = "Max wait time for the bus to come (optimisation constraint).";

	public static final String MAX_TRAVEL_TIME_ALPHA = "maxTravelTimeAlpha";
	static final String MAX_TRAV_ALPHA_EXP =
			"Defines the slope of the maxTravelTime estimation function (optimisation constraint), i.e. "
					+ "maxTravelTimeAlpha * estimated_drt_travel_time + maxTravelTimeBeta. "
					+ "Alpha should not be smaller than 1.";

	public static final String MAX_TRAVEL_TIME_BETA = "maxTravelTimeBeta";
	static final String MAX_TRAVEL_TIME_BETA_EXP =
			"Defines the shift of the maxTravelTime estimation function (optimisation constraint), i.e. "
					+ "maxTravelTimeAlpha * estimated_drt_travel_time + maxTravelTimeBeta. "
					+ "Beta should not be smaller than 0.";

	public static final String REQUEST_REJECTION = "requestRejection";
	static final String REQUEST_REJECTION_EXP = "If true, the max travel and wait times of a submitted request"
			+ " are considered hard constraints (the request gets rejected if one of the constraints is violated)."
			+ " If false, the max travel and wait times are considered soft constraints (insertion of a request that"
			+ " violates one of the constraints is allowed, but its cost is increased by additional penalty to make"
			+ " it relatively less attractive). Penalisation of insertions can be customised by injecting a customised"
			+ " InsertionCostCalculator.PenaltyCalculator";

	public static final String CHANGE_START_LINK_TO_LAST_LINK_IN_SCHEDULE = "changeStartLinkToLastLinkInSchedule";
	static final String CHANGE_START_EXP =
			"If true, the startLink is changed to last link in the current schedule, so the taxi starts the next "
					+ "day at the link where it stopped operating the day before. False by default.";

	public static final String IDLE_VEHICLES_RETURN_TO_DEPOTS = "idleVehiclesReturnToDepots";
	static final String IDLE_VEHICLES_RETURN_TO_DEPOTS_EXP = "Idle vehicles return to the nearest of all start links. See: DvrpVehicle.getStartLink()";

	public static final String OPERATIONAL_SCHEME = "operationalScheme";
	static final String OP_SCHEME_EXP = "Operational Scheme, either door2door or stopbased. door2door by default";

	public static final String MAX_WALK_DISTANCE = "maxWalkDistance";
	static final String MAX_WALK_EXP = "Maximum desired walk distance (in meters) to next stop location in stopbased system. If no suitable stop is found in that range, the search radius will be extended in steps of maxWalkDistance until a stop is found.";

	public static final String ESTIMATED_DRT_SPEED = "estimatedDrtSpeed";
	static final String ESTIMATED_DRT_SPEED_EXP =
			"Beeline-speed estimate for DRT. Used in analysis, optimisation constraints "
					+ "and in plans file, [m/s]. The default value is 25 km/h";

	public static final String ESTIMATED_BEELINE_DISTANCE_FACTOR = "estimatedBeelineDistanceFactor";
	static final String ESTIMATED_BEELINE_DISTANCE_FACTOR_EXP = "Beeline distance factor for DRT. Used in analyis and in plans file. The default value is 1.3.";

	public static final String VEHICLES_FILE = "vehiclesFile";
	static final String VEH_FILE_EXP = "An XML file specifying the vehicle fleet. The file format according to dvrp_vehicles_v1.dtd";

	public static final String TRANSIT_STOP_FILE = "transitStopFile";
	static final String TRANSIT_STOP_FILE_EXP =
			"Stop locations file (transit schedule format, but without lines) for DRT stops. "
					+ "Used only for the stopbased mode";

	public static final String PLOT_CUST_STATS = "writeDetailedCustomerStats";
	static final String PLOT_CUST_STATS_EXP = "Writes out detailed DRT customer stats in each iteration. True by default.";

	public static final String PRINT_WARNINGS = "plotDetailedWarnings";
	static final String PRINT_WARNINGS_EXP = "Prints detailed warnings for DRT customers that cannot be served or routed. Default is true.";

	public static final String NUMBER_OF_THREADS = "numberOfThreads";
	static final String NUMBER_OF_THREADS_EXP =
			"Number of threads used for parallel evaluation of request insertion into existing schedules."
					+ " Scales well up to 4, due to path data provision, the most computationally intensive part,"
					+ " using up to 4 threads. Default value is 'min(4, no. of cores available to JVM)'";

	@NotBlank
	private String mode = TransportMode.drt; // travel mode (passengers'/customers' perspective)

	private boolean useModeFilteredSubnetwork = false;

	@Positive
	private double stopDuration = Double.NaN;// seconds

	@PositiveOrZero
	private double maxWaitTime = Double.NaN;// seconds

	// max arrival time defined as:
	// maxTravelTimeAlpha * unshared_ride_travel_time(fromLink, toLink) + maxTravelTimeBeta,
	// where unshared_ride_travel_time(fromLink, toLink) is calculated with FastAStarEuclidean
	// (hence AStarEuclideanOverdoFactor needs to be specified)
	@DecimalMin("1.0")
	private double maxTravelTimeAlpha = Double.NaN;// [-]

	@PositiveOrZero
	private double maxTravelTimeBeta = Double.NaN;// [s]

	private boolean requestRejection = true;

	private boolean changeStartLinkToLastLinkInSchedule = false;

	private boolean idleVehiclesReturnToDepots = false;

	@NotNull
	private OperationalScheme operationalScheme = OperationalScheme.door2door;

	@PositiveOrZero // used only for stopbased DRT scheme
	private double maxWalkDistance = 0;// [m];

	@PositiveOrZero
	private double estimatedDrtSpeed = 25. / 3.6;// [m/s]

	@DecimalMin("1.0")
	private double estimatedBeelineDistanceFactor = 1.3;// [-]

	@NotNull
	private String vehiclesFile = null;

	@Nullable
	private String transitStopFile = null; // only for stopbased DRT scheme

	private boolean plotDetailedCustomerStats = true;
	private boolean printDetailedWarnings = true;

	@Positive
	private int numberOfThreads = Math.min(Runtime.getRuntime().availableProcessors(),
			ParallelPathDataProvider.MAX_THREADS);

	public enum OperationalScheme {
		stopbased, door2door
	}

	public DrtConfigGroup() {
		super(GROUP_NAME);
	}

	@Override
	protected void checkConsistency(Config config) {
		super.checkConsistency(config);

		if (Time.isUndefinedTime(config.qsim().getEndTime())
				&& config.qsim().getSimEndtimeInterpretation()
				!= QSimConfigGroup.EndtimeInterpretation.onlyUseEndtime) {
			// Not an issue if all request rejections are immediate (i.e. happen during request submission)
			log.warn("qsim.endTime should be specified and qsim.simEndtimeInterpretation should be 'onlyUseEndtime'"
					+ " if postponed request rejection is allowed. Otherwise, rejected passengers"
					+ " (who are stuck endlessly waiting for a DRT vehicle) will prevent QSim from stopping."
					+ " Keep also in mind that not setting an end time may result in agents "
					+ "attempting to travel without vehicles being available.");
		}
		if (config.qsim().getNumberOfThreads() != 1) {
			throw new RuntimeException("Only a single-threaded QSim allowed");
		}
		if (getMaxWaitTime() < getStopDuration()) {
			throw new RuntimeException(
					DrtConfigGroup.MAX_WAIT_TIME + " must not be smaller than " + DrtConfigGroup.STOP_DURATION);
		}
		if (getOperationalScheme() == OperationalScheme.stopbased && getTransitStopFile() == null) {
			throw new RuntimeException(DrtConfigGroup.TRANSIT_STOP_FILE
					+ " must not be null when "
					+ DrtConfigGroup.OPERATIONAL_SCHEME
					+ " is "
					+ DrtConfigGroup.OperationalScheme.stopbased);
		}
		if (getNumberOfThreads() > Runtime.getRuntime().availableProcessors()) {
			throw new RuntimeException(
					DrtConfigGroup.NUMBER_OF_THREADS + " is higher than the number of logical cores available to JVM");
		}
		if (config.global().getNumberOfThreads() < getNumberOfThreads()) {
			log.warn("Consider increasing global.numberOfThreads to at least the value of drt.numberOfThreads"
					+ " in order to speed up the DRT route update during the replanning phase.");
		}
		if (getParameterSets(MinCostFlowRebalancingParams.SET_NAME).size() > 1) {
			throw new RuntimeException("More then one rebalancing parameter sets is specified");
		}

		if (useModeFilteredSubnetwork) {
			DvrpRoutingNetworkProvider.
					checkUseModeFilteredSubnetworkAllowed(config, mode);
		}
	}

	@Override
	public Map<String, String> getComments() {
		Map<String, String> map = super.getComments();
		map.put(MODE, MODE_EXP);
		map.put(USE_MODE_FILTERED_SUBNETWORK, USE_MODE_FILTERED_SUBNETWORK_EXP);
		map.put(STOP_DURATION, STOP_DURATION_EXP);
		map.put(MAX_WAIT_TIME, MAX_WAIT_TIME_EXP);
		map.put(MAX_TRAVEL_TIME_ALPHA, MAX_TRAV_ALPHA_EXP);
		map.put(MAX_TRAVEL_TIME_BETA, MAX_TRAVEL_TIME_BETA_EXP);
		map.put(CHANGE_START_LINK_TO_LAST_LINK_IN_SCHEDULE, CHANGE_START_EXP);
		map.put(VEHICLES_FILE, VEH_FILE_EXP);
		map.put(PLOT_CUST_STATS, PLOT_CUST_STATS_EXP);
		map.put(IDLE_VEHICLES_RETURN_TO_DEPOTS, IDLE_VEHICLES_RETURN_TO_DEPOTS_EXP);
		map.put(OPERATIONAL_SCHEME, OP_SCHEME_EXP);
		map.put(MAX_WALK_DISTANCE, MAX_WALK_EXP);
		map.put(TRANSIT_STOP_FILE, TRANSIT_STOP_FILE_EXP);
		map.put(ESTIMATED_DRT_SPEED, ESTIMATED_DRT_SPEED_EXP);
		map.put(ESTIMATED_BEELINE_DISTANCE_FACTOR, ESTIMATED_BEELINE_DISTANCE_FACTOR_EXP);
		map.put(NUMBER_OF_THREADS, NUMBER_OF_THREADS_EXP);
		map.put(PRINT_WARNINGS, PRINT_WARNINGS_EXP);
		map.put(REQUEST_REJECTION, REQUEST_REJECTION_EXP);
		return map;
	}

	/**
	 * @return {@value #MODE_EXP}
	 */
	@Override
	@StringGetter(MODE)
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode {@value #MODE_EXP}
	 */
	@StringSetter(MODE)
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return {@value #USE_MODE_FILTERED_SUBNETWORK_EXP}
	 */
	@StringGetter(USE_MODE_FILTERED_SUBNETWORK)
	public boolean isUseModeFilteredSubnetwork() {
		return useModeFilteredSubnetwork;
	}

	/**
	 * @param useModeFilteredSubnetwork {@value #USE_MODE_FILTERED_SUBNETWORK_EXP}
	 */
	@StringSetter(USE_MODE_FILTERED_SUBNETWORK)
	public void setUseModeFilteredSubnetwork(boolean useModeFilteredSubnetwork) {
		this.useModeFilteredSubnetwork = useModeFilteredSubnetwork;
	}

	/**
	 * @return -- {@value #STOP_DURATION_EXP}
	 */
	@StringGetter(STOP_DURATION)
	public double getStopDuration() {
		return stopDuration;
	}

	/**
	 * @param -- {@value #STOP_DURATION_EXP}
	 */
	@StringSetter(STOP_DURATION)
	public void setStopDuration(double stopDuration) {
		this.stopDuration = stopDuration;
	}

	/**
	 * @return -- {@value #MAX_WAIT_TIME_EXP}
	 */
	@StringGetter(MAX_WAIT_TIME)
	public double getMaxWaitTime() {
		return maxWaitTime;
	}

	/**
	 * @param -- {@value #MAX_WAIT_TIME_EXP}
	 */
	@StringSetter(MAX_WAIT_TIME)
	public void setMaxWaitTime(double maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	/**
	 * @return -- {@value #MAX_TRAV_ALPHA_EXP}
	 */
	@StringGetter(MAX_TRAVEL_TIME_ALPHA)
	public double getMaxTravelTimeAlpha() {
		return maxTravelTimeAlpha;
	}

	/**
	 * @param maxTravelTimeAlpha {@value #MAX_TRAV_ALPHA_EXP}
	 */
	@StringSetter(MAX_TRAVEL_TIME_ALPHA)
	public void setMaxTravelTimeAlpha(double maxTravelTimeAlpha) {
		this.maxTravelTimeAlpha = maxTravelTimeAlpha;
	}

	/**
	 * @return -- {@value #MAX_TRAVEL_TIME_BETA_EXP}
	 */
	@StringGetter(MAX_TRAVEL_TIME_BETA)
	public double getMaxTravelTimeBeta() {
		return maxTravelTimeBeta;
	}

	/**
	 * @param maxTravelTimeBeta -- {@value #MAX_TRAVEL_TIME_BETA_EXP}
	 */
	@StringSetter(MAX_TRAVEL_TIME_BETA)
	public void setMaxTravelTimeBeta(double maxTravelTimeBeta) {
		this.maxTravelTimeBeta = maxTravelTimeBeta;
	}

	/**
	 * @return -- {@value #REQUEST_REJECTION_EXP}
	 */
	@StringGetter(REQUEST_REJECTION)
	public boolean isRequestRejection() {
		return requestRejection;
	}

	/**
	 * @param requestRejection -- {@value #REQUEST_REJECTION_EXP}
	 */
	@StringSetter(REQUEST_REJECTION)
	public void setRequestRejection(boolean requestRejection) {
		this.requestRejection = requestRejection;
	}

	/**
	 * @return -- {@value #CHANGE_START_EXP}
	 */
	@StringGetter(CHANGE_START_LINK_TO_LAST_LINK_IN_SCHEDULE)
	public boolean isChangeStartLinkToLastLinkInSchedule() {
		return changeStartLinkToLastLinkInSchedule;
	}

	/**
	 * @param changeStartLinkToLastLinkInSchedule -- {@value #CHANGE_START_EXP}
	 */
	@StringSetter(CHANGE_START_LINK_TO_LAST_LINK_IN_SCHEDULE)
	public void setChangeStartLinkToLastLinkInSchedule(boolean changeStartLinkToLastLinkInSchedule) {
		this.changeStartLinkToLastLinkInSchedule = changeStartLinkToLastLinkInSchedule;
	}

	/**
	 * @return -- {@value #VEH_FILE_EXP}
	 */
	@StringGetter(VEHICLES_FILE)
	public String getVehiclesFile() {
		return vehiclesFile;
	}

	/**
	 * @param vehiclesFile -- {@value #VEH_FILE_EXP}
	 */
	@StringSetter(VEHICLES_FILE)
	public void setVehiclesFile(String vehiclesFile) {
		this.vehiclesFile = vehiclesFile;
	}

	/**
	 * @return -- {@value #VEH_FILE_EXP}
	 */
	public URL getVehiclesFileUrl(URL context) {
		return ConfigGroup.getInputFileURL(context, this.vehiclesFile);
	}

	/**
	 * @return -- {@value #IDLE_VEHICLES_RETURN_TO_DEPOTS_EXP}}
	 */
	@StringGetter(IDLE_VEHICLES_RETURN_TO_DEPOTS)
	public boolean getIdleVehiclesReturnToDepots() {
		return idleVehiclesReturnToDepots;
	}

	/**
	 * @param idleVehiclesReturnToDepots -- {@value #IDLE_VEHICLES_RETURN_TO_DEPOTS_EXP}
	 */
	@StringSetter(IDLE_VEHICLES_RETURN_TO_DEPOTS)
	public void setIdleVehiclesReturnToDepots(boolean idleVehiclesReturnToDepots) {
		this.idleVehiclesReturnToDepots = idleVehiclesReturnToDepots;
	}

	/**
	 * @return -- {@value #OP_SCHEME_EXP}
	 */
	@StringGetter(OPERATIONAL_SCHEME)
	public OperationalScheme getOperationalScheme() {
		return operationalScheme;
	}

	/**
	 * @param operationalScheme -- {@value #OP_SCHEME_EXP}
	 */
	@StringSetter(OPERATIONAL_SCHEME)
	public void setOperationalScheme(OperationalScheme operationalScheme) {
		this.operationalScheme = operationalScheme;
	}

	/**
	 * @return -- {@value #TRANSIT_STOP_FILE_EXP}
	 */
	@StringGetter(TRANSIT_STOP_FILE)
	public String getTransitStopFile() {
		return transitStopFile;
	}

	/**
	 * @return -- {@value #TRANSIT_STOP_FILE_EXP}
	 */
	public URL getTransitStopsFileUrl(URL context) {
		return ConfigGroup.getInputFileURL(context, this.transitStopFile);
	}

	/**
	 * @param-- {@value #TRANSIT_STOP_FILE_EXP}
	 */
	@StringSetter(TRANSIT_STOP_FILE)
	public void setTransitStopFile(String transitStopFile) {
		this.transitStopFile = transitStopFile;
	}

	/**
	 * @return -- {@value #MAX_WALK_EXP}
	 */
	@StringGetter(MAX_WALK_DISTANCE)
	public double getMaxWalkDistance() {
		return maxWalkDistance;
	}

	/**
	 * @param-- {@value #MAX_WALK_EXP}
	 */
	@StringSetter(MAX_WALK_DISTANCE)
	public void setMaxWalkDistance(double maximumWalkDistance) {
		this.maxWalkDistance = maximumWalkDistance;
	}

	/**
	 * @return -- {@value #ESTIMATED_DRT_SPEED_EXP}
	 */
	@StringGetter(ESTIMATED_DRT_SPEED)
	public double getEstimatedDrtSpeed() {
		return estimatedDrtSpeed;
	}

	/**
	 * @param-- {@value #ESTIMATED_DRT_SPEED_EXP}
	 */
	@StringSetter(ESTIMATED_DRT_SPEED)
	public void setEstimatedDrtSpeed(double estimatedSpeed) {
		this.estimatedDrtSpeed = estimatedSpeed;
	}

	/**
	 * @return -- {@value #ESTIMATED_BEELINE_DISTANCE_FACTOR_EXP}
	 */
	@StringGetter(ESTIMATED_BEELINE_DISTANCE_FACTOR)
	public double getEstimatedBeelineDistanceFactor() {
		return estimatedBeelineDistanceFactor;
	}

	/**
	 * @param-- {@value #ESTIMATED_BEELINE_DISTANCE_FACTOR_EXP}
	 */
	@StringSetter(ESTIMATED_BEELINE_DISTANCE_FACTOR)
	public void setEstimatedBeelineDistanceFactor(double estimatedBeelineDistanceFactor) {
		this.estimatedBeelineDistanceFactor = estimatedBeelineDistanceFactor;
	}

	/**
	 * @return -- {@value #PLOT_CUST_STATS_EXP}
	 */
	@StringGetter(PLOT_CUST_STATS)
	public boolean isPlotDetailedCustomerStats() {
		return plotDetailedCustomerStats;
	}

	/**
	 * @param -- {@value #PLOT_CUST_STATS_EXP}
	 */
	@StringSetter(PLOT_CUST_STATS)
	public void setPlotDetailedCustomerStats(boolean plotDetailedCustomerStats) {
		this.plotDetailedCustomerStats = plotDetailedCustomerStats;
	}

	/**
	 * @return -- {@value #NUMBER_OF_THREADS_EXP}
	 */
	@StringGetter(NUMBER_OF_THREADS)
	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	/**
	 * @param-- {@value #NUMBER_OF_THREADS_EXP}
	 */
	@StringSetter(NUMBER_OF_THREADS)
	public void setNumberOfThreads(final int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/**
	 * @return -- {@value #PRINT_WARNINGS_EXP}
	 */
	@StringGetter(PRINT_WARNINGS)
	public boolean isPrintDetailedWarnings() {
		return printDetailedWarnings;
	}

	/**
	 * @param -- {@value #PRINT_WARNINGS_EXP}
	 */
	@StringSetter(PRINT_WARNINGS)
	public void setPrintDetailedWarnings(boolean printDetailedWarnings) {
		this.printDetailedWarnings = printDetailedWarnings;
	}

	/**
	 * @return 'minCostFlowRebalancing' parameter set defined in the DRT config or null if the parameters were not
	 * specified
	 */
	public Optional<MinCostFlowRebalancingParams> getMinCostFlowRebalancing() {
		Collection<? extends ConfigGroup> parameterSets = getParameterSets(MinCostFlowRebalancingParams.SET_NAME);
		if (parameterSets.size() > 1) {
			throw new RuntimeException("More then one rebalancing parameter sets is specified");
		}
		return parameterSets.isEmpty() ?
				Optional.empty() :
				Optional.of((MinCostFlowRebalancingParams)parameterSets.iterator().next());
	}

	@Override
	public ConfigGroup createParameterSet(String type) {
		if (type.equals(MinCostFlowRebalancingParams.SET_NAME)) {
			return new MinCostFlowRebalancingParams();
		}
		return super.createParameterSet(type);
	}
}
