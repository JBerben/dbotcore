package org.darkstorm.bcel.deobbers;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.darkstorm.bcel.util.InstructionSearcher;

public class ExceptionTableDeobber extends Deobber {
	private int deletedExceptions;

	public ExceptionTableDeobber(Injector injector) {
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
			InstructionSearcher searcher = new InstructionSearcher(methodGen);
			for(CodeExceptionGen exceptionGen : methodGen
					.getExceptionHandlers()) {
				if(exceptionGen.getCatchType() != null
						&& exceptionGen.getCatchType().getClassName()
								.equals("java.lang.RuntimeException")) {
					InstructionHandle start = exceptionGen.getHandlerPC();
					Instruction startIns = start.getInstruction();
					if(!(startIns instanceof NEW
							&& ((NEW) startIns).getLoadClassType(cpg) != null && ((NEW) startIns)
							.getLoadClassType(cpg).getClassName()
							.equals("java.lang.StringBuilder")))
						continue;
					methodGen.removeExceptionHandler(exceptionGen);
					exceptionGen.setHandlerPC(null);
					searcher.setCurrent(start);
					Instruction instruction;
					while((instruction = searcher.getNext()) != null) {
						if(instruction instanceof ATHROW) {
							if(searcher.getCurrentHandle().hasTargeters())
								searcher.getPrev();
							try {
								iList.delete(start, searcher.getCurrentHandle());
							} catch(TargetLostException exception) {
								System.out.println(exceptionGen.getCatchType());
								exception.printStackTrace();
							}
							deletedExceptions++;
							break;
						}
					}
					break;
				}
			}
			iList.setPositions();
			methodGen.setInstructionList(iList);
			methodGen.removeLineNumbers();
			methodGen.setMaxLocals();
			methodGen.setMaxStack();
			classGen.replaceMethod(m, methodGen.getMethod());
		}
	}

	@Override
	public void finish() {
		System.out.println("Removed " + deletedExceptions
				+ " exception handlers.");
	}
}
