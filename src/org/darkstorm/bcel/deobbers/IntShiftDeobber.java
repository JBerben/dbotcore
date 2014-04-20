package org.darkstorm.bcel.deobbers;

import java.util.Iterator;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.InstructionFinder;
import org.darkstorm.bcel.*;

public class IntShiftDeobber extends Deobber {
	private int fixedShifts;

	public IntShiftDeobber(Injector injector) {
		super(injector);
	}

	@Override
	public void deob(ClassGen classGen) {
		ConstantPoolGen cpg = classGen.getConstantPool();
		for(Method m : classGen.getMethods()) {
			if(m.isAbstract()) {
				continue;
			}
			MethodGen methodGen = new MethodGen(m, classGen.getClassName(), cpg);
			InstructionList iList = methodGen.getInstructionList();
			if(iList == null) {
				continue;
			}
			InstructionFinder instructionFinder = new InstructionFinder(iList);
			for(@SuppressWarnings("unchecked")
			Iterator<InstructionHandle[]> iterator = instructionFinder
					.search("PushInstruction ((ishl)|(ishr))"); iterator
					.hasNext();) {
				InstructionHandle[] ih = iterator.next();
				if(ih[0].getInstruction() instanceof ConstantPushInstruction) {
					ConstantPushInstruction pushInstruction = (ConstantPushInstruction) ih[0]
							.getInstruction();
					int realPush = pushInstruction.getValue().intValue() & 0x1f;
					if(pushInstruction instanceof ICONST) {
						ih[0].setInstruction(new ICONST(realPush));
						// fixedShifts++;
					} else if(pushInstruction instanceof SIPUSH) {
						ih[0].setInstruction(new SIPUSH((short) realPush));
						fixedShifts++;
					} else if(pushInstruction instanceof BIPUSH) {
						ih[0].setInstruction(new BIPUSH((byte) realPush));
						fixedShifts++;
					}
				} else if(ih[0].getInstruction() instanceof LDC) {
					LDC ldc = (LDC) ih[0].getInstruction();
					if(ldc.getValue(cpg) instanceof Integer) {
						int realPush = ((Integer) ldc.getValue(cpg)) & 0x1f;
						ih[0].setInstruction(new LDC(cpg.addInteger(realPush)));
						fixedShifts++;
					} else if(ldc.getValue(cpg) instanceof Long) {
						int realPush = (int) (((Long) ldc.getValue(cpg)) & 0x1f);
						ih[0].setInstruction(new LDC(cpg.addLong(realPush)));
						fixedShifts++;
					}
				}
			}
			methodGen.setInstructionList(iList);
			methodGen.setMaxLocals();
			methodGen.setMaxStack();
			classGen.replaceMethod(m, methodGen.getMethod());
		}
	}

	@Override
	public void finish() {
		System.out.println("Fixed " + fixedShifts + " int shifts.");
	}
}
