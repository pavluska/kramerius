package cz.incad.kramerius.rest.api.k5.client.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.ObjectPidsPath;
import cz.incad.kramerius.ProcessSubtreeException;
import cz.incad.kramerius.SolrAccess;
import cz.incad.kramerius.TreeNodeProcessor;
import cz.incad.kramerius.rest.api.k5.client.JSONDecorator;
import cz.incad.kramerius.rest.api.k5.client.JSONDecoratorsAggregate;
import cz.incad.kramerius.utils.XMLUtils;

public class JSONUtils {

	public static final Logger LOGGER = Logger.getLogger(JSONUtils.class.getName());
	
//	public static enum Operations {
//		read, edit, create, delete
//	}
	
	public static JSONObject link(JSONObject obj, String key, String link ) {
		JSONObject json = new JSONObject();
		json.put("href", link);
		obj.put(key, json);
		return obj;
	}
	
	public static JSONObject pidAndModelDesc(String pid, JSONObject jsonObject, FedoraAccess fedoraAccess,String callContext, JSONDecoratorsAggregate decoratorsAggregate, String baseLink)
			throws IOException {
		jsonObject.put("pid", pid);
		if (PIDSupport.isComposedPID(pid)) {
			// page model
			jsonObject.put("model", "page");
		} else {
			jsonObject.put("model", fedoraAccess.getKrameriusModelName(pid));
		}

		// apply decorator
		if (callContext != null && decoratorsAggregate != null) {
			Map<String, Object> m = new HashMap<String, Object>();
			List<JSONDecorator> ldecs = decoratorsAggregate.getDecorators();
			for (JSONDecorator d : ldecs) { d.before(m); }
			for (JSONDecorator d : ldecs) {
				if (d.apply(jsonObject, callContext)) {
					d.decorate(jsonObject, m);
				} 
			}
			for (JSONDecorator d : ldecs) { d.after(); }
		}
		return jsonObject;
	}

	public static JSONObject pidAndModelDesc(String pid, FedoraAccess fedoraAccess, String callContext, JSONDecoratorsAggregate decoratorsAggregate, String baseUrl)
			throws IOException {
		return pidAndModelDesc(pid, new JSONObject(),fedoraAccess, callContext, decoratorsAggregate, baseUrl);
	}
	
	public static JSONObject miniature(String pid, FedoraAccess fedoraAccess, JSONObject jsonObject) {
		throw new UnsupportedOperationException();
	}
	
	
	
	
	public static JSONArray children(final String pid, FedoraAccess fedoraAccess, SolrAccess solrAccess) {
		try {
			
			JSONArray jsonArray = new JSONArray();
			

			final List<String> children = new ArrayList<String>();
			fedoraAccess.processSubtree(pid, new TreeNodeProcessor() {

				
				@Override
				public boolean skipBranch(String p, int level) {
					return level > 1;
				}
				
				@Override
				public void process(String p, int level) throws ProcessSubtreeException {
					if (level == 1) {
						children.add(p);
					}
				}
				
				@Override
				public boolean breakProcessing(String p, int level) {
					return false;
				}
			});
			for (String chpid : children) {
				jsonArray.add(chpid);
			}
			return jsonArray;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new JSONArray();
		} catch (ProcessSubtreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new JSONArray();
		}
		
	}
	

	private static ObjectPidsPath selectPath(ObjectPidsPath[] paths) {
		return paths.length > 0 ? paths[0] : null;
	}

	
//	public static JSONArray context(String pid, SolrAccess solrAccess, FedoraAccess fedoraAccess) throws IOException {
//		JSONArray pathsArr = new JSONArray();
//		ObjectPidsPath[] path = solrAccess.getPath(pid);
//		for (ObjectPidsPath objectPidsPath : path) {
//			JSONArray onePathArray = new JSONArray();
//			String[] pathFromRootToLeaf = objectPidsPath.getPathFromRootToLeaf();
//			for (String ppid : pathFromRootToLeaf) {
//				onePathArray.add(pidAndModelDesc(ppid, fedoraAccess));
//			}
//			pathsArr.add(onePathArray);
//		}
//		return pathsArr;
//	}

	
	
	public static JSONElementTree elementTree(Document doc) {
		Map<Element, JSONElementTree> trees = new HashMap<Element, JSONElementTree>();
		Stack<Element> stack = new Stack<Element>();
		stack.push(doc.getDocumentElement());
		while(!stack.isEmpty()) {
			Element topElement = stack.pop();
			JSONElementTree theTree = trees.get(topElement);
			if (theTree  == null) {
				if (topElement.getParentNode() != null && topElement.getParentNode().getNodeType() == Node.ELEMENT_NODE) {
					theTree = new JSONElementTree(trees.get(topElement.getParentNode()), topElement);
					trees.get(topElement.getParentNode()).addChild(theTree);
				} else {
					theTree = new JSONElementTree(null, topElement);
				}
			}
			
			trees.put(topElement, theTree);
			
			List<Element> elms = XMLUtils.getElements(topElement);
			for (Element le : elms) { stack.push(le); }
		}
		return trees.get(doc.getDocumentElement());
	}
	
	
	public static class JSONElementTree {

		private Element element;
		private JSONElementTree parent;
		private List<JSONElementTree> children = new ArrayList<JSONUtils.JSONElementTree>();
		
		public JSONElementTree(JSONElementTree parent, Element elm) {
			this.parent = parent;
			this.element = elm;
		}
		
		public Element getElement() {
			return element;
		}
		
		public JSONElementTree getParent() {
			return parent;
		}
		
		public List<JSONElementTree> getChildren() {
			return children;
		}
		
		public void addChild(JSONElementTree ch) {
			this.children.add(ch);
		}
		
		public void removeChild(JSONElementTree ch) {
			this.children.remove(ch);
		}
		
		public String getKey() {
			return this.element.getLocalName();
		}
		
		public JSONObject toJSON(JSONObject parent) {
			JSONObject jsonObj = new JSONObject();
			if (XMLUtils.getElements(this.element).isEmpty()) {
				jsonObj.put("textcontent", this.element.getTextContent());
			}
			JSONObject attributes = new JSONObject();
			NamedNodeMap nmp = this.element.getAttributes();
			for (int i = 0,ll=nmp.getLength(); i < ll; i++) {
				Node node = nmp.item(i);
				if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
					Attr attr = (Attr) node;
					attributes.put(attr.getName(), attr.getValue());
				}
			}
			if (nmp.getLength() > 0) {
				jsonObj.put("attributes", attributes);
			}
			
			for (JSONElementTree theTree : this.children) {
				JSONObject child = theTree.toJSON(jsonObj);
				if (!jsonObj.containsKey(theTree.getKey())) {
					jsonObj.put(theTree.getKey(), child);
				} else {
					JSON json = (JSON) jsonObj.get(theTree.getKey());
					if (json.isArray()) {
						((JSONArray)json).add(child);
					} else {
						JSONArray jsonArray = new JSONArray();
						jsonArray.add(json);
						jsonArray.add(child);
						jsonObj.put(theTree.getKey(), jsonArray);
					}
				}
			}
			return jsonObj;
		}
		
	}

}
