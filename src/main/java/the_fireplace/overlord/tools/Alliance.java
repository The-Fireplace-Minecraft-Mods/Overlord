package the_fireplace.overlord.tools;

import java.io.Serializable;

/**
 * Serializable two-member Player Pairs. Class named Alliance because that was the initial purpose, and renaming would break existing ones.
 *
 * @author The_Fireplace
 */
public class Alliance implements Serializable {
	private StringPair obj1;
	private StringPair obj2;

	public Alliance(StringPair obj1, StringPair obj2) {
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	public StringPair getUser1() {
		return this.obj1;
	}

	public StringPair getUser2() {
		return this.obj2;
	}

	@Override
	public String toString() {
		return Alliance.class.getName() + '@' + Integer.toHexString(this.hashCode()) + " [" + this.obj1.toString() + ", " + this.obj2.toString() + ']';
	}
}
