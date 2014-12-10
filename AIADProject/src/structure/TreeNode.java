package structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> implements Iterable<TreeNode<T>> {

	T data;
	TreeNode<T> parent;
	List<TreeNode<T>> children;
	int value = 0;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public TreeNode(T data) {
		this.data = data;
		this.children = new LinkedList<TreeNode<T>>();
	}

	public TreeNode<T> addChild(T child, int value) {
		TreeNode<T> childNode = new TreeNode<T>(child);
		childNode.parent = this;
		childNode.setValue(value);
		this.children.add(childNode);
		return childNode;
	}

	@Override
	public Iterator<TreeNode<T>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode<T>> children) {
		this.children = children;
	}

	public TreeNode<T> getLeastValue() {
		int rspInt = 1000000000;
		TreeNode<T> rsp = null;
		for (int i = 0; i < children.size(); i++) {
			if (rspInt > children.get(i).getValue() && children.get(i).getChildren().size()==0) {
				rspInt = children.get(i).getValue();
				rsp = children.get(i);
			}
			
			else if(children.get(i).getChildren().size()>0)
			{
				rsp=children.get(i).getLeastValue();
				rspInt=children.get(i).getValue();
			}
		}
		return rsp;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public TreeNode<T> getParent() {
		return parent;
	}

	public void setParent(TreeNode<T> parent) {
		this.parent = parent;
	}

	
}