# ArrayList源码阅读 #

## 1.是什么 ##
> 1.ArrayList是Java集合框架体系中的一个实现类，用来存储对象实例。

> 2.数组也可以存储对象，为什么还要ArrayList呢？数组的长度是不可变的，而ArrayList是可变长度的。在不可预知的一堆对象中，用ArrayList来存储就比较合适了。

> 3.ArrayList里面是怎么实现的呢？是一个对象数组,而且长度是可变的。 
> ``transient Object[] elementData;``

## 2.源码分析 ##

> 从构造方法，和对象的增加，删除，修改，查找，遍历等维度来分析。

### 1.构造方法 ###

> 1.无参构造  

```java
 /**
     * Constructs an empty list with an initial capacity of ten.
     构造一个初始化容量为10的空集合
     */
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
```

> 2.有参构造器

```java
 /**
     * Constructs an empty list with the specified initial capacity.
     * 构造一个指定初始化容量的空集合
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }
```



```java
 /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
       构造一个集合，包含指定的集合，他们按照集合的顺序返回集合的迭代器
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public ArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // 这个是jdk的bug
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // replace with empty array.
            this.elementData = EMPTY_ELEMENTDATA;
        }
    } 
```



> 小总结：
>
> ArrayList的构造，如果知道集合的大小，最好手动指定初始化容量，要不然默认是10，扩容1.5部，扩容的过程中需要进行数组的拷贝，比较耗时。影响性能。
>
> **遗留问题：**
>
> - RandomAccess？Cloneable？Serializable实现的这几个接口的作用？
> - 数组的修饰符transient的含义？





### 2.增加元素

1.在末尾新增一个

```java
 /**
     * Appends the specified element to the end of this list.
     * 在集合末尾追加元素
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
    // 确保数组的容量够用,方法实现
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        //赋值
        elementData[size++] = e;
        //新增成功
        return true;
    }
    
    
     private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        //初始化
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
         //确保容量够用
        ensureExplicitCapacity(minCapacity);
    }

 private void ensureExplicitCapacity(int minCapacity) {
        modCount++;//这个变量表示的是集合修改的次数

        // overflow-conscious code  扩容方法
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

 /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *  扩容方法
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        //扩容1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        //将原来的数组内容拷贝到新数组中 底层调用  
        //System.arraycopy(original, 0, copy, 0,
          //               Math.min(original.length, newLength));
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

// 处理最大容量
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
```

2.在指定位置新增一个

```java
/**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *  
      在指定位置新增元素，如果原来位置上存在元素，则依次向后移动一位
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        //检查添加的位置，位置大于集合的大小或者小于0，则会抛出异常
        rangeCheckForAdd(index);
        //确保容量，新增修改次数modcount,(如果扩容的话，将原来的数组拷贝到新数组中)
        ensureCapacityInternal(size + 1);  // Increments modCount!!
  	 	//将插入的元素位置之后的元素向后移动一位
        //底层方法，将elementData数组从index位置开始拷贝到elementData的inedx+1位置开始，拷贝
        //的长度是size-index
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        //将数值赋值给指定的位置
        elementData[index] = element;
        //数组的长度+1
        size++;
    }
```

3.在末尾新增一个集合

```java
 /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
//在集合的后面追加指定的集合，按照集合迭代的顺序，
    public boolean addAll(Collection<? extends E> c) {
        //新增的集合转为数组
        Object[] a = c.toArray();
        int numNew = a.length;
        //确保容量够用,modCount自增1，否则扩容，
        ensureCapacityInternal(size + numNew);  // Increments modCount
        //拷贝数组，将传过来的集合加到原集合后面
        System.arraycopy(a, 0, elementData, size, numNew);
        //数组大小修改
        size += numNew;
        return numNew != 0;
    }
```

4.在指定位置新增集合

```java
 /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        //检查新增位置
        rangeCheckForAdd(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        //确保容量够用
        ensureCapacityInternal(size + numNew);  // Increments modCount

        //需要移动的元素的个数
        int numMoved = size - index;
        if (numMoved > 0)
            //将需要移动的元素拷贝到指定的位置(为新插入的元素腾个空)
            System.arraycopy(elementData, index, elementData, index + numNew,
                             numMoved);
        //将新增加的元素拷贝到指定的位置
        System.arraycopy(a, 0, elementData, index, numNew);
        //修改大小
        size += numNew;
        return numNew != 0;
    }
```

