/* *********************************************************************** *
 * project: org.matsim.													   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,     *
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

/**
 * 
 */
package playground.kai.usecases.invertednetwork;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.router.*;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;

import javax.inject.Provider;

/**
 * @author thomas
 *
 */
public class InvertedNetworkForCarsRouterFactoryImpl implements Provider<TripRouter> {


	private Provider<TripRouter> delegate;

	private Scenario scenario;



	private TravelDisutilityFactory tdf;

//	private boolean firstCall = true;
	
	public InvertedNetworkForCarsRouterFactoryImpl(final Scenario scenario, TravelDisutilityFactory tdf ) {
		this.delegate = TripRouterFactoryBuilderWithDefaults.createDefaultTripRouterFactoryImpl(scenario);
		this.scenario = scenario;
		this.tdf = tdf ;
	}
	
	@Override
	public TripRouter get() {
	
		TripRouter tripRouter = this.delegate.get();

		// add alternative module for car routing
		tripRouter.setRoutingModule(TransportMode.car, new InvertedRoutingModule(this.scenario,this.tdf) );
		
		return tripRouter;
	}
}