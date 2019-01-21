# LinkedList源码分析

## 1.概述

> 1. LinkedList是List接口的一种实现，底层数据结构是链表，和ArrayList比较来说，插入和删除比较快（只需要移动指针即可），查询和修改相对较慢(不支持随机访问)。
>
> 2. LinkedList不是线程安全的，允许元素为null的双向链表，实现接口 
>
>    ```java
>    public class LinkedList<E>
>        extends AbstractSequentialList<E>
>        implements List<E>, Deque<E>, Cloneable, java.io.Serializable
>    ```
>
>    Deque:双端队列，没有实现`RandomAccess`所以其以下标，**随机访问元素速度较慢**。
>
>    空间效率比较高，ArrayList有冗余的的空间。



## 2.源码分析

### 1.构造方法

```java
    //集合元素数量
    transient int size = 0;

    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     *
     *            意思是：集合为空或者
     *            首元素的直接前驱是空并且首元素的值不为空
     *
     */
    //头结点
    transient com.mrl.collection.linedlist.LinkedList.Node<E> first;

    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    //尾节点
    transient com.mrl.collection.linedlist.LinkedList.Node<E> last;

    /**
     * Constructs an empty list.
     *
     * 空构造函数
     *
     */
    public LinkedList() {
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * 指定集合的构造
     *
     * @param  c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public LinkedList(Collection<? extends E> c) {
        this();
        //添加元素,插入链表
        addAll(c);
    }
```

**节点的结构**

```java
    private static class Node<E> {
        //元素
        E item;
        //直接后继
        com.mrl.collection.linedlist.LinkedList.Node<E> next;
        //直接前驱
        com.mrl.collection.linedlist.LinkedList.Node<E> prev;
        //构造
        Node(com.mrl.collection.linedlist.LinkedList.Node<E> prev, E element, com.mrl.collection.linedlist.LinkedList.Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```



### 2.新增

新增一个集合

```java
 /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator.  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in
     * progress.  (Note that this will occur if the specified collection is
     * this list, and it's nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * 以index为下标，插入集合c中所有元素
     *
     * @param index index at which to insert the first element
     *              from the specified collection
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        //检查是否越界 index>=0&&index<=size
        checkPositionIndex(index);
        //转化为数组
        Object[] a = c.toArray();
        //长度
        int numNew = a.length;
        if (numNew == 0)
            return false;

        // //index节点的前置节点，后置节点
        com.mrl.collection.linedlist.LinkedList.Node<E> pred, succ;
        //在链表末尾追加元素
        if (index == size) {
            //队尾节点的后置节点为null
            succ = null;
            //前置节点是队尾
            pred = last;
        } else {
            //取出index节点，作为后置节点
            succ = node(index);
            //前置节点是，index节点的前一个节点
            pred = succ.prev;
        }

        //for循环依次插入节点，而ArrayList是通过数组的拷贝
        for (Object o : a) {
            @SuppressWarnings("unchecked")
            E e = (E) o;
            //以前置节点，元素值，构建一个新节点
            com.mrl.collection.linedlist.LinkedList.Node<E> newNode
                    = new com.mrl.collection.linedlist.LinkedList.Node<>(pred, e, null);
            //前置节点是空，说明是头节点
            if (pred == null)
                //构造的新节点赋值给头节点
                first = newNode;
            else//否则前置节点的后置节点设置为新的节点
                pred.next = newNode;
            //步进，当前的节点设置为前置节点。为下次添加节点做准备
            pred = newNode;
        }

        //循环结束后判断，如果后置节点是null，是在队尾追加的，
        if (succ == null) {
            last = pred;//设置尾节点
        } else {
            //是在队中插入的节点,
            pred.next = succ; //更新前置节点的后置节点
            succ.prev = pred;//更新后置节点的后置节点
        }

        /// 修改数量size
        size += numNew;
        //修改modCount
        modCount++;
        return true;
    }

  /**
     * Returns the (non-null) Node at the specified element index.
     *
     * //根据index查出node
     */
    com.mrl.collection.linedlist.LinkedList.Node<E> node(int index) {
        // assert isElementIndex(index);

        //折半判断index
        if (index < (size >> 1)) {
            com.mrl.collection.linedlist.LinkedList.Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            com.mrl.collection.linedlist.LinkedList.Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

```

