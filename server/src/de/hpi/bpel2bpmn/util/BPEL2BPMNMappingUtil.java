package de.hpi.bpel2bpmn.util;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * Utility class for the BPEL 2 BPMN mapping.
 * 
 * @author matthias.weidlich
 *
 */
public final class BPEL2BPMNMappingUtil {
	
	public static final Collection<String> BPEL_ACTIVITIES = new HashSet<String>() {{ 
		add("invoke"); 
		add("receive"); 
		add("reply"); 
		add("wait"); 
		add("exit"); 
		add("empty"); 
		add("throw"); 
		add("rethrow"); 
		add("validate"); 
		add("assign"); 
		add("compensate"); 
		add("compensatescope"); 
		add("pick"); 
		add("onmessage"); 
		add("onalarm"); 
		add("sequence"); 
		add("while"); 
		add("repeatuntil"); 
		add("foreach"); 
		add("flow"); 
		add("scope"); 
		}};
	
	
	/**
	 * Checks whether an invoke activity is synchronous or not. That
	 * depends on the definition of output data, either set via the 
	 * attribute outputVariable or a toParts construct.
	 * 
	 * @param invoke
	 * @return
	 */
	public static boolean isSynchronousInvoke(Node invoke) {
		if (!invoke.getNodeName().equalsIgnoreCase("invoke"))
			return false;
		
		boolean isSynchronousInvoke = false;
		
		if (invoke.getAttributes().getNamedItem("outputVariable") != null)
			isSynchronousInvoke = true;
		
		for (Node child = invoke.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeName().equalsIgnoreCase("toParts")) {
				isSynchronousInvoke = true;
			}
		}
		return isSynchronousInvoke;
	}
	
	/**
	 * Get the node of the activity out of a node. If the name attribute
	 * is not set, we return the type of the activity as the name.
	 * 
	 * @param node
	 * @return
	 */
	public static String getRealNameOfNode(Node node) {
		String name = node.getNodeName();
		if (node.getAttributes().getNamedItem("name") != null) {
			name = node.getAttributes().getNamedItem("name").getNodeValue();
		}
		return name;
	}
	
	/**
	 * Checks whether the optional attribute createInstance is set for 
	 * a specific node. That relates to receive and pick activities.
	 * 
	 * @param node
	 * @return whether the attribute createInstance is set
	 */
	public static boolean isCreateInstanceSet(Node node) {
		boolean createInstance = false;
		if (node.getAttributes().getNamedItem("createInstance") != null) {
			if (node.getAttributes().getNamedItem("createInstance").getNodeValue().equalsIgnoreCase("yes"))
				createInstance = true;
		}
		return createInstance;
	}
	
	/**
	 * Checks whether the node has a child node that represents a 
	 * BPEL activity.
	 * 
	 * @param node
	 * @return whether there is an activity child node
	 */
	public static boolean hasActivityChildNode(Node node) {
		boolean foundNonTextChild = false;
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (!(child instanceof Text)) {
				if (BPEL_ACTIVITIES.contains(child.getNodeName())) {
					foundNonTextChild = true;
				}
			}
		}
		
		return foundNonTextChild;
	}
	
	/**
	 * Get a child node that represents a BPEL activity.
	 * 
	 * @param node
	 * @return 
	 */
	public static Node getActivityChildNode(Node node) {
		Node returnNode = null;
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (!(child instanceof Text)) {
				if (BPEL_ACTIVITIES.contains(child.getNodeName())) {
					returnNode = child;
				}
			}
		}
		
		return returnNode;
	}

	/**
	 * Get all child nodes that represent BPEL activities.
	 * 
	 * @param node
	 * @return 
	 */
	public static Collection<Node> getAllActivityChildNodes(Node node) {
		Collection<Node> returnNodes = new HashSet<Node>();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (!(child instanceof Text)) {
				if (BPEL_ACTIVITIES.contains(child.getNodeName())) {
					returnNodes.add(child);
				}
			}
		}
		
		return returnNodes;
	}

	/**
	 * Get all child nodes that represent BPEL activities. This function
	 * recursively iterates over all child nodes of the given node.
	 * 
	 * @param node
	 * @return 
	 */
	public static Collection<Node> getAllActivityChildNodesRecursively(Node node) {
		Collection<Node> returnNodes = new HashSet<Node>();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (!(child instanceof Text)) {
				if (BPEL_ACTIVITIES.contains(child.getNodeName())) {
					returnNodes.addAll(getAllActivityChildNodesRecursively(child));
				}
			}
		}
		
		return returnNodes;
	}
	
	public static boolean hasActivityChildNodeWithCreateInstanceSet(Node node) {
		boolean result = false;
		for (Node child : getAllActivityChildNodesRecursively(node)) {
			if (isCreateInstanceSet(child))
				result = true;
		}
		return result;
	}

	public static Node getSpecificChildNode(Node node, String name) {
		Node returnNode = null;
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeName().equalsIgnoreCase(name)) {
				returnNode = child;
			}
		}
		
		return returnNode;
	}
	
	public static Collection<Node> getAllSpecificChildNodes(Node node, String name) {
		Collection<Node> returnNodes = new HashSet<Node>();
		
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeName().equalsIgnoreCase(name)) {
				returnNodes.add(child);
			}
		}
		
		return returnNodes;
	}
	
	public static boolean nodeIsMappedToSingleActivity(Node node) {
		if (node.getNodeName().equalsIgnoreCase("assign")
				|| node.getNodeName().equalsIgnoreCase("validate")) {
			return true;
		}
		
		return false;
	}
	
	public static boolean nodeHasIncomingControlLinks(Node node) {
		Node targetsNode = getSpecificChildNode(node, "targets");
		
		if (targetsNode == null)
			return false;
		
		Collection<Node> targetNodes = getAllSpecificChildNodes(targetsNode, "target");
		
		if (targetNodes.size() == 0) {
			return false;
		}
		else {
			return true;
		}
	}

	public static boolean nodeHasOutgoingControlLinks(Node node) {
		Node sourcesNode = getSpecificChildNode(node, "sources");
		
		if (sourcesNode == null)
			return false;
		
		Collection<Node> sourceNodes = getAllSpecificChildNodes(sourcesNode, "source");
		
		if (sourceNodes.size() == 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * Gets the join condition for a node with incoming control links.
	 * In case there is no explicit join condition, we generate the 
	 * implicit join condition.
	 * 
	 * @param node
	 * @return
	 */
	public static String getJoinConditionOfNode(Node node) {
		Node targetsNode = getSpecificChildNode(node, "targets");	
		Node conditionNode = getSpecificChildNode(targetsNode, "joincondition");
		
		if (conditionNode != null) {
			return conditionNode.getTextContent();
		}
		else {
			// there is no explicit join condition
			// therefore we need to construct the implicit join condition
			Iterator<Node> iter = getAllSpecificChildNodes(targetsNode, "target").iterator();
			StringBuffer buffer = new StringBuffer();
			if (iter.hasNext()) {
	            buffer.append(iter.next().getAttributes().getNamedItem("linkName").getNodeValue());
	            while (iter.hasNext()) {
	                buffer.append(" OR ");
	                buffer.append(iter.next().getAttributes().getNamedItem("linkName").getNodeValue());
	            }
			}
			return buffer.toString();
		}
	}
	
	public static boolean isSuppressJoinFailure(Node node) {
		boolean result = false;

		if (node.getAttributes().getNamedItem("suppressJoinFailure") != null) {
			if (node.getAttributes().getNamedItem("suppressJoinFailure").getNodeValue().equalsIgnoreCase("yes")) {
				result = true;
			}
		} else {
			if (node.getParentNode() != null) {
				result = isSuppressJoinFailure(node.getParentNode());
			}
		}

		return result;
	}
	
	
	public static Collection<String> getAllIncomingControlLinkNames(Node node) {
		Collection<String> names = new HashSet<String>();
		Node targetsNode = getSpecificChildNode(node, "targets");
		for (Node targetNode : getAllSpecificChildNodes(targetsNode, "target")) {
			names.add(targetNode.getAttributes().getNamedItem("linkName").getNodeValue());
		}
		return names;
	}

	public static Collection<String> getAllOutgoingControlLinkNames(Node node) {
		Collection<String> names = new HashSet<String>();
		Node sourcesNode = getSpecificChildNode(node, "sources");
		for (Node sourceNode : getAllSpecificChildNodes(sourcesNode, "source")) {
			names.add(sourceNode.getAttributes().getNamedItem("linkName").getNodeValue());
		}
		return names;
	}
}