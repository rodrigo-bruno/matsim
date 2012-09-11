/* *********************************************************************** *
 * project: org.matsim.*
 * TransitRouterNetwork.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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

package playground.sergioo.TransitRouterVariable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.misc.Counter;
import org.matsim.core.utils.misc.Time;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

/**
 * @author sergioo
 *
 */

public final class TransitRouterNetworkWW implements Network {

	private final static Logger log = Logger.getLogger(TransitRouterNetworkWW.class);
	
	private final Map<Id, TransitRouterNetworkLink> links = new LinkedHashMap<Id, TransitRouterNetworkLink>();
	private final Map<Id, TransitRouterNetworkNode> nodes = new LinkedHashMap<Id, TransitRouterNetworkNode>();
	protected QuadTree<TransitRouterNetworkNode> qtNodes = null;

	private long nextNodeId = 0;
	protected long nextLinkId = 0;

	public static final class TransitRouterNetworkNode implements Node {

		public final TransitRouteStop stop;
		public final TransitRoute route;
		public final TransitLine line;
		final Id id;
		final Map<Id, TransitRouterNetworkLink> ingoingLinks = new LinkedHashMap<Id, TransitRouterNetworkLink>();
		final Map<Id, TransitRouterNetworkLink> outgoingLinks = new LinkedHashMap<Id, TransitRouterNetworkLink>();

		public TransitRouterNetworkNode(final Id id, final TransitRouteStop stop, final TransitRoute route, final TransitLine line) {
			this.id = id;
			this.stop = stop;
			this.route = route;
			this.line = line;
		}

		@Override
		public Map<Id, ? extends Link> getInLinks() {
			return this.ingoingLinks;
		}

		@Override
		public Map<Id, ? extends Link> getOutLinks() {
			return this.outgoingLinks;
		}

		@Override
		public boolean addInLink(final Link link) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addOutLink(final Link link) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Coord getCoord() {
			return this.stop.getStopFacility().getCoord();
		}

		@Override
		public Id getId() {
			return this.id;
		}

		public TransitRouteStop getStop() {
			return stop;
		}

		public TransitRoute getRoute() {
			return route;
		}

		public TransitLine getLine() {
			return line;
		}
	}

	/**
	 * Looks to me like an implementation of the Link interface, with get(Transit)Route and get(Transit)Line on top.
	 * To recall: TransitLine is something like M44.  But it can have more than one route, e.g. going north, going south,
	 * long route, short route. That is, presumably we have one such TransitRouterNetworkLink per TransitRoute. kai/manuel, feb'12
	 */
	public static final class TransitRouterNetworkLink implements Link {

		public final TransitRouterNetworkNode fromNode;
		public final TransitRouterNetworkNode toNode;
		final TransitRoute route;
		final TransitLine line;
		final Id id;
		private double length;

		public TransitRouterNetworkLink(final Id id, final TransitRouterNetworkNode fromNode, final TransitRouterNetworkNode toNode, final TransitRoute route, final TransitLine line) {
			this.id = id;
			this.fromNode = fromNode;
			this.toNode = toNode;
			this.route = route;
			this.line = line;
			this.length = CoordUtils.calcDistance(this.toNode.stop.getStopFacility().getCoord(), this.fromNode.stop.getStopFacility().getCoord());
		}

		@Override
		public TransitRouterNetworkNode getFromNode() {
			return this.fromNode;
		}

		@Override
		public TransitRouterNetworkNode getToNode() {
			return this.toNode;
		}

		@Override
		public double getCapacity() {
			return getCapacity(Time.UNDEFINED_TIME);
		}

		@Override
		public double getCapacity(final double time) {
			return 9999;
		}

		@Override
		public double getFreespeed() {
			return getFreespeed(Time.UNDEFINED_TIME);
		}

		@Override
		public double getFreespeed(final double time) {
			return 10;
		}

		@Override
		public Id getId() {
			return this.id;
		}

		@Override
		public double getNumberOfLanes() {
			return getNumberOfLanes(Time.UNDEFINED_TIME);
		}

		@Override
		public double getNumberOfLanes(final double time) {
			return 1;
		}