> 小总结:
>
> 新增的方法有两个，一个是在末尾新增，一个是在指定位置新增。
>
> 新增一般都会做下面几件事：
>
> 1.确保当前数组的容量是否足够新增，不够的话就需要扩容，把老数组的数据拷贝到新数组中
>
> 2.判断插入的位置是否合法（<0或者>数组的大小均不合法(数组是连续的，大于数组大小，数组就断裂了)
>
> 3.数组的拷贝(在指定位置新增)
>
> 4.将数值赋值到指定位置。长度+1
>
> **每次新增数据的时候，都会将modCount的值自增1**
>
> modCount是ArrayList的父类AbstractList定义的一个属性
>
> **遗留问题：**
>
> - modCount的作用？为什么要自增1



### 3.删除元素

1.删除指定位置上的一个元素

```java
 /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
//删除指定位置上的元素
    public E remove(int index) {
        //检查删除的位置，不能是>=size的位置
        rangeCheck(index);
        //modCount自增
        modCount++;
        //获取指定位置上的元素
        E oldValue = elementData(index);
        //需要移动元素的个数
        int numMoved = size - index - 1;
        if (numMoved > 0)
            //将需要移动的元素移动到指定的位置上
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        //清除最后一个元素的值，不引用让gc去回收这个对象
        elementData[--size] = null; // clear to let GC do its work
        //返回删除的对象
        return oldValue;
    }
```

2.删除指定元素：

```java
 /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
//删除指定元素（删除第一个出现的)
    public boolean remove(Object o) {
        //元素是null
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }


 /*
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     */
//私有的删除方法，跳过边界检查不返回删除的值   
private void fastRemove(int index) {
    //modCount自增
        modCount++;
    //需要移动的元素个数
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }

```

3.删除指定范围的数据

```java
 /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex} or
     *         {@code toIndex} is out of range
     *         ({@code fromIndex < 0 ||
     *          fromIndex >= size() ||
     *          toIndex > size() ||
     *          toIndex < fromIndex})
     */
//删除指定返回的元素 [} 左闭右开
    protected void removeRange(int fromIndex, int toIndex) {
        //自增
        modCount++;
        //需要移动的元素个数
        int numMoved = size - toIndex;
        //移动数据
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                         numMoved);
        
        // clear to let GC do its work
        //需要清除的元素的起始位置
        int newSize = size - (toIndex-fromIndex);
        for (int i = newSize; i < size; i++) {
            elementData[i] = null;
        }
        size = newSize;
    }
```



4.删除指定的集合

```java
  /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param c collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     */
//移除集合中包含的指定集合的元素
    public boolean removeAll(Collection<?> c) {
        //判空，集合为空指针异常
        Objects.requireNonNull(c);
        return batchRemove(c, false);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     */
////保留集合中包含的指定集合的元素
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, true);
    }



//批量删除
 private boolean batchRemove(Collection<?> c, boolean complement) {
        final Object[] elementData = this.elementData;
     //w 代表批量删除后 数组还剩多少元素
        int r = 0, w = 0;
     //是否修改
        boolean modified = false;
        try {
            for (; r < size; r++)
                //将需要保留的元素存起来
                if (c.contains(elementData[r]) == complement)
                    elementData[w++] = elementData[r];
        } finally {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            // c.contains(elementData[r])可能报错，报错的话，r的值就和size不一致了，
            // 这样的话前面的值还是正确的，将剩余数组的值拷贝到新数组中
            if (r != size) {
                System.arraycopy(elementData, r,
                                 elementData, w,
                                 size - r);
                //给w赋值
                w += size - r;
            }
            if (w != size) {
                // clear to let GC do its work
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                //modCount修改
                modCount += size - w;
                //现在数组的大小
                size = w;
                modified = true;
            }
        }
  		//w==size的话，说明全部保留，返回false
        return modified;
    }



```



5.删除所有

```java
  /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
//清空集合
    public void clear() {
        modCount++;

        // clear to let GC do its work
        for (int i = 0; i < size; i++)
            elementData[i] = null;
        size = 0;
    }
```

**小总结：**

> 1.删除元素必然会修改modCount
>
> 2.删除基本都有数组的复制，效率低



### 4.修改元素

1.修改指定位置上的元素：

```java
  /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
     	//检查下标
        rangeCheck(index);
        
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }
//不会修改modCount

```



### 5.查询

1.查询指定位置元素

