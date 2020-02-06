package DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import Domain.SequenceDiagramArrow;
import Domain.Variable;

public class ASMSequenceDiagramParser implements ISequenceDiagramParser {

	private int depth;
	
	public ASMSequenceDiagramParser(int depth) {
		this.depth = depth;
	}

	@Override
	public List<SequenceDiagramArrow> parse(String name) throws IOException {
		// TODO Auto-generated method stub
		if(depth == 0) {
			return new ArrayList<SequenceDiagramArrow>();
		}
		depth -= 1;
		String[] argumentTypes = name.substring(name.indexOf("(")+1,name.indexOf(")")).split(",");
		if(argumentTypes.length == 1 && argumentTypes[0].isEmpty()) {
			argumentTypes = new String[0];
		}
		String fullName = name.replaceAll("\\(.*?\\) ?", "");
		int p = fullName.lastIndexOf('.');
		String methodName = fullName.substring(p+1);
		String className = fullName.substring(0, p);
		ClassReader reader = new ClassReader(className);
		ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.EXPAND_FRAMES);
		MethodNode methodNode = new MethodNode();
		for(MethodNode m : classNode.methods) {
			if(m.name.equals(methodName)) {
				ArrayList<String> arguments = new ArrayList<>();
				for (Type argType : Type.getArgumentTypes(m.desc)) {
					String argumentType = argType.getClassName();
					arguments.add(argumentType.substring(argumentType.lastIndexOf('.') + 1));
				}
				if(arguments.size() != argumentTypes.length) {
					continue;
				}else {
					boolean isSame = true;
					for(int i = 0; i < argumentTypes.length; i++) {
						if(!argumentTypes[i].equals(arguments.get(i))) {
							isSame = false;
						}
					}
					if(isSame) {
						methodNode = m;
						break;
					}
				}
			}
		}
		
		ArrayList<SequenceDiagramArrow> arrows = new ArrayList<>();
		InsnList instructions = methodNode.instructions;
		for (int i = 0; i < instructions.size(); i++) {
			AbstractInsnNode insn = instructions.get(i);
			if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
				ASMSequenceMethodVisitor methodVisitor = new ASMSequenceMethodVisitor(Opcodes.ASM7);
				insn.accept(methodVisitor);
				String arguments = "";
				if(methodVisitor.getTypes().size() != 0) {
					for(String s : methodVisitor.getTypes()) {
						if(s.contains(".")) {
							arguments+=s.substring(s.lastIndexOf('.') + 1)+",";
						}else {
							arguments+=s + ",";
						}
					}
					arguments = arguments.substring(0, arguments.length() - 1);
				}
				SequenceDiagramArrow callArrow = new SequenceDiagramArrow(Constants.CALL, className, methodVisitor.getOwner(), methodVisitor.getName()+"("+arguments+")", false);
				if(methodVisitor.getName().equals(Constants.CONSTRUCTOR)) {
					callArrow.tag = Constants.CREATE_SYMBOL;
					callArrow.isCreate = true;
					arrows.add(callArrow);
					continue;
				}
				if(!methodVisitor.isItf()) {
					if(checkDIP(callArrow)) {
						callArrow.assignColor(Constants.DIP_VIOLATION_COLOR);
					}
				}
				arrows.add(callArrow);
				String nextMethod = String.format("%s.%s(%s)", methodVisitor.getOwner(), methodVisitor.getName(), arguments);
				arrows.addAll(this.parse(nextMethod));
				arrows.add(new SequenceDiagramArrow(Constants.RETURN, methodVisitor.getOwner(), className, "", false));
			}
		}
		depth += 1;
		return arrows;
	}
	
	private boolean checkDIP(SequenceDiagramArrow in) throws IOException {
		ClassReader reader = new ClassReader(in.pointTo);
		ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.EXPAND_FRAMES);
		if(classNode.superName != null) {
			ClassReader superReader = new ClassReader(classNode.superName);
			ClassNode superClassNode = new ClassNode();
			superReader.accept(superClassNode, ClassReader.EXPAND_FRAMES);
			for(MethodNode m : superClassNode.methods) {
				if(m.name.equals(in.tag.replaceAll("\\(.*?\\) ?", ""))) {
					return true;
				}
			}
		}
		for(String s : classNode.interfaces) {
			ClassReader itfReader = new ClassReader(s);
			ClassNode itfClassNode = new ClassNode();
			itfReader.accept(itfClassNode, ClassReader.EXPAND_FRAMES);
			for(MethodNode m : itfClassNode.methods) {
				if(m.name.equals(in.tag.replaceAll("\\(.*?\\) ?", ""))) {
					return true;
				}
			}
		}
		return false;
	}
}
