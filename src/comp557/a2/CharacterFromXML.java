// Yann Sartori 260822776
package comp557.a2;
 		  	  				   
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Loads an articulated character hierarchy from an XML file. 
 */
public class CharacterFromXML {

	public static GraphNode load( String filename ) {
		try {
			InputStream inputStream = new FileInputStream(new File(filename));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			return createScene( null, document.getDocumentElement() ); // we don't check the name of the document elemnet
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load simulation input file.", e);
		}
	}
	
	/**
	 * Load a subtree from a XML node.
	 * Returns the root on the call where the parent is null, but otherwise
	 * all children are added as they are created and all other deeper recursive
	 * calls will return null.
	 */
	public static GraphNode createScene( GraphNode parent, Node dataNode ) {
        NodeList nodeList = dataNode.getChildNodes();
        for ( int i = 0; i < nodeList.getLength(); i++ ) {
            Node n = nodeList.item(i);
            // skip all text, just process the ELEMENT_NODEs
            if ( n.getNodeType() != Node.ELEMENT_NODE ) continue;
            String nodeName = n.getNodeName();
            GraphNode node = null;
            if ( nodeName.equalsIgnoreCase( "node" ) ) {
            	node = CharacterFromXML.createJoint( n );
            } else if ( nodeName.equalsIgnoreCase( "geom" ) ) {        		
        		node = CharacterFromXML.createGeom( n ) ;            
            } else {
            	System.err.println("Unknown node " + nodeName );
            	continue;
            }
            if ( node == null ) continue;
            // recurse to load any children of this node
            createScene( node, n );
            if ( parent == null ) {
            	// if no parent, we can only have one root... ignore other nodes at root level
            	return node;
            } else {
            	parent.add( node );
            }
        }
        return null;
	}
	
	/**​‌​​​‌‌​​​‌‌​​​‌​​‌‌‌​​‌
	 * Create a joint
	 * 
	 */
	public static GraphNode createJoint( Node dataNode ) {
		String type = dataNode.getAttributes().getNamedItem("type").getNodeValue();
		String name = dataNode.getAttributes().getNamedItem("name").getNodeValue();
		double[] xyzRot, xyzPos, xyzRotAxis, minmaxAngle, minAngle, maxAngle, xyzScale;
		Node isStatic;

		try {
			xyzPos = getAttributeByName(dataNode, "position", 3);
			xyzRot = getAttributeByName(dataNode, "rotation", 3);
			xyzRotAxis = getAttributeByName(dataNode, "rotation-axis", 3); 
			minmaxAngle = getAttributeByName(dataNode, "min-max-angle", 3);
			minAngle = getAttributeByName(dataNode, "min", 3);
			maxAngle = getAttributeByName(dataNode, "max", 3);
			isStatic = dataNode.getAttributes().getNamedItem("static");
			xyzScale = getAttributeByName(dataNode, "scale", 3);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}

		if ( type.equals("freejoint") ) {
			FreeJoint joint = new FreeJoint( name );
			if ( isStatic == null ) joint.addDofs();
			if ( xyzRot != null ) joint.setRotation(xyzRot[0], xyzRot[1], xyzRot[2]);
			if ( xyzPos != null ) joint.setTranslation(xyzPos[0], xyzPos[1], xyzPos[2]);
			if ( xyzScale != null ) joint.setScale(xyzScale[0], xyzScale[1], xyzScale[2]);

			return joint;
		} else if ( type.equals("spherical") ) {
			SphericalJoint joint = new SphericalJoint(name);

			if ( minAngle != null ) joint.setMin(minAngle[0], minAngle[1], minAngle[2]);
			if ( maxAngle != null ) joint.setMax(maxAngle[0], maxAngle[1], maxAngle[2]);
			if ( xyzRot != null ) joint.setRotation(xyzRot[0], xyzRot[1], xyzRot[2]);
			if ( xyzPos != null ) joint.setTranslation(xyzPos[0], xyzPos[1], xyzPos[2]);
	
			return joint;
		} else if ( type.equals("rotary") ) {
			RotaryJoint joint = new RotaryJoint(name);

			if ( xyzPos != null ) joint.setTranslation(xyzPos[0], xyzPos[1], xyzPos[2]);
			if ( xyzRotAxis != null ) joint.setRotationAxis(xyzRotAxis[0], xyzRotAxis[1], xyzRotAxis[2]);
			if ( minmaxAngle != null ) joint.setMinMaxAngle(minmaxAngle[0], minmaxAngle[1], minmaxAngle[2]);

			return joint;
		} else {
			System.err.println("Unknown type " + type );
		}
		return null;
	}

	/**
	 * Creates a geometry DAG node 
	 * 
	 * TODO: Objective 5: Adapt commented code in greatGeom to create your geometry nodes when loading from xml
	 */
	public static GraphNode createGeom( Node dataNode ) {
		String type = dataNode.getAttributes().getNamedItem("type").getNodeValue();
		String name = dataNode.getAttributes().getNamedItem("name").getNodeValue();
		double[] xyzPos, rgb, xyzScale, xyzRot, ks, kd;
		try {
			xyzPos = getAttributeByName(dataNode, "position", 3);
			xyzScale = getAttributeByName(dataNode, "scale", 3); 
			rgb = getAttributeByName(dataNode, "colour", 3);
			xyzRot = getAttributeByName(dataNode, "rotation", 3);
			ks = getAttributeByName(dataNode, "ks", 1);
			kd = getAttributeByName(dataNode, "ks", 1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		GeometryNode geom = null;
		if ( type.equals("cube" ) ) {
			geom = new CubeGeometry( name );
		} else if ( type.equals( "sphere" )) {
			geom = new SphereGeometry( name );				
		} else {
			System.err.println("unknown type " + type );
			return null;
		}
		if ( xyzPos != null ) geom.setTranslation(xyzPos[0], xyzPos[1], xyzPos[2]);
		if ( xyzScale != null ) geom.setScale(xyzScale[0], xyzScale[1], xyzScale[2]);
		if ( rgb != null ) geom.setColour(rgb[0], rgb[1], rgb[2]);
		if ( xyzRot != null ) geom.setRotation(xyzRot[0], xyzRot[1], xyzRot[2]);
		if ( ks != null ) geom.setKs(ks[0]);
		if ( kd != null ) geom.setKd(kd[0]);
		return geom;		
	}

	public static double[] getAttributeByName(Node dataNode, String attrName, int numElems) {
		Node attr = dataNode.getAttributes().getNamedItem(attrName);
		double[] arr = new double[numElems];
		if ( attr == null ) return null;
		Scanner s = new Scanner(attr.getNodeValue());
		int i = 0;
		try {
			while ( s.hasNextDouble() ) {
				arr[i] = s.nextDouble();
				i++;
			}
		} catch (IndexOutOfBoundsException e) {
			s.close();
			throw new Error(String.format("Error: provided more parameters then %s expected, expected %d", attrName, numElems));
		}
		if ( i < numElems ) {
			s.close();
			throw new Error(String.format("Error: provided %d parameters for %s, expecting %d", i, attrName, numElems));
		}
		s.close();
		return arr;
	}

}