```java
 /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
//效率极高
    public E get(int index) {
     	
        rangeCheck(index);

        return elementData(index);
    }

```



### 6.包含

```java
 public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                //使用的equals
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
```



### 7.遍历

1.for循环遍历

```java
//手动指定循环条件，效率蛮高
for (int i = 0; i < list1.size(); i++) {
            System.out.println(list1.get(i));
        }
```

2.迭代器遍历

```java
 for(Iterator<String> iterator = list1.iterator();iterator.hasNext();){
           iterator.next(); 
         }
```

ArrayList迭代器原理：

```java
 /**
     * An optimized version of AbstractList.Itr
     */
    private class Itr implements Iterator<E> {
        //要返回的下一个元素的位置，默认是0
        int cursor;       // index of next element to return  
      	//上一次返回的元素 (删除的标志位)
        int lastRet = -1; // index of last element returned; -1 if no such 
      	//用于判断集合是否修改过结构的 标志
        int expectedModCount = modCount;

        //游标是否移动至末尾
        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            //检查并发访问异常
            checkForComodification();
            int i = cursor;
            //判断是否越界
            if (i >= size)
                throw new NoSuchElementException();
            //当前数组
            Object[] elementData = ArrayList.this.elementData;
            ////再次判断是否越界，在 我们这里的操作时，有异步线程修改了List
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            //游标+1
            cursor = i + 1;
            //返回元素，并设置上一次返回元素的下标
            return (E) elementData[lastRet = i];
        }

        //remove掉上一次next的元素
        public void remove() {
            //是否next过
            if (lastRet < 0)
                throw new IllegalStateException();
           	//是否修改过
            checkForComodification();

            try {
                //删除元素 remove方法内会修改 modCount 所以后面要更新Iterator里的这个标志值
                ArrayList.this.remove(lastRet);
                //由于cursor比lastRet大1，所有这行代码是指指针往回移动一位
                cursor = lastRet;
               //将最后一个元素返回的索引重置为-1
                lastRet = -1;
         //重新设置了expectedModCount的值，避免了ConcurrentModificationException的产生
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> consumer) {
            Objects.requireNonNull(consumer);
            final int size = ArrayList.this.size;
            int i = cursor;
            if (i >= size) {
                return;
            }
            final Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            while (i != size && modCount == expectedModCount) {
                consumer.accept((E) elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            cursor = i;
            lastRet = i - 1;
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }
```

4.for each:内部也是迭代器

```java
 for (String s : list1) {
            
        }
```

4.Lambda表达式:底层也是靠迭代器实现的

```
 list1.forEach(c->{
            
        });
```

**小总结：**

> 对于ArrayList来说，普通的for循环和迭代器实现的遍历效率差不多，因为读取元素都是直接索引，复杂度是O(1),对于LinkedList来说，迭代器就比较好了。后面看LinkedList源码再仔细看。



### 8.subList

