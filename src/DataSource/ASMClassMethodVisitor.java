package DataSource;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;

public class ASMClassMethodVisitor extends MethodVisitor {

	private List<String> types;

	public ASMClassMethodVisitor(int api) {
		// TODO Auto-generated constructor stub
		super(api);
		this.types = new ArrayList<>();
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		ASMSignatureVisitor visitor = new ASMSignatureVisitor(Opcodes.ASM7);
		SignatureReader reader = new SignatureReader(desc);
		reader.accept(visitor);
		for (TypeData t : visitor.getTypes()) {
			types.add(t.name);
		}
		ASMSignatureVisitor ownerVisitor = new ASMSignatureVisitor(Opcodes.ASM7);
		SignatureReader ownerReader = new SignatureReader(owner);
		try {
			ownerReader.accept(ownerVisitor);
			for (TypeData t : ownerVisitor.getTypes()) {
				types.add(t.name);
			}
		} catch (IllegalArgumentException e) {
			types.add(owner);
		}
	}

	public List<String> getTypes() {
		return this.types;
	}

}