> 小结：
>
> 1.链表批量增加是靠for循环依次增加的，ArrayList是靠System.arraycopy批量复制
>
> 2.通过下标获取某个node的时候，会判断index处于前半段还是后半段。提升查询效率

2.插入单个节点Node：

  1.在尾部插入：

```java
  /**
     * Appends the specified element to the end of this list.
     *  尾部插入一个节点
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }
     /**
     * Links e as last element.
     */
    void linkLast(E e) {
        //记录原尾部节点
        final com.mrl.collection.linedlist.LinkedList.Node<E> l = last;
        //以原来的尾部节点为前置节点构造新节点
        final com.mrl.collection.linedlist.LinkedList.Node<E> newNode
                = new com.mrl.collection.linedlist.LinkedList.Node<>(l, e, null);
        //更新尾部节点
        last = newNode;
        if (l == null)
            //如果尾部节点是null，即原来是空链表，额外更新头节点
            first = newNode;
        else
            //更新原尾节点的后置节点为新节点
            l.next = newNode;
        //修改size
        size++;
        //修改modCount
        modCount++;
    }
```

2.在指定下标插入：

```java
  /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *  在指定位置插入一个节点元素
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        //检查是否越界
        checkPositionIndex(index);
        //在末尾追加
        if (index == size)
            linkLast(element);
        else
            //中间插入
            linkBefore(element, node(index));
    }
 /**
     * Inserts element e before non-null Node succ.
     *
     * 在succ节点之前插入一个新节点
     *
     */
    void linkBefore(E e, com.mrl.collection.linedlist.LinkedList.Node<E> succ) {
        // assert succ != null;
        //保存后置节点的前置节点
        final com.mrl.collection.linedlist.LinkedList.Node<E> pred = succ.prev;
        //构建新节点
        final com.mrl.collection.linedlist.LinkedList.Node<E> newNode = new com.mrl.collection.linedlist.LinkedList.Node<>(pred, e, succ);
        //新节点是原succ节点的前置节点
        succ.prev = newNode;
        if (pred == null)//如果之前的前置节点是空,说明succ是原头结点。所以新节点是现在的头结点
            first = newNode;
        else//否则构建前置节点的后置节点为new
            pred.next = newNode;
        size++;
        modCount++;
    }
```

### 3.删除

```java
 /**
     * Removes the element at the specified position in this list.  Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        //检查下标
        checkElementIndex(index);
        //删除节点
        return unlink(node(index));
    }
    /**
     * Unlinks non-null node x.
     */
    E unlink(com.mrl.collection.linedlist.LinkedList.Node<E> x) {
        // assert x != null;
        //当前元素的节点值
        final E element = x.item;
        //当前元素的后置节点
        final com.mrl.collection.linedlist.LinkedList.Node<E> next = x.next;
        //当前元素的前置节点
        final com.mrl.collection.linedlist.LinkedList.Node<E> prev = x.prev;

        //当前元素的前置节点是null，当前节点是头节点
        if (prev == null) {
            //后置节点赋为头节点
            first = next;
        } else {
            //否则，移动指针，前置节点的后置节点的指向后置节点
            prev.next = next;
            x.prev = null;//当前元素的前置节点删除
        }

        //后置节点为空,说明当前是尾节点
        if (next == null) {
            last = prev;
        } else {
            //否则移动指针
            next.prev = prev;
            x.next = null;
        }

        //清空当前元素值
        x.item = null;
        size--;
        modCount++;
        return element;
    }
```

删除指定元素

```java
 /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If this list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *  删除指定元素
     * @param o element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (com.mrl.collection.linedlist.LinkedList.Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (com.mrl.collection.linedlist.LinkedList.Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
```

> 删除会修改modCount,按照下标删，根据index先找到Node,然后去unlink这个node，按照元素删除，会先找链表是否有这个Node，找到去unlink。

### 4.修改

```
  /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *  修改指定位置上的元素
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
        checkElementIndex(index);
        com.mrl.collection.linedlist.LinkedList.Node<E> x = node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }
```



### 5.查询

```java
   /**
     * Returns the element at the specified position in this list.
     *  得到元素
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        checkElementIndex(index);
        //
        return node(index).item;
    }
```

