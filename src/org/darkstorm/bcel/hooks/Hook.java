package org.darkstorm.bcel.hooks;

import java.util.*;

import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.jdom.Element;

public abstract class Hook {
	private static Map<String, Map<Integer, Integer>> positionUpdates = new HashMap<String, Map<Integer, Integer>>();

	public Hook() {
	}

	public Hook(Element element) {
		fromXML(element);
	}

	public abstract String getClassName();

	public abstract String getInterfaceName();

	public abstract Element toXML();

	public abstract boolean hasSameTargetAs(Hook hook);

	protected abstract void fromXML(Element element);

	public abstract ClassGen generateInterface(ClassGen classGen);

	public abstract boolean isInjectable(Injector injector, ClassGen classGen);

	public abstract void inject(Injector injector, ClassGen classGen);

	protected void updateMethodPositions(MethodGen method, int position,
			int offset) {
		String key = method.getClassName() + "." + method.getName()
				+ method.getSignature();
		Map<Integer, Integer> positions = positionUpdates.get(key);
		Integer zero = Integer.valueOf(0);
		if(positions == null) {
			positions = new HashMap<Integer, Integer>();
			for(int originalPosition : method.getInstructionList()
					.getInstructionPositions())
				positions.put(originalPosition, zero);
			positionUpdates.put(key, positions);
		}
		Integer originalOffset = positions.get(position);
		if(originalOffset == null)
			originalOffset = zero;
		positions.put(position, originalOffset + offset);
	}

	protected int getOffsetAtPosition(MethodGen method, int position) {
		String key = method.getClassName() + "." + method.getName()
				+ method.getSignature();
		Map<Integer, Integer> positions = positionUpdates.get(key);
		if(positions == null)
			return 0;
		int offset = 0;
		for(Integer pos : positions.keySet()) {
			if(pos.intValue() > position)
				continue;
			offset += positions.get(pos).intValue();
		}
		return offset;
	}
}