/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.deacpositioning;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

/**
 *
 * @author gergohajzer
 */
public class DeacPosM {
    
    public static <T> void main(String[] args){
	JXMapViewer mapViewer = new JXMapViewer();
            
	JFrame frame = new JFrame("Deac tagok eloszlása");
	frame.getContentPane().add(mapViewer);
	frame.setSize(800, 600);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);

	TileFactoryInfo info = new OSMTileFactoryInfo();
	DefaultTileFactory tileFactory = new DefaultTileFactory(info);
	tileFactory.setThreadPoolSize(8);
	mapViewer.setTileFactory(tileFactory);
        
        MouseInputListener mouseInputListener = new PanMouseInputListener(mapViewer);
	mapViewer.addMouseListener(mouseInputListener);
	mapViewer.addMouseMotionListener(mouseInputListener);
	mapViewer.addMouseListener(new CenterMapListener(mapViewer));
	mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
	mapViewer.addKeyListener(new PanKeyListener(mapViewer));
                
        boolean cityBool = true;
        List<String> city = new ArrayList<>();
        List<Boolean> member = new ArrayList<>();
        //TODO  ?white? waypoint for DEIK members.                
        try{
            java.util.Scanner in = new java.util.Scanner(new java.io.FileReader("Input/varosok"));
                while(in.hasNext()) {
                    if (cityBool) {
                        city.add(in.next());
                        cityBool = false;
                    }else {
                        if (in.next().equals("igen")) {
                            member.add(true);
                        }else {
                            member.add(false);
                        }
                        cityBool = true;
                    }
                }
        }catch(java.io.FileNotFoundException ex){
            System.err.println("Error opening the file!");
            System.exit(1);   
        }
                
        List<GeoPosition> track = new ArrayList<>();
                
        city.stream().map((element) -> {
            java.util.Map<String, Double> coords;
            coords = OpenStreetMapUtils.getInstance().getCoordinates(element);
            return coords;
        }).forEachOrdered((coords) -> {
            track.add(new GeoPosition(coords.get("lat"),coords.get("lon")));
        });
                
	mapViewer.zoomToBestFit(new HashSet<>(track), 0.7);
                

        Set<Waypoint> waypoints = new HashSet<>();
        
        track.forEach((element) -> {
            waypoints.add(new DefaultWaypoint(element));
        });

	WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
	waypointPainter.setWaypoints(waypoints);
		
	List<Painter<JXMapViewer>> painters = new ArrayList<>();
	painters.add(waypointPainter);
		
	CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
	mapViewer.setOverlayPainter(painter);
    }    
}
