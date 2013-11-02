package net.onthewings.bezelcursor;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

public class AccessibilityNodeTree {
	public class Node {
		public boolean isCurrent = false;
		public Node parent = null;
		public List<Node> children = new LinkedList<AccessibilityNodeTree.Node>();
		public int hashCode;
		public int actions;
		public Rect screenBound = new Rect();
		//public boolean isAccessibilityFocused;
		public boolean isCheckable;
		public boolean isChecked;
		public boolean isClickable;
		public boolean isEnabled;
		public boolean isFocusable;
		public boolean isFocused;
		public boolean isLongClickable;
		public boolean isPassword;
		public boolean isScrollable;
		public boolean isSelected;
		//public boolean isVisibleToUser;
		public String className;
		public String text;
		public String contentDescription;
		public String packageName;
		
		public Node(AccessibilityNodeInfo a_node) {
			hashCode = a_node.hashCode();
			actions = a_node.getActions();
			a_node.getBoundsInScreen(screenBound);
			//isAccessibilityFocused = a_node.isAccessibilityFocused();
			isCheckable = a_node.isCheckable();
			isChecked = a_node.isChecked();
			isClickable = a_node.isCheckable();
			isEnabled = a_node.isEnabled();
			isFocusable = a_node.isFocusable();
			isFocused = a_node.isFocused();
			isLongClickable = a_node.isLongClickable();
			isPassword = a_node.isPassword();
			isScrollable = a_node.isScrollable();
			isSelected = a_node.isSelected();
			//isVisibleToUser = a_node.isVisibleToUser();
			
			className = a_node.getClassName().toString();
			text = a_node.getText().toString();
			contentDescription = a_node.getContentDescription().toString();
			packageName = a_node.getPackageName().toString();
		}
	}
	
	public Node current;
	public Node root;
	
	public AccessibilityNodeTree(AccessibilityNodeInfo current) {
		this.current = new Node(current);
		this.current.isCurrent = true;
		
		AccessibilityNodeInfo _root = current;
		while (_root.getParent() != null) _root = _root.getParent();
		root = constructTree(_root);
	}
	
	Node constructTree(AccessibilityNodeInfo a_node) {
		Node node = a_node.hashCode() == current.hashCode ? current : new Node(a_node);
		int childCount = a_node.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			AccessibilityNodeInfo child = a_node.getChild(i);
			Node childNode = constructTree(child);
			childNode.parent = node;
			node.children.add(childNode);
		}
		return node;
	}
}
