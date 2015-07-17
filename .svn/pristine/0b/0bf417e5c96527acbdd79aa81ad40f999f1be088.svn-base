package flexjson;

import flexjson.transformer.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class TransformerUtil {

    private static final TypeTransformerMap defaultTransformers = new TypeTransformerMap();

    static {
        // define all standard type transformers
        Transformer transformer = new NullTransformer();
        defaultTransformers.put(null, new TransformerWrapper(transformer));

        transformer = new ObjectTransformer();
        defaultTransformers.put(Object.class, new TransformerWrapper(transformer));

        transformer = new ClassTransformer();
        defaultTransformers.put(Class.class, new TransformerWrapper(transformer));

        transformer = new BooleanTransformer();
        defaultTransformers.put(boolean.class, new TransformerWrapper(transformer));
        defaultTransformers.put(Boolean.class, new TransformerWrapper(transformer));

        transformer = new NumberTransformer();
        defaultTransformers.put(Number.class, new TransformerWrapper(transformer));

        defaultTransformers.put(Integer.class, new TransformerWrapper(transformer));
        defaultTransformers.put(int.class, new TransformerWrapper(transformer));

        defaultTransformers.put(Long.class, new TransformerWrapper(transformer));
        defaultTransformers.put(long.class, new TransformerWrapper(transformer));

        defaultTransformers.put(Double.class, new TransformerWrapper(transformer));
        defaultTransformers.put(double.class, new TransformerWrapper(transformer));

        defaultTransformers.put(Float.class, new TransformerWrapper(transformer));
        defaultTransformers.put(float.class, new TransformerWrapper(transformer));

        defaultTransformers.put(BigDecimal.class, new TransformerWrapper(transformer));
        defaultTransformers.put(BigInteger.class, new TransformerWrapper(transformer));

        transformer = new StringTransformer();
        defaultTransformers.put(String.class, new TransformerWrapper(transformer));

        transformer = new CharacterTransformer();
        defaultTransformers.put(Character.class, new TransformerWrapper(transformer));
        defaultTransformers.put(char.class, new TransformerWrapper(transformer));

        transformer = new BasicDateTransformer();
        defaultTransformers.put(Date.class, new TransformerWrapper(transformer));

        transformer = new EnumTransformer();
        defaultTransformers.put(Enum.class, new TransformerWrapper(transformer));

        transformer = new IterableTransformer();
        defaultTransformers.put(Iterable.class, new TransformerWrapper(transformer));

        transformer = new MapTransformer();
        defaultTransformers.put(Map.class, new TransformerWrapper(transformer));

        transformer = new NullTransformer();
        defaultTransformers.put(void.class, new TransformerWrapper(transformer));

        transformer = new ArrayTransformer();
        defaultTransformers.put(Arrays.class, new TransformerWrapper(transformer));

        try {
            Class hibernateProxy = Class.forName("org.hibernate.proxy.HibernateProxy");
            defaultTransformers.put(hibernateProxy, new TransformerWrapper(new HibernateTransformer()));
        } catch (ClassNotFoundException ex) {
            // no hibernate so ignore.
        }


        Collections.unmodifiableMap(defaultTransformers);
    }

    public static TypeTransformerMap getDefaultTypeTransformers() {
        return defaultTransformers;
    }

}
