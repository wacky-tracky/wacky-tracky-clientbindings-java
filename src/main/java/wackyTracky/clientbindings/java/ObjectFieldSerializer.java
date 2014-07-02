package wackyTracky.clientbindings.java;

import java.lang.reflect.Field;
import java.util.Vector;

public class ObjectFieldSerializer {
	public static ObjectFieldSerializer with(Object o) {
		return new ObjectFieldSerializer(o);
	}

	private final Object subject;

	private final Vector<Field> fields = new Vector<Field>();

	public ObjectFieldSerializer(Object subject) {
		this.subject = subject;

		this.includeAllFields();
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

	public void includeAllFields() {
		for (Field f : this.subject.getClass().getFields()) {
			this.fields.add(f);
		}
	}

	@Override
	public String toString() {
		String ret = "  ";

		for (Field f : this.fields) {
			try {
				String v;
				Object o = f.get(this.subject);

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
