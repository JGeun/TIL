import java.util.ArrayList;

class MyStack<T> {
    private ArrayList<T> list;

    public MyStack() {
        list = new ArrayList<>();
    }

    public void push(T item) {
        list.add(item);
    }

    public T peek() {
        try {
            return list.get(list.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("현재 배열이 비어있음");
            return null;
        }
    }

    public T pop() {
        try {
            return list.remove(list.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("현재 배열이 비어있음");
            return null;
        }
    }

    public int size() {
        return list.size();
    }
}