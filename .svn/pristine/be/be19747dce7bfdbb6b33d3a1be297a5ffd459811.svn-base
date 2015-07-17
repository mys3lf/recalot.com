package flexjson.factories;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

import java.util.Collection;
import java.util.ArrayList;
import java.lang.reflect.Type;

public class ListObjectFactory implements ObjectFactory {
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if( value instanceof Collection) {
            return context.bindIntoCollection((Collection)value, new ArrayList(), targetType);
        } else {
            ArrayList<Object> set = new ArrayList<Object>();
            set.add( context.bind( value ) );
            return set;
        }
    }
}
