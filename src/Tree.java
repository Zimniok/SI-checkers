import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tree<T> {
    private Node<T> root;

    public Tree(T rootData) {
        root = new Node<T>();
        root.data = rootData;
        root.children = new ArrayList<Node<T>>();
    }

    public Tree(Node<T> rootData) {
        root = rootData;
        root.children = rootData.children;
    }

    public static class Node<T> {
        public T data;
        private Node<T> parent;
        private List<Node<T>> children;

        public Node(){
            this.children = new ArrayList<>();
        }

        public void addChild(Node<T> child){
            this.children.add(child);
            child.parent = this;
        }
        public List<Node<T>> getChildren() { return this.children; }
        public Node<T> getParent() { return this.parent; }
    }

    public void addChild(Node<T> child){
        root.children.add(child);
        child.parent = root;
    }

    public Node<T> getNode(){
        return this.root;
    }

    public List<Node<T>> getChildren(){
        return root.children;
    }

    public int calculateDepthFromNode(Node<T> startNode){
        if(startNode.children.size() == 0){
            return 1;
        }
        ArrayList<Integer> depths = new ArrayList<>();
        for (int i = 0; i < startNode.children.size(); i++){
            depths.add(calculateDepthFromNode(startNode.children.get(i)));
        }
        return Collections.max(depths) + 1;
    }
}