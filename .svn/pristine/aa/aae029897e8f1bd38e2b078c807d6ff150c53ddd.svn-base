package flexjson.factories;

import flexjson.ObjectFactory;
import flexjson.ObjectBinder;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.HashMap;

public class MapObjectFactory implements ObjectFactory {
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if( targetType != null ) {
            if( targetType instanceof ParameterizedType ) {
                ParameterizedType ptype = (ParameterizedType) targetType;
                return context.bindIntoMap( (Map)value, new HashMap<Object,Object>(), ptype.getActualTypeArguments()[0], ptype.getActualTypeArguments()[1] );
            }
        }
        return context.bindIntoMap( (Map)value, new HashMap<Object,Object>(), null, null );
    }
}
