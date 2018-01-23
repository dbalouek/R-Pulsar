//package com.rutgers.QuadTree;
//
//import com.teamdev.jxbrowser.chromium.Browser;
//import com.teamdev.jxbrowser.chromium.BrowserPreferences;
//import com.teamdev.jxbrowser.chromium.BrowserType;
//import com.teamdev.jxbrowser.chromium.swing.BrowserView;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Vector;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.WindowConstants;
//
//@SuppressWarnings("serial")
//public class QuadTreeGUI extends JPanel {
//    
//    private Browser browser;
//    private BrowserView view;
//    private PointQuadTree<String> QuadTree;
//
//    public QuadTreeGUI() {
//
//    }
//    
//    /**
//     * Create a new panel
//     */
//    public void setupGui() throws IOException {
//        browser = new Browser(BrowserType.LIGHTWEIGHT);
//        BrowserPreferences preferences = browser.getPreferences();
//        preferences.setTransparentBackground(true);
//        browser.setPreferences(preferences);
//        view = new BrowserView(browser);
//        
//        JButton zoomOutButton = new JButton("", new ImageIcon(this.getClass().getClassLoader().getResource("Images/HeatMap.png")));
//        zoomOutButton.setBorderPainted(false); 
//        zoomOutButton.setContentAreaFilled(false); 
//        zoomOutButton.setFocusPainted(false); 
//        zoomOutButton.setOpaque(false);
//        
//        JPanel toolBar = new JPanel();
//        toolBar.setLayout(new BorderLayout());
//        toolBar.setOpaque(true);
//        toolBar.setBackground(new Color(0, 0, 0, 125));
//        toolBar.add(zoomOutButton);
//        
//        
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("Images/HeatMap.png"))));
//        frame.add(view, BorderLayout.CENTER);
//        frame.setSize(900, 500);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//
//        browser.loadURL(this.getClass().getClassLoader().getResource("Maps/map.html").toString());
//    }
//    
//    public void updateSreen(PointQuadTree<String> quadTree) {
//        QuadTree = quadTree;
//        PointNode<String> rootNode = QuadTree.getRootNode();
//        clearPins();
//        clearRectangles();
//        drawRectangles(rootNode);
//    }
//    
//    private void drawRectangles(PointNode<String> node) {      
//        System.out.println("Rectangle: " + node.getNorth() + "," + node.getSouth() + " " + node.getEast() + "," +  node.getWest());
//        browser.executeJavaScript("var rectangle = new google.maps.Rectangle({\n" +
//            "strokeColor: '"+ node.strokeColor + "',\n" +
//            "strokeOpacity: 0.8,\n" +
//            "strokeWeight: 2,\n" +
//            "fillColor: '" + node.fillColor + "',\n" +
//            "fillOpacity: 0.1,\n" +
//            "map: map,\n" +
//            "bounds: { \n" +
//            "    north: "+ node.getNorth() +",\n" + 
//            "    south: "+ node.getSouth() +",\n" + 
//            "    east: "+ node.getEast() +",\n" + 
//            "    west: "+ node.getWest() +"\n" +
//            "}});\n" +
//            "rectangles.push(rectangle);\n");
//        
//        
//        Map<AbstractNode.Cell, PointNode<String>> subNodes = node.getSubNodes();
//        
//        for (PointNode<String> subNode : subNodes.values()) {
//                drawRectangles(subNode);
//        }
//        
//        drawPins(node);
//    }
//    
//    private void drawPins(PointNode node) {
//        Vector<AbstractNodeElement<String>> elements = (Vector<AbstractNodeElement<String>>) node.getElements();
//        for (AbstractNodeElement<String> element : elements) {
//            double latitude = element.getLatitude();
//            double longitude = element.getLongitude();
//            String name = element.getElement();
//            
//            browser.executeJavaScript("var myLatlng = new google.maps.LatLng("+ latitude + "," + longitude +");\n" +
//                "var image = '" + element.getIcon() + "';\n" +
//                "var marker = new google.maps.Marker({\n" +
//                "    position: myLatlng,\n" +
//                "    animation: google.maps.Animation.DROP,\n" +
//                "    map: map,\n" +
//                "    title: '"+ name +"',\n" +
//                "    icon: image " +
//                "});\n" +
//                "marker.setMap(map);\n" +
//                "markers.push(marker);\n" +
////                "var infowindow = new google.maps.InfoWindow({\n" +
////                "content:\"" + name + "\"});\n" +
////                "infowindow.open(map,marker);\n" +
//                "marker.setAnimation(google.maps.Animation.BOUNCE);\n");
////                "markers.forEach(function(marker){\n" +
////                "marker.addListener('click', function() {\n" +
////                "if (marker.getAnimation() !== null) {\n" +
////                "marker.setAnimation(null);\n" +
////                "} else {\n"+
////                "marker.setAnimation(google.maps.Animation.BOUNCE);\n"+
////                "}\n"+
////                "});\n" +
////                "});\n");
//        }
//    }
//    
//    private void clearPins() {
//        browser.executeJavaScript("markers.forEach(function(marker){\n"
//                            + "marker.setMap(null);\n"
//                            + "});\n");
//    }
//    
//    private void clearRectangles() {
//        browser.executeJavaScript("rectangles.forEach(function(rectangle){\n"
//                    + "rectangle.setMap(null);\n"
//                    + "});\n");
//    }
//}