根据对象找到下标：

```java

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *  根据节点对象，查询下标
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (com.mrl.collection.linedlist.LinkedList.Node<E> x = first; x != null; x = x.next) {
                if (x.item == null)
                    return index;
                index++;
            }
        } else {
            for (com.mrl.collection.linedlist.LinkedList.Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }
```

从尾部找：

```java
 /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * 从尾部查找
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (com.mrl.collection.linedlist.LinkedList.Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (x.item == null)
                    return index;
            }
        } else {
            for (com.mrl.collection.linedlist.LinkedList.Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }
```

### 6.转为数组

````java
  /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list
     *         in proper sequence
     */
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (com.mrl.collection.linedlist.LinkedList.Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }
````

### 7.遍历

for循环

```java
 for (int i = 0; i < linkedList2.size(); i++) {
            System.out.println(linkedList2.get(i));
        }
```

foreach(迭代器实现)

```java
 for (Object o : linkedList2) {
            System.out.println(o);
        }
```



迭代器：

```java
 for(Iterator iterator = linkedList2.iterator();iterator.hasNext();){
            System.out.println(iterator.next());
        }
```



```java
 /**
     * Returns a list-iterator of the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * Obeys the general contract of {@code List.listIterator(int)}.<p>
     *
     * The list-iterator is <i>fail-fast</i>: if the list is structurally
     * modified at any time after the Iterator is created, in any way except
     * through the list-iterator's own {@code remove} or {@code add}
     * methods, the list-iterator will throw a
     * {@code ConcurrentModificationException}.  Thus, in the face of
     * concurrent modification, the iterator fails quickly and cleanly, rather
     * than risking arbitrary, non-deterministic behavior at an undetermined
     * time in the future.
     *
     * @param index index of the first element to be returned from the
     *              list-iterator (by a call to {@code next})
     * @return a ListIterator of the elements in this list (in proper
     *         sequence), starting at the specified position in the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see List#listIterator(int)
     */
    public ListIterator<E> listIterator(int index) {
        //从零开始
        checkPositionIndex(index);
        //返回迭代器,可以指定index，如果是iterator(),从0开始
        return new com.mrl.collection.linedlist.LinkedList.ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        //上一次返回的节点
        private com.mrl.collection.linedlist.LinkedList.Node<E> lastReturned;
        //要返回的下一个元素的节点
        private com.mrl.collection.linedlist.LinkedList.Node<E> next;
        //要返回的下一个元素的索引
        private int nextIndex;
        //modCount，并发访问异常
        private int expectedModCount = modCount;

        ListItr(int index) {
            // assert isPositionIndex(index);
            //索引==size，next为空，否则找到当前索引的节点
            next = (index == size) ? null : node(index);
            //下一个索引置为索引
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public E next() {
            //检查并发访问异常
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();
            //要返回的值赋值给lastReturned
            lastReturned = next;
            //移动指针，向后移动一位
            next = next.next;
            //索引++
            nextIndex++;
            //返回
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            //检查
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();
            //next如果是null，则是尾节点。lastReturned赋值为尾节点
            //否则lastReturned为next的prev节点
            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;//索引--
            return lastReturned.item;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();
            //上次返回的节点后面的节点做个备份
            com.mrl.collection.linedlist.LinkedList.Node<E> lastNext = lastReturned.next;
            //释放节点
            unlink(lastReturned);
            //这个条件不会走
            if (next == lastReturned)
                next = lastNext;
            else
                //索引--
                nextIndex--;
            //删除
            lastReturned = null;
            expectedModCount++;
        }

        public void set(E e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }

        public void add(E e) {
            checkForComodification();
            lastReturned = null;
            //尾部追加
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

```

> 总结：
>
> LinekedList的遍历一定不能用for循环遍历，速度太慢，用foreach或者迭代器。



## 3.总结

> 1. LinkedList批量增加用的是for循环遍历数组，依次插入。
> 2. 通过下标获取Node，根据index处于size的那一半进行查找。
> 3. 删除，按照下标删，根据index找到Node，然后去LinkedList里面unlink这个节点。按照元素删除，先去遍历链表是否存在在Node，如果有就去unlink.
> 4. 改,根据index找到Node，unlink
> 5. 查，根据index找到Node