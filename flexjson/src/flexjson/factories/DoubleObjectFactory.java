package flexjson.factories;

import flexjson.ObjectFactory;
import flexjson.ObjectBinder;

import java.lang.reflect.Type;

public class DoubleObjectFactory implements ObjectFactory {
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            try {
                return Double.parseDouble(value.toString());
            } catch (Exception e) {
                throw context.cannotConvertValueToTargetType(value, Double.class);
            }
        }
    }
}