```java

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations.
     *
     * <p>This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>
     *      list.subList(from, to).clear();
     * </pre>
     * Similar idioms may be constructed for {@link #indexOf(Object)} and
     * {@link #lastIndexOf(Object)}, and all of the algorithms in the
     * {@link Collections} class can be applied to a subList.
     *
     * <p>The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
//返回子集合,左闭右开
public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList(this, 0, fromIndex, toIndex);
    }

//检查下标
    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
    }

//SubList
    private class SubList extends AbstractList<E> implements RandomAccess {
       //父集合
        private final AbstractList<E> parent;
        private final int parentOffset;
        private final int offset;
        int size;

        SubList(AbstractList<E> parent,
                int offset, int fromIndex, int toIndex) {
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = ArrayList.this.modCount;
        }

        public E set(int index, E e) {
            rangeCheck(index);
            checkForComodification();
            E oldValue = ArrayList.this.elementData(offset + index);
            ArrayList.this.elementData[offset + index] = e;
            return oldValue;
        }

        public E get(int index) {
            rangeCheck(index);
            checkForComodification();
            return ArrayList.this.elementData(offset + index);
        }

        public int size() {
            checkForComodification();
            return this.size;
        }

        public void add(int index, E e) {
            rangeCheckForAdd(index);
            checkForComodification();
            parent.add(parentOffset + index, e);
            this.modCount = parent.modCount;
            this.size++;
        }

        public E remove(int index) {
            rangeCheck(index);
            checkForComodification();
            E result = parent.remove(parentOffset + index);
            this.modCount = parent.modCount;
            this.size--;
            return result;
        }

        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            parent.removeRange(parentOffset + fromIndex,
                               parentOffset + toIndex);
            this.modCount = parent.modCount;
            this.size -= toIndex - fromIndex;
        }

        public boolean addAll(Collection<? extends E> c) {
            return addAll(this.size, c);
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize==0)
                return false;

            checkForComodification();
            parent.addAll(parentOffset + index, c);
            this.modCount = parent.modCount;
            this.size += cSize;
            return true;
        }

        public Iterator<E> iterator() {
            return listIterator();
        }

        public ListIterator<E> listIterator(final int index) {
            checkForComodification();
            rangeCheckForAdd(index);
            final int offset = this.offset;

            return new ListIterator<E>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = ArrayList.this.modCount;

                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }

                @SuppressWarnings("unchecked")
                public E next() {
                    checkForComodification();
                    int i = cursor;
                    if (i >= SubList.this.size)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return (E) elementData[offset + (lastRet = i)];
                }

                public boolean hasPrevious() {
                    return cursor != 0;
                }

                @SuppressWarnings("unchecked")
                public E previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return (E) elementData[offset + (lastRet = i)];
                }

                @SuppressWarnings("unchecked")
                public void forEachRemaining(Consumer<? super E> consumer) {
                    Objects.requireNonNull(consumer);
                    final int size = SubList.this.size;
                    int i = cursor;
                    if (i >= size) {
                        return;
                    }
                    final Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length) {
                        throw new ConcurrentModificationException();
                    }
                    while (i != size && modCount == expectedModCount) {
                        consumer.accept((E) elementData[offset + (i++)]);
                    }
                    // update once at end of iteration to reduce heap write traffic
                    lastRet = cursor = i;
                    checkForComodification();
                }

                public int nextIndex() {
                    return cursor;
                }

                public int previousIndex() {
                    return cursor - 1;
                }

                public void remove() {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = ArrayList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void set(E e) {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        ArrayList.this.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void add(E e) {
                    checkForComodification();

                    try {
                        int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = ArrayList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                final void checkForComodification() {
                    if (expectedModCount != ArrayList.this.modCount)
                        throw new ConcurrentModificationException();
                }
            };
        }

        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList(this, offset, fromIndex, toIndex);
        }

        private void rangeCheck(int index) {
            if (index < 0 || index >= this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+this.size;
        }

        private void checkForComodification() {
            if (ArrayList.this.modCount != this.modCount)
                throw new ConcurrentModificationException();
        }

        public Spliterator<E> spliterator() {
            checkForComodification();
            return new ArrayListSpliterator<E>(ArrayList.this, offset,
                                               offset + this.size, this.modCount);
        }
    }
```





**总结**

> ArrayList内部由数组实现，对增删的效率相对比较低（因为要进行数组的拷贝),而查询和更新的速度很快，直接索引到相应的位置即可、（删除插入可以用LinkindList）
>
> 底层数组实现，使用默认构造方法初始化出来的容量是10。
>
> 扩容的长度是原来的1.5倍。
>
> 实现了RandomAccess接口，底层又是数组，get读取元素性能很好。
>
> 线程不安全。
>
> 1. ArrayList是线程不安全的，Vector是线程安全的
>
> 2. 扩容时候ArrayList扩0.5倍，Vector扩1倍
>
>    ArrayList有没有办法线程安全？
>
>    Collections工具类有一个synchronizedList方法
>
>    可以把list变为线程安全的集合，但是意义不大，因为可以使用Vector





问题解答：

> 1.为什么ArrayList的elementData是用transient修饰的？
>
> transient修饰的属性意味着不会被序列化，也就是说在序列化ArrayList的时候，不序列化elementData。
>
> 为什么要这么做呢？
>
> 1. elementData不总是满的，每次都序列化，会浪费时间和空间。
> 2. 重写了writeObject  保证序列化的时候虽然不序列化全部 但是有的元素都序列化。

> 2.实现RandomAccess表示可以支持任意访问，通过索引访问。
>
> 3.实现实现了 `Cloneable` 接口，以指示 `Object.clone()` 方法可以合法地对该类实例进行按字段复制。 如果在没有实现 `Cloneable` 接口的实例上调用 `Object` 的 `clone` 方法，则会导致抛出 `CloneNotSupportedException` 异常。
>
> 4.类通过实现 `java.io.Serializable` 接口以启用其序列化功能。未实现此接口的类将无法使其任何状态序列化或反序列化。