		@Override
		public double getLength() {
			return this.length;
		}

		@Override
		public void setCapacity(final double capacity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setFreespeed(final double freespeed) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean setFromNode(final Node node) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setNumberOfLanes(final double lanes) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setLength(final double length) {
			this.length = length;
		}

		@Override
		public boolean setToNode(final Node node) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Coord getCoord() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<String> getAllowedModes() {
			return null;
		}

		@Override
		public void setAllowedModes(final Set<String> modes) {
			throw new UnsupportedOperationException();
		}

		public TransitRoute getRoute() {
			return route;
		}

		public TransitLine getLine() {
			return line;
		}

	}
	public TransitRouterNetworkNode createNode(final TransitRouteStop stop, final TransitRoute route, final TransitLine line) {
		final TransitRouterNetworkNode node = new TransitRouterNetworkNode(new IdImpl(this.nextNodeId++), stop, route, line);
		this.nodes.put(node.getId(), node);
		return node;
	}

	public TransitRouterNetworkLink createLink(final TransitRouterNetworkNode fromNode, final TransitRouterNetworkNode toNode, final TransitRoute route, final TransitLine line) {
		final TransitRouterNetworkLink link = new TransitRouterNetworkLink(new IdImpl(this.nextLinkId++), fromNode, toNode, route, line);
		this.links.put(link.getId(), link);
		fromNode.outgoingLinks.put(link.getId(), link);
		toNode.ingoingLinks.put(link.getId(), link);
		return link;
	}
	@Override
	public Map<Id, TransitRouterNetworkNode> getNodes() {
		return this.nodes;
	}
	@Override
	public Map<Id, TransitRouterNetworkLink> getLinks() {
		return this.links;
	}
	public void finishInit() {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (TransitRouterNetworkNode node : getNodes().values())
			if(node.line == null) {
				Coord c = node.stop.getStopFacility().getCoord();
				if (c.getX() < minX)
					minX = c.getX();
				if (c.getY() < minY)
					minY = c.getY();
				if (c.getX() > maxX)
					maxX = c.getX();
				if (c.getY() > maxY)
					maxY = c.getY();
			}
		QuadTree<TransitRouterNetworkNode> quadTree = new QuadTree<TransitRouterNetworkNode>(minX, minY, maxX, maxY);
		for (TransitRouterNetworkNode node : getNodes().values()) {
			if(node.line == null) {	
				Coord c = node.stop.getStopFacility().getCoord();
				quadTree.put(c.getX(), c.getY(), node);
			}
		}
		this.qtNodes = quadTree;
	}
	public static TransitRouterNetworkWW createFromSchedule(final Network network, final TransitSchedule schedule, final double maxBeelineWalkConnectionDistance) {
		log.info("start creating transit network");
		final TransitRouterNetworkWW transitNetwork = new TransitRouterNetworkWW();
		final Counter linkCounter = new Counter(" link #");
		final Counter nodeCounter = new Counter(" node #");
		// build nodes and links connecting the nodes according to the transit routes
		for (TransitLine line : schedule.getTransitLines().values())
			for (TransitRoute route : line.getRoutes().values()) {
				TransitRouterNetworkNode prevNode = null;
				for (TransitRouteStop stop : route.getStops()) {
					TransitRouterNetworkNode node = transitNetwork.createNode(stop, route, line);
					nodeCounter.incCounter();
					if (prevNode != null) {
						transitNetwork.createLink(network, prevNode, node, route, line);
						linkCounter.incCounter();
					}
					prevNode = node;
				}
			}
		// build stop nodes and waiting links
		log.info("add waiting links");
		List<Tuple<TransitRouterNetworkNode, TransitRouterNetworkNode>> toBeAdded = new LinkedList<Tuple<TransitRouterNetworkNode, TransitRouterNetworkNode>>();
		for(TransitStopFacility stopF:schedule.getFacilities().values()) {
			TransitRouteStop stop = null;
			SEARCH_ROUTE_STOP://For finding one route stop with the desired facility
			for (TransitLine line : schedule.getTransitLines().values())
				for (TransitRoute route : line.getRoutes().values())
					for (TransitRouteStop stop2 : route.getStops())
						if(stopF.getId().equals(stop2.getStopFacility().getId())) {
							stop = stop2;
							break SEARCH_ROUTE_STOP;
						}
			if(stop!=null) {
				TransitRouterNetworkNode node = transitNetwork.createNode(stop, null, null);
				nodeCounter.incCounter();
				for(TransitRouterNetworkNode node2 : transitNetwork.getNodes().values())
					if(node2.getLine()!=null && node2.stop.getStopFacility().getId().equals(stopF.getId())) {
						toBeAdded.add(new Tuple<TransitRouterNetworkNode, TransitRouterNetworkNode>(node, node2));
						linkCounter.incCounter();
					}
			}
		}
		log.info(toBeAdded.size() + " waiting links to be added.");
		for (Tuple<TransitRouterNetworkNode, TransitRouterNetworkNode> tuple : toBeAdded)
			transitNetwork.createLink(tuple.getFirst(), tuple.getSecond(), null, null);
		transitNetwork.finishInit();
		// build transfer links
		log.info("add transfer links");
		toBeAdded = new LinkedList<Tuple<TransitRouterNetworkNode, TransitRouterNetworkNode>>();
		// connect all stops with walking links if they're located less than beelineWalkConnectionDistance from each other
		for (TransitRouterNetworkNode node : transitNetwork.getNodes().values())
			if (node.line!=null && node.getInLinks().size()>0) // only add links from this node to other nodes if agents actually can arrive here
				for (TransitRouterNetworkNode node2 : transitNetwork.getNearestNodes(node.stop.getStopFacility().getCoord(), maxBeelineWalkConnectionDistance))
					if (node2.line==null) { // only add links to other nodes when agents can depart there
						toBeAdded.add(new Tuple<TransitRouterNetworkNode, TransitRouterNetworkNode>(node, node2));
						linkCounter.incCounter();
					}
		log.info(toBeAdded.size() + " transfer links to be added.");
		for (Tuple<TransitRouterNetworkNode, TransitRouterNetworkNode> tuple : toBeAdded)
			transitNetwork.createLink(tuple.getFirst(), tuple.getSecond(), null, null);
		log.info("transit router network statistics:");
		log.info(" # nodes: " + transitNetwork.getNodes().size());
		log.info(" # links total:     " + transitNetwork.getLinks().size());
		log.info(" # transfer links:  " + toBeAdded.size());
		return transitNetwork;
	}
	public TransitRouterNetworkLink createLink(final Network network, final TransitRouterNetworkNode fromNode, final TransitRouterNetworkNode toNode, final TransitRoute route, final TransitLine line) {
		final TransitRouterNetworkLink link = new TransitRouterNetworkLink(new IdImpl(this.nextLinkId++), fromNode, toNode, route, line);
		if(route!=null) {
			double length = 0;
			for(Id linkId:route.getRoute().getSubRoute(fromNode.stop.getStopFacility().getLinkId(), toNode.stop.getStopFacility().getLinkId()).getLinkIds())
				length += network.getLinks().get(linkId).getLength();
			link.setLength(length);
		}
		this.getLinks().put(link.getId(), link);
		fromNode.outgoingLinks.put(link.getId(), link);
		toNode.ingoingLinks.put(link.getId(), link);
		return link;
	}
	public Collection<TransitRouterNetworkNode> getNearestNodes(final Coord coord, final double distance) {
		return this.qtNodes.get(coord.getX(), coord.getY(), distance);
	}

	public TransitRouterNetworkNode getNearestNode(final Coord coord) {
		return this.qtNodes.get(coord.getX(), coord.getY());
	}

	@Override
	public double getCapacityPeriod() {
		return 3600.0;
	}

	@Override
	public NetworkFactory getFactory() {
		return null;
	}

	@Override
	public double getEffectiveLaneWidth() {
		return 3;
	}

	@Override
	public void addNode(Node nn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addLink(Link ll) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Link removeLink(Id linkId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node removeNode(Id nodeId) {
		throw new UnsupportedOperationException();
	}
}
