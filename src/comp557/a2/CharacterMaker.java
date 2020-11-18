// Yann Sartori 260822776
package comp557.a2;

import javax.swing.JTextField;

import mintools.parameters.BooleanParameter;

public class CharacterMaker {

	static public String name = "Yann Sartori 260822776";
	
	static BooleanParameter loadFromFile = new BooleanParameter( "Load from file (otherwise by procedure)", true );
	static JTextField baseFileName = new JTextField("data/a2data/character");
	
	/**
	 * Creates a character, either procedurally, or by loading from an xml file
	 * @return root node
	 */
	static public GraphNode create() {
		
		if ( loadFromFile.getValue() ) {
			return CharacterFromXML.load( baseFileName.getText() + ".xml");
		} else { 
						
			// Use this for testing, but ultimately it will be more interesting
			// to create your character with an xml description (see example).
			
			// Here we just return null, which will not be very interesting, so write
			// some code to create a test or partial character and return the root node.
			GraphNode root = new FreeJoint("root");
			GraphNode sphere = new SphereGeometry("ball");
			root.add(sphere);

			return root;
		}
	}
}
