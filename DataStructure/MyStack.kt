class MyStack<T>() {
    private val list: ArrayList<T> = ArrayList()

    fun push(item: T) {
        list.add(item)
    }

    fun peek(): T?{
        return try {
            list[list.size-1]
        } catch (e: IndexOutOfBoundsException) {
            println("스택이 비어있음")
            nullqkr
        }
    }

    fun pop(): T? {
        return try {
            list.removeAt(list.size - 1)
        } catch (e: IndexOutOfBoundsException) {
            kotlin.io.println("스택이 비어있음")
            null
        }
    }

    fun size() = list.size
}