package wackyTracky.clientbindings.java;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;

public class ObjectFieldSerializer {
	public static ObjectFieldSerializer with(Object o) {
		return new ObjectFieldSerializer(o);
	}

	private final Object instance;
	private final Class<?> clazz;

	private final Vector<Field> fields = new Vector<Field>();

	public ObjectFieldSerializer(Class<?> clazz) {
		this(clazz, null);
	}

	public ObjectFieldSerializer(Class<?> clazz, Object instance) {
		this.clazz = clazz;
		this.instance = instance;

		this.includeAllFields();
	}

	public ObjectFieldSerializer(Object instance) {
		this(instance.getClass(), instance);
	}

	public ObjectFieldSerializer include(String... toInclude) {
		Vector<Field> bkp = new Vector<Field>();
		bkp.addAll(this.fields);

		for (String fieldName : toInclude) {
			for (Field f : this.fields) {
				if (f.getName().equals(fieldName)) {
					if (!bkp.contains(f)) {
						bkp.add(f);
					}
				}
			}
		}

		this.fields.removeAllElements();
		this.fields.addAll(bkp);

		return this;
	}

	public ObjectFieldSerializer includeAllFields() {
		for (Field f : this.clazz.getFields()) {
			if (Modifier.isTransient(f.getModifiers())) {
				continue;
			}

			this.fields.add(f);
		}

		return this;
	}

	@Override
	public String toString() {
		String ret = "  ";

		for (Field f : this.fields) {
			try {
				String v;
				Object o = f.get(this.instance);

				if (o == null) {
					v = "<null>";
				} else {
					v = o.toString();
				}

				ret += f.getName() + "=" + v + ", ";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ret = "{" + ret.substring(0, ret.length() - 2).trim() + "}";

		return ret;
	}
}
