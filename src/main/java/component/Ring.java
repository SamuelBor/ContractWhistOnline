package component;

import java.util.Iterator;

/*  Ring class used in order to implement a cyclic queue
to serve the players one by one in "order" around the "table"
Code used from
https://stackoverflow.com/a/23666610
*/
public class Ring<T> implements Iterable<T> {
    // Source of all Iterators.
    final Iterable<T> it;
    // Current Iterator we are consuming.
    Iterator<T> i;

    public Ring(Iterable<T> it) {
        this.it = it;
        i = it.iterator();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T> () {

            @Override
            public boolean hasNext() {
                // There's always a next for a Ring.
                return true;
            }

            @Override
            public T next() {
                if ( !i.hasNext() ) {
                    // Iterator is exhausted - make a new one!
                    i = it.iterator();
                }
                return i.next();
            }

        };
    }
}